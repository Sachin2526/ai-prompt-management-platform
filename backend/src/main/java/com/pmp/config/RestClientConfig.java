package com.pmp.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

import java.time.Duration;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class RestClientConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Intercepts all outgoing requests to Groq and strips the `extra_body`
     * field that Spring AI injects but Groq's API does not support.
     */
    @Bean
    RestClientCustomizer restClientCustomizer() {
        return builder -> {
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(Duration.ofSeconds(20));
            factory.setReadTimeout(Duration.ofSeconds(60));
            builder.requestFactory(factory);

            builder.requestInterceptor(new ExtraBodyStripperInterceptor(objectMapper));
        };
    }

    private static class ExtraBodyStripperInterceptor implements ClientHttpRequestInterceptor {

        private final ObjectMapper objectMapper;

        ExtraBodyStripperInterceptor(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            if (body != null && body.length > 0) {
                String contentType = request.getHeaders()
                        .getFirst(HttpHeaders.CONTENT_TYPE);
                if (contentType != null && contentType.contains("application/json")) {
                    body = stripExtraBody(body);
                }
            }
            return execution.execute(request, body);
        }

        private byte[] stripExtraBody(byte[] original) {
            try {
                JsonNode node = objectMapper.readTree(original);
                if (node instanceof ObjectNode objectNode && objectNode.has("extra_body")) {
                    objectNode.remove("extra_body");
                    return objectMapper.writeValueAsBytes(objectNode);
                }
            } catch (Exception ignored) {
                // If we can't parse, send as-is
            }
            return original;
        }
    }
}