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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 *         <p/>
 *         Representation of a metric.
 */
public class OpenTsdbMetric implements Serializable{
    public OpenTsdbMetric(){

    }

    public OpenTsdbMetric(String metric, long timestamp, Object value, Map<String, String> tags) {
        this.metric = metric;
        this.timestamp = timestamp;
        this.value = value;
        this.tags = new HashMap<String,String>();
        this.tags.putAll(tags);
    }


    public String metric;

    private Long timestamp;

    private Object value;

    private Map<String, String> tags = new HashMap<String, String>();

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof OpenTsdbMetric)) {
            return false;
        }

        final OpenTsdbMetric rhs = (OpenTsdbMetric) o;

        return equals(metric, rhs.metric)
                && equals(timestamp, rhs.timestamp)
                && equals(value, rhs.value)
                && equals(tags, rhs.tags);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{metric, timestamp, value, tags});
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "->metric: " + metric
                + ",value: " + value
                + ",timestamp: " + timestamp
                + ",tags: " + tags;
    }

    public String getMetric() {
        return metric;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Object getValue() {
        return value;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    private boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}