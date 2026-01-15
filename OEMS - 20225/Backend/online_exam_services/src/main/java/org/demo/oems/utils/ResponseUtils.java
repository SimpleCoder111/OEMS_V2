package org.demo.oems.utils;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ResponseUtils {

    public static JSONObject responseFormatUtils(String responseCode, String responseMessage){
        JSONObject finalResponse = new JSONObject();
        finalResponse.put("responseCode", responseCode);
        finalResponse.put("responseMessage", responseMessage);
        return finalResponse;
    }




}
