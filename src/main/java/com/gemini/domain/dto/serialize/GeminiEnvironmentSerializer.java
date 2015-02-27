/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiNetworkRouterDTO;
import com.gemini.domain.dto.GeminiSecurityGroupDTO;
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
public class GeminiEnvironmentSerializer implements JsonSerializer<GeminiEnvironmentDTO> {

    @Override
    public JsonElement serialize(GeminiEnvironmentDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject envElement = new JsonObject();

        //register all the required custom serializers
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiNetworkDTO.class, new GeminiNetworkSerializer())
                .registerTypeAdapter(GeminiApplicationDTO.class, new GeminiApplicationSerializer())
                .registerTypeAdapter(GeminiNetworkRouterDTO.class, new GeminiNetworkRouterSerializer())
                .registerTypeAdapter(GeminiSecurityGroupDTO.class, new GeminiSecurityGroupSerializer())
                .create();
        
        //first the primitives
        envElement.addProperty("name", src.getName());
        envElement.addProperty("type", src.getType().toString());
        envElement.addProperty("adminUserName", src.getAdminUserName());
        envElement.addProperty("adminPassword", src.getAdminPassword());
        envElement.addProperty("endPoint", src.getEndPoint());
        
        //now the security group objects
        JsonArray secGrpArray = new JsonArray();
        src.getSecurityGroups().stream().forEach(sg -> {
            JsonElement srcGrp = gson.toJsonTree(sg, GeminiSecurityGroupDTO.class);
            secGrpArray.add(srcGrp);
        });
        envElement.add("securityGroups", secGrpArray);

        //the gateways
        JsonArray gatewayArray = new JsonArray();
        src.getGateways().stream().forEach(g -> {
            JsonElement gateway = gson.toJsonTree(g, GeminiNetworkDTO.class);
            gatewayArray.add(gateway);
        });
        envElement.add("gateways", gatewayArray);
        
        //the applications
        JsonArray appArray = new JsonArray();
        src.getApplications().stream().forEach(a -> {
            JsonElement app = gson.toJsonTree(a, GeminiApplicationDTO.class);
            appArray.add(app);
        });
        envElement.add("applications", appArray);

        //skip the orphan networks as it only has a meaning in-memory
        
        //the routers
        JsonArray routerArray = new JsonArray();
        src.getRouters().stream().forEach(r -> {
            JsonElement router = gson.toJsonTree(r, GeminiNetworkRouterDTO.class);
            routerArray.add(router);
        });
        envElement.add("routers", routerArray);
        
        return envElement;
    }

}
