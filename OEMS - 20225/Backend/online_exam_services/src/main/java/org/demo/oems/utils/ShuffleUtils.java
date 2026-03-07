package org.demo.oems.utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleUtils {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(ShuffleUtils.class);

    private ShuffleUtils() {}

    /**
     * Shuffle a JSON array string and return shuffled JSON array string
     * Example input:  "[1,2,3,4,5]"
     * Example output: "[3,1,5,2,4]" (order randomized)
         */
    public static String shuffleJsonArrayString(String jsonArrayStr) {
        if (jsonArrayStr == null || jsonArrayStr.trim().isEmpty()) {
            return jsonArrayStr;
        }

        try {
            List<Object> list = mapper.readValue(jsonArrayStr, new TypeReference<>() {});
            Collections.shuffle(list);
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            logger.error("Failed to shuffle JSON array :: {}", e.getMessage());
            return jsonArrayStr;
        }
    }

    /**
     * Generic version: shuffle any List<T> and return it
     * Useful when you already have parsed data
     */
    public static <T> List<T> shuffleList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * Shuffle and return as JSON string (convenience overload)
     */
    public static <T> String shuffleListToJson(List<T> list) {
        List<T> shuffled = shuffleList(list);
        try {
            return mapper.writeValueAsString(shuffled);
        } catch (Exception e) {
            return "[]";
        }
    }

}
