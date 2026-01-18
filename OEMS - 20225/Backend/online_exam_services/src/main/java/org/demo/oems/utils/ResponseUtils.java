package org.demo.oems.utils;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseUtils {

    public static JSONObject formatServiceResponse(String responseCode, String responseMessage){
        JSONObject finalResponse = new JSONObject();
        finalResponse.put("responseCode", responseCode);
        finalResponse.put("responseMessage", responseMessage);
        return finalResponse;
    }


    public static Map<String, Object> formatAPIResponse(String responseCode, String responseMessage, Object data){
        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("code", responseCode);
        finalResponse.put("message", responseMessage);
        finalResponse.put("data", data);
        return finalResponse;
    }




}
