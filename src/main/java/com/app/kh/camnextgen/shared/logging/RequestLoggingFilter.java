package com.app.kh.camnextgen.shared.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
        "password", "oldPassword", "newPassword", "confirmPassword", 
        "token", "refreshToken", "accessToken", "secret"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        
        CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
        long start = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logRequestResponse(wrappedRequest, response, duration);
        }
    }

    private void logRequestResponse(CachedBodyHttpServletRequestWrapper request, HttpServletResponse response, long duration) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            
            // Request details
            Map<String, Object> requestDetails = new HashMap<>();
            requestDetails.put("method", request.getMethod());
            requestDetails.put("endpoint", request.getRequestURI());
            requestDetails.put("params", request.getParameterMap());
            
            String body = new String(request.getCachedBody());
            if (body.length() > 0) {
                try {
                    JsonNode bodyNode = objectMapper.readTree(body);
                    maskSensitiveFields(bodyNode);
                    requestDetails.put("body", bodyNode);
                } catch (Exception e) {
                    requestDetails.put("body", body);
                }
            } else {
                requestDetails.put("body", null);
            }
            
            logMap.put("request", requestDetails);

            // Response details
            Map<String, Object> responseDetails = new HashMap<>();
            responseDetails.put("method", request.getMethod());
            responseDetails.put("status", response.getStatus());
            responseDetails.put("endpoint", request.getRequestURI());
            responseDetails.put("durationMs", duration);
            
            logMap.put("response", responseDetails);
            
            log.info("\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logMap));
        } catch (Exception e) {
            log.warn("Failed to log request/response as JSON: {}", e.getMessage());
        }
    }

    private void maskSensitiveFields(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (SENSITIVE_FIELDS.contains(field.getKey())) {
                    objectNode.put(field.getKey(), "******");
                } else {
                    maskSensitiveFields(field.getValue());
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                maskSensitiveFields(element);
            }
        }
    }
}
