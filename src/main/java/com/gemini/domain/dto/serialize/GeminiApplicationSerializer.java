/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
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
public class GeminiApplicationSerializer implements JsonSerializer<GeminiApplicationDTO> {

    @Override
    public JsonElement serialize(GeminiApplicationDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject appElement = new JsonObject();
        
        //the primitives
        appElement.addProperty("name", src.getName());
        appElement.addProperty("description", src.getDescription());
        appElement.addProperty("custom", src.getCustom());
        appElement.addProperty("backupSize", src.getBackupSize());
        appElement.addProperty("location", src.getLocation());
        
        //now the networks
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiNetworkDTO.class, new GeminiNetworkSerializer())
                .create();
        JsonArray netArray = new JsonArray();
        src.getNetworks().stream().forEach(n -> netArray.add(gson.toJsonTree(n, GeminiNetworkDTO.class)));
        appElement.add("networks", netArray);

        return appElement;
    }
}
