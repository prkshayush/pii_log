package com.piilog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class ConfigLoader {
    public static List<Pattern> loadPatterns(String configPath) throws Exception {
        ObjectMapper mapper;
        if (configPath.endsWith(".yaml") || configPath.endsWith(".yml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else if (configPath.endsWith(".json")) {
            mapper = new ObjectMapper();
        } else {
            throw new IllegalArgumentException("Unsupported config file type. Use YAML or JSON.");
        }
        JsonNode root = mapper.readTree(new File(configPath));
        List<Pattern> patterns = new ArrayList<>();
        if (root.has("patterns")) {
            for (JsonNode node : root.get("patterns")) {
                patterns.add(Pattern.compile(node.asText()));
            }
        } else {
            throw new IllegalArgumentException("Config file must contain a 'patterns' array.");
        }
        return patterns;
    }

    public static List<String> loadKeywords(String configPath) throws Exception {
        ObjectMapper mapper;
        if (configPath.endsWith(".yaml") || configPath.endsWith(".yml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else if (configPath.endsWith(".json")) {
            mapper = new ObjectMapper();
        } else {
            throw new IllegalArgumentException("Unsupported config file type. Use YAML or JSON.");
        }
        JsonNode root = mapper.readTree(new File(configPath));
        List<String> keywords = new ArrayList<>();
        if (root.has("keywords")) {
            for (JsonNode node : root.get("keywords")) {
                keywords.add(node.asText());
            }
        } else {
            throw new IllegalArgumentException("Config file must contain a 'keywords' array.");
        }
        return keywords;
    }
}
