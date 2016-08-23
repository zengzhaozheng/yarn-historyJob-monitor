/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kugou.opentsdb;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * OpenTSDB 2.0 jersey based REST client
 *
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
public class OpenTsdb {

    public static final int CONN_TIMEOUT_DEFAULT_MS = 2000;
    private static final int CONNECTION_REQUEST_TIMEOUT_DEFAULT_MS = 2000;
    private static final int MAX_CONNECTIONS_TOTAL = 500;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 1;

    //private static final String API_PUT = "/api/put?summary";
    private static final String API_PUT = "/api/put?details";
    private static final Logger logger = LoggerFactory.getLogger(OpenTsdb.class);

    /**
     * Initiate a client Builder with the provided base opentsdb server url.
     *
     * @param hostname
     * @param port
     * @return
     */
    public static Builder forService(String hostname, int port) {
        return new Builder(hostname, port);
    }

    private final CloseableHttpClient client;
    private final HttpHost host;
    private int batchSizeLimit;
    private int connectionTimeout;
    private int connectionRequestTimeout;

    public static class Builder {
        private Integer connectionTimeout = CONN_TIMEOUT_DEFAULT_MS;
        private Integer connectionRequestTimeout = CONNECTION_REQUEST_TIMEOUT_DEFAULT_MS;
        private String hostname;
        private int port;
        private int batchSizeLimit;

        public Builder(String hostname, int port) {
            this.hostname = hostname;
            this.port = port;
        }

        public Builder withConnectTimeout(Integer connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder withConnectionRequestTimeout(Integer connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
            return this;
        }

        public Builder withBatchSizeLimit(int batchSizeLimit) {
            this.batchSizeLimit = batchSizeLimit;
            return this;
        }

        public OpenTsdb create() {
            return new OpenTsdb(hostname, port, connectionTimeout, connectionRequestTimeout, batchSizeLimit);
        }
    }

    private OpenTsdb(String hostname, int port, int connectionTimeout, int connectionRequestTimeout, int batchSizeLimit) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        cm.setMaxTotal(MAX_CONNECTIONS_TOTAL);
        client = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(config)
                .build();
        host = new HttpHost(hostname, port);
        this.batchSizeLimit = batchSizeLimit;
        this.connectionTimeout = connectionTimeout;
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    /**
     * Send a metric to opentsdb
     *
     * @param metric
     */
    public void send(Object metric) {
        send(Collections.singleton(metric));
    }

    public void send(String metricsJson) {
        sendHelper(metricsJson);
    }

    /**
     * send a set of metrics to opentsdb
     *
     * @param metrics
     */
    public void send(Collection<OpenTsdbMetric> metrics) {
        // we set the patch size because of existing issue in opentsdb where large batch of metrics failed
        // see at https://groups.google.com/forum/#!topic/opentsdb/U-0ak_v8qu0
        // we recommend batch size of 5 - 10 will be safer
        // alternatively you can enable chunked request
        if (null == metrics || metrics.isEmpty()) {
            return;
        }

        if (batchSizeLimit > 0 && metrics.size() > batchSizeLimit) {
            final Set<OpenTsdbMetric> smallMetrics = new HashSet<OpenTsdbMetric>();
            for (final OpenTsdbMetric metric : metrics) {
                smallMetrics.add(metric);
                if (smallMetrics.size() >= batchSizeLimit) {
                    sendHelper(smallMetrics);
                    smallMetrics.clear();
                }
            }
            sendHelper(smallMetrics);
        } else {
            sendHelper(metrics);
        }
    }

    private void sendHelper(Collection<OpenTsdbMetric> metrics) {
        if (!metrics.isEmpty()) {
            String json = JSON.toJSONString(metrics);
            sendHelper(json);
        }
    }

    private void sendHelper(String json) {
        System.out.print(json);
        /*
         * might want to bind to a specific version of the API.
         * according to: http://opentsdb.net/docs/build/html/api_http/index.html#api-versioning
         * "if you do not supply an explicit version, ... the latest version will be used."
         * circle back on this if it's a problem.
         */
        if (null != json && !json.isEmpty()) {
            HttpPost httpPost = new HttpPost(API_PUT);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int statusCode;
            try {
                response = client.execute(host, httpPost);
                statusCode = response.getStatusLine().getStatusCode();
                response.getEntity().writeTo(outputStream);
            } catch (IOException e) {
                System.out.println(e);
                logger.error("Exception thrown when inserted the datapoints.", e);
                return;
            }

            // Response should not be null when no exception happened
            if (statusCode == 400) {
                String respEntity = outputStream.toString();
                // System.out.println(respEntity);
                //System.out.println(json);
                logger.error("Failed to insert the datapoints, msg = {}", respEntity);
                logger.error("The datapoints = {}", json);
            }
        }
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            logger.error("Failed to close the client", e);
        }
    }
}
