package com.wj.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * desc:JSON数据处理
 * @user:gongdezhe
 * @date:2018/11/26
 */

public class JsonUtil {

    /**
     * 从json字符串中解析ObjectNode
     * @param json
     * @return
     */
    public static ObjectNode getObjectNode(String json) {
        // TODO Auto-generated method stub
        ObjectMapper jsonMapper = new ObjectMapper();
        ObjectNode objectNode = null;
        try {
            objectNode = jsonMapper.readValue(json, ObjectNode.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return objectNode;
    }

    /**
     * 创建一个新的objectNode，用于封装json字符串
     * @return
     */
    public static ObjectNode getObjectNode(){
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.createObjectNode();
    }
}