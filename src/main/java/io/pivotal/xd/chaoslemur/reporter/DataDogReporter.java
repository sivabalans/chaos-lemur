/*
 * Copyright 2014-2015 the original author or authors.
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

package io.pivotal.xd.chaoslemur.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

final class DataDogReporter implements Reporter {

    private static final String URI = "https://app.datadoghq.com/" +
            "api/v1/events?api_key={apiKey}&application_key={appKey}";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String apiKey;

    private final String appKey;

    private final RestTemplate restTemplate;

    private final String[] tags;

    DataDogReporter(String apiKey, String appKey, RestTemplate restTemplate, String[] tags) {
        this.apiKey = apiKey;
        this.appKey = appKey;
        this.restTemplate = restTemplate;
        this.tags = tags;
    }

    @Override
    public void sendEvent(String title, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("text", message);
        payload.put("tags", this.tags);

        try {
            this.restTemplate.postForEntity(URI, payload, Void.class, this.apiKey, this.appKey);
        } catch (HttpClientErrorException e) {
            this.logger.error(e.getResponseBodyAsString());
        }
    }
}
