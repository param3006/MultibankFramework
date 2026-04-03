package com.multibank.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
public final class TestDataLoader {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private TestDataLoader() {}


    public static Map<String, Object> loadJson(String classpathFile) {
        try (InputStream is = getStream(classpathFile)) {
            MapType type = JSON_MAPPER.getTypeFactory()
                .constructMapType(Map.class, String.class, Object.class);
            return JSON_MAPPER.readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read JSON test data: " + classpathFile, e);
        }
    }

    /** Loads a JSON file and deserialises it directly into a POJO. */
    public static <T> T loadJson(String classpathFile, Class<T> type) {
        try (InputStream is = getStream(classpathFile)) {
            return JSON_MAPPER.readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException("Cannot deserialise JSON: " + classpathFile, e);
        }
    }


    //Loads a YAML file from the classpath and returns it as a generic map.
    public static Map<String, Object> loadYaml(String classpathFile) {
        try (InputStream is = getStream(classpathFile)) {
            MapType type = YAML_MAPPER.getTypeFactory()
                .constructMapType(Map.class, String.class, Object.class);
            return YAML_MAPPER.readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read YAML test data: " + classpathFile, e);
        }
    }



    public static List<String> getList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List<?> list) {
            return (List<String>) list;
        }
        log.warn("Key '{}' not found or not a list in test data", key);
        return List.of();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Map<?,?> map) {
            return (Map<String, Object>) map;
        }
        log.warn("Key '{}' not found or not a map in test data", key);
        return Map.of();
    }

    public static String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : "";
    }

    public static int getInt(Map<String, Object> data, String key, int defaultValue) {
        Object value = data.get(key);
        if (value instanceof Integer i) return i;
        if (value instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException e) { /* fall through */ }
        }
        return defaultValue;
    }


    private static InputStream getStream(String classpathFile) {
        InputStream is = TestDataLoader.class.getClassLoader().getResourceAsStream(classpathFile);
        if (is == null) throw new IllegalArgumentException(
            "Test data file not found on classpath: " + classpathFile);
        return is;
    }
}
