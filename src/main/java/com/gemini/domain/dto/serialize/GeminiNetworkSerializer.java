/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiSubnetDTO;
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
public class GeminiNetworkSerializer implements JsonSerializer<GeminiNetworkDTO> {

    @Override
    public JsonElement serialize(GeminiNetworkDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject netElement = new JsonObject();

        //first the primitives
        netElement.addProperty("name", src.getName());
        netElement.addProperty("description", src.getDescription());
        netElement.addProperty("type", src.getNetworkType());
        netElement.addProperty("cloudID", src.getCloudID());

        //now the subnets
        JsonArray subnetElements = new JsonArray();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiSubnetDTO.class, new GeminiSubnetSerializer())
                .create();
        src.getSubnets().stream().forEach(s -> {
            JsonElement sElement = gson.toJsonTree(s, GeminiSubnetDTO.class);
            subnetElements.add(sElement);
        });
        netElement.add("subnets", subnetElements);

        return netElement;
    }
}
