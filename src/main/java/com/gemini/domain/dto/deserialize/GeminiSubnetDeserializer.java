/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiNetworkRouterDTO;
import com.gemini.domain.dto.GeminiSubnetAllocationPoolDTO;
import com.gemini.domain.dto.GeminiSubnetDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiSubnetDeserializer implements JsonDeserializer<GeminiSubnetDTO> {

    @Override
    public GeminiSubnetDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiSubnetDTO newSubnet = new GeminiSubnetDTO();
        newSubnet.setName(json.getAsJsonObject().get("name").getAsString());
        newSubnet.setCidr(json.getAsJsonObject().get("cidr").getAsString());
        newSubnet.setGateway(json.getAsJsonObject().get("gateway").getAsString());
        //ignore the parent, it will be set when the network is deserialized
        String parent = json.getAsJsonObject().get("parent").getAsString();

        //now the allocation pool
        try {
            JsonArray poolArray = json.getAsJsonObject().get("allocationPool").getAsJsonArray();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(GeminiSubnetAllocationPoolDTO.class, new GeminiSubnetAllocationPoolDeserializer())
                    .create();

            for (JsonElement e : poolArray) {
                GeminiSubnetAllocationPoolDTO newPool = gson.fromJson(e, GeminiSubnetAllocationPoolDTO.class);
                newPool.setParent(newSubnet);
                newSubnet.addAllocationPool(newPool);
            }
        } catch (NullPointerException npe) {
            Logger.debug("No allocation pools for subnet {}", newSubnet.getName());
        }
        return newSubnet;
    }

}
