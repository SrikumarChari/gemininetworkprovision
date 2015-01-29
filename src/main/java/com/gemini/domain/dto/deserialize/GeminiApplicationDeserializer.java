/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
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
public class GeminiApplicationDeserializer implements JsonDeserializer<GeminiApplicationDTO> {

    @Override
    public GeminiApplicationDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiApplicationDTO newApp = new GeminiApplicationDTO();

        //using different try/catch blocks to enable specific error messages
        //first the primitive fields
        try {
            newApp.setName(json.getAsJsonObject().get("name").getAsString());
            newApp.setDescription(json.getAsJsonObject().get("description").getAsString());
            newApp.setCustom(json.getAsJsonObject().get("custom").getAsString());
            newApp.setBackupSize(json.getAsJsonObject().get("backupSize").getAsInt());
            newApp.setLocation(json.getAsJsonObject().get("location").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON Application Deserializer - no application name, description, custom, backupSize and location");
        }

        //now the networks
        try {
            JsonArray networkArray = json.getAsJsonObject().get("networks").getAsJsonArray();

            //create the gson and register custom deserializer
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(GeminiNetworkDTO.class, new GeminiNetworkDeserializer())
                    .create();

            //parse all the network objects
            for (JsonElement e : networkArray) {
                GeminiNetworkDTO n = gson.fromJson(e, GeminiNetworkDTO.class);
                newApp.addNetwork(n);
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
            Logger.debug("No networks for application: {}", newApp.getName());
        }
        return newApp;
    }
}
