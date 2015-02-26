/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.model.GeminiTenant;
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
public class GeminiTenantSerializer implements JsonSerializer<GeminiTenant>{

    @Override
    public JsonElement serialize(GeminiTenant src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject tenantElement = new JsonObject();
        
        //setup our custom serializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiEnvironmentDTO.class, new GeminiEnvironmentSerializer())
                .create();
        
        tenantElement.addProperty("name", src.getName());
        tenantElement.addProperty("domain", src.getDomainName());
        tenantElement.addProperty("users", gson.toJson(src.getUsers()));
        ;
        return tenantElement;
    }
    
}
