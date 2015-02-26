/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiNetworkDTO;
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
public class GeminiNetworkDeserializer implements JsonDeserializer<GeminiNetworkDTO> {

    @Override
    public GeminiNetworkDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiNetworkDTO network = new GeminiNetworkDTO();

        try {
            network.setName(json.getAsJsonObject().get("name").getAsString());
            network.setDescription(json.getAsJsonObject().get("description").getAsString());
            network.setNetworkType(json.getAsJsonObject().get("networkType").getAsString());
            network.setProvisioned(json.getAsJsonObject().get("provisioned").getAsBoolean());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - network deserializer no name, networkType or provisioned fields");
        }

        try {
            network.setCloudID(json.getAsJsonObject().get("cloudID").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.debug("no cloud id for network {}", network.getName());
        }

        //now the subnets
        try {
            JsonArray subnetArray = json.getAsJsonObject().get("subnets").getAsJsonArray();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(GeminiSubnetDTO.class, new GeminiSubnetDeserializer())
                    .create();

            for (JsonElement e : subnetArray) {
                GeminiSubnetDTO newSubnetDTO = gson.fromJson(e, GeminiSubnetDTO.class);
                newSubnetDTO.setParent(network);
                network.addSubnet(newSubnetDTO);
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException e) {
            //no subnets, could be a gateway object. Ignore exception
            Logger.debug("No subnets for network {}", network.getName());
        }
        return network;
    }

}
