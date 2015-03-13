/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.dto.GeminiTenantDTO;
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
public class GeminiTenantSerializer implements JsonSerializer<GeminiTenantDTO>{

    @Override
    public JsonElement serialize(GeminiTenantDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject tenantElement = new JsonObject();
        
        //setup our custom serializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiEnvironmentDTO.class, new GeminiEnvironmentSerializer())
                .create();
        
        //the primitives
        tenantElement.addProperty("name", src.getName());
        tenantElement.addProperty("domain", src.getDomainName());
        
        //the users 
        JsonArray userArray = new JsonArray();
        src.getUsers().stream().forEach(u -> {
            JsonObject jsonUser = new JsonObject();
            jsonUser.addProperty("name", u.getName());
            jsonUser.addProperty("userID", u.getUserID());
            jsonUser.addProperty("password", u.getPassword());
            jsonUser.addProperty("preferences", u.getPreferences());
            userArray.add(jsonUser);
        });
        tenantElement.add("users", userArray);
        
        //the environments
        JsonArray envArray = new JsonArray();
        src.getEnvironments().stream().forEach(e -> {
            JsonElement jsonEnv = gson.toJsonTree(e);
            envArray.add(jsonEnv);
        });
        tenantElement.add("environments", envArray);
        return tenantElement;
    }
}
