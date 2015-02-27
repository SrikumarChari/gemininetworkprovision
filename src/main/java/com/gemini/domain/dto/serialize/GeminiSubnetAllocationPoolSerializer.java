/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiServerDTO;
import com.gemini.domain.dto.GeminiSubnetAllocationPoolDTO;
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
public class GeminiSubnetAllocationPoolSerializer implements JsonSerializer<GeminiSubnetAllocationPoolDTO> {

    @Override
    public JsonElement serialize(GeminiSubnetAllocationPoolDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject allocPool = new JsonObject();
        
        //the primitives
        allocPool.addProperty("start", src.getStart());
        allocPool.addProperty("end", src.getEnd());
        allocPool.addProperty("parent", src.getParent().getName());
        
        //ignore the parent object it is not needed for the JSON
        
        //now the servers
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiServerDTO.class, new GeminiServerSerializer())
                .create();
        JsonArray serverArray = new JsonArray();
        src.getServers().stream().forEach(s -> serverArray.add(gson.toJsonTree(s, GeminiServerDTO.class)));
        allocPool.add("servers", serverArray);
        
        return allocPool;
    }
    
}
