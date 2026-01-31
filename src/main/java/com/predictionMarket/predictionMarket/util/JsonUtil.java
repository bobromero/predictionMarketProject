package com.predictionMarket.predictionMarket.util;

import tools.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj){
        try{
            return mapper.writeValueAsString(obj);
        }
        catch(Exception e){
            return "{}";
        }
    }
}
