/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiSecurityGroupDTO;
import com.gemini.domain.dto.GeminiSecurityGroupRuleDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author schari
 */
public class GeminiSecurityGroupSerializer implements JsonSerializer<GeminiSecurityGroupDTO> {

    @Override
    public JsonElement serialize(GeminiSecurityGroupDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject secGrpElement = new JsonObject();

        //the primitives first
        secGrpElement.addProperty("name", src.getName());
        secGrpElement.addProperty("description", src.getDescription());
        secGrpElement.addProperty("cloudID", src.getCloudID());

        JsonArray secRuleArray = new JsonArray();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiSecurityGroupRuleDTO.class, new GeminiSecurityGroupRuleSerializer())
                .create();
        src.getSecurityRules().stream().forEach(sr -> secRuleArray.add(gson.toJsonTree(sr, GeminiSecurityGroupRuleDTO.class)));
        secGrpElement.add("rules", secRuleArray);

        return secGrpElement;
    }
}
