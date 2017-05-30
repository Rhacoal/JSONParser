package com.github.rhacoal.jsonpaser;

import com.github.rhacoal.jsonpaser.json.JsonParser;
import com.github.rhacoal.jsonpaser.json.value.JsonObject;

public class Demo {

    public static void main(String[] args) {
        String json = "{\"test\": [123e2, 7.6, -3], \"test2\": {\"test3\": []}}";
        JsonParser jp = new JsonParser(json, true);
        JsonObject jo = jp.parse().asObject();
        System.out.println(jo.get("test").asArray().get(2));
        System.out.println(jo.encode(true));
    }

}
