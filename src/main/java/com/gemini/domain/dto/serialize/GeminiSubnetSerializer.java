/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiSubnetAllocationPoolDTO;
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
public class GeminiSubnetSerializer implements JsonSerializer<GeminiSubnetDTO> {

    @Override
    public JsonElement serialize(GeminiSubnetDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject subnetElement = new JsonObject();
        
        //first the primitives
        subnetElement.addProperty("name", src.getName());
        subnetElement.addProperty("cloudID", src.getCloudID());
        subnetElement.addProperty("cidr", src.getCidr());
        subnetElement.addProperty("gateway", src.getGateway());
        subnetElement.addProperty("enableDHCP", src.isEnableDHCP());
        subnetElement.addProperty("networkType", src.getNetworkType());
        
        //now the allocation pool
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiSubnetAllocationPoolDTO.class, new GeminiSubnetAllocationPoolSerializer())
                .create();
        JsonArray allocPool = new JsonArray();
        src.getAllocationPool().stream().forEach(ap -> allocPool.add(gson.toJsonTree(ap, GeminiSubnetAllocationPoolDTO.class)));
        subnetElement.add("allocationPool", allocPool);
        
        return subnetElement;
    }
    
}
