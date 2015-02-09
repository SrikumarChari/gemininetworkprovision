/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiSubnetAllocationPoolDTO;
import com.gemini.domain.dto.GeminiSubnetDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
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
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(GeminiSubnetAllocationPoolDTO.class, new GeminiSubnetAllocationPoolDeserializer())
                    .registerTypeAdapter(GeminiNetworkDTO.class, new GeminiNetworkDeserializer())
                    .create();

        try {
            newSubnet.setName(json.getAsJsonObject().get("name").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - no name for subnet");
        }

        try {
            newSubnet.setCidr(json.getAsJsonObject().get("cidr").getAsString());
            //ignore the parent, it will be set when the network is deserialized
            String parent = json.getAsJsonObject().get("parent").getAsString();
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - no cidr for subnet {}", newSubnet.getName());
        }

        try {
            newSubnet.setGateway(json.getAsJsonObject().get("gateway").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            //not an error, it could be that there is no gateway required for this subnet
            Logger.debug("Malformed JSON - no gateway for subnet {}", newSubnet.getName());
        }

        //now the allocation pool
        try {
            JsonArray poolArray = json.getAsJsonObject().get("allocationPool").getAsJsonArray();
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
