/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiSecurityGroupDTO;
import com.gemini.domain.dto.GeminiServerDTO;
import com.gemini.domain.dto.GeminiServerTypeDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.HashMap;
import org.pmw.tinylog.Logger;

/**
 *
 * @author Srikumar
 */
public class GeminiServerDeserializer implements JsonDeserializer<GeminiServerDTO> {

    @Override
    public GeminiServerDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiServerDTO newServer = new GeminiServerDTO();

        //first the name
        try {
            newServer.setName(json.getAsJsonObject().get("name").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid server object, no name provided");
        }

        //now the rest of native variables
        try {
            newServer.setDescription(json.getAsJsonObject().get("description").getAsString());
            newServer.setDateCreated(json.getAsJsonObject().get("dateCreated").getAsString());
            newServer.setAddress(json.getAsJsonObject().get("address").getAsString());
            newServer.setAddressType(json.getAsJsonObject().get("addressType").getAsString());
            newServer.setSubnetMask(json.getAsJsonObject().get("subnetMask").getAsString());
            newServer.setPort(json.getAsJsonObject().get("port").getAsInt());
            newServer.setOs(json.getAsJsonObject().get("os").getAsString());
            newServer.setType(json.getAsJsonObject().get("type").getAsString());
            newServer.setAdmin(json.getAsJsonObject().get("admin").getAsString());
            newServer.setPassword(json.getAsJsonObject().get("password").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid server object {}", newServer.getName());
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiSecurityGroupDTO.class, new GeminiSecurityGroupDeserializer())
                .create();

        //now the server type
        try {
            GeminiServerTypeDTO srvType = gson.fromJson(json.getAsJsonObject().get("serverType"), null);
            newServer.setServerType(srvType);
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid server type object in server {}", newServer.getName());
        }

        //now the metadata
        try {
            Map<String, String> metadata = gson.fromJson(json.getAsJsonObject().get("metadata"), HashMap.class);
            newServer.setMetadata(metadata);
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid metadata for server {}", newServer.getName());
        }

        //now the list of security groups
        try {
            JsonArray secGroups = json.getAsJsonObject().get("secGroups").getAsJsonArray();
            //parse all the security group objects
            for (JsonElement e : secGroups) {
                GeminiSecurityGroupDTO n = gson.fromJson(e, GeminiSecurityGroupDTO.class);
                newServer.addSecGroup(n);
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security groups for server {}", newServer.getName());
        }

        return newServer;
    }

}
