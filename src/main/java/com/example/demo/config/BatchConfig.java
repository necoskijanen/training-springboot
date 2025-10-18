package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "batch")
@Data
public class BatchConfig {

    private List<Job> jobs;

    @Data
    public static class Job {
        private String id;
        private String name;
        private String description;
        private boolean enabled;
        private String command;
        private List<String> arguments;
        private Map<String, String> environment;
        private int timeout;
        private String workingDirectory;
    }
}