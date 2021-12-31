package com.bjtu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class TypeConvertUtil {
    // Descriptions: 完成JavaMap到Sting的无损转换
    public static String MapToString(Map map) {
        return JSONObject.toJSONString(map);
    }

    // Descriptions: 完成String到JavaMap的无损转换
    public static Map<String, ArrayList<String>> StringToMap(String str) {
        JSONObject jsonObject = JSON.parseObject(str);
        Map<String, ArrayList<String>> result = new HashMap<>();
        // 遍历jsonObj
        Iterator iter = jsonObject.entrySet().iterator();
        ArrayList<String> listTemp = new ArrayList<>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            // 设置ArrayList
            Object obj = entry.getValue();
            JSONArray jsonArray = (JSONArray) obj;
            listTemp = (ArrayList<String>) jsonArray.toJavaList(String.class);
            if (obj instanceof ArrayList<?>) {
                for (Object o : (ArrayList<?>) obj) {
                    listTemp.add(String.class.cast(o));
                }
            }
            result.put(entry.getKey().toString(), listTemp);
        }
        return result;

    }

    // Descriptions: 完成byte[]到String的无损转换
    public static String BytearrayToString(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    // Descriptions: 完成String到byte[]的无损转换
    public static byte[] StringToBytearray(String str){
        return Base64.getDecoder().decode(str);
    }
}
