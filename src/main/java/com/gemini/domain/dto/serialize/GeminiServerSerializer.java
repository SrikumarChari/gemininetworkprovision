/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiServerDTO;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 *
 * @author schari
 */
public class GeminiServerSerializer implements JsonSerializer<GeminiServerDTO> {

    @Override
    public JsonElement serialize(GeminiServerDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject serverElement = new JsonObject();
        Gson gson = new Gson();

        serverElement.addProperty("name", src.getName());
        serverElement.addProperty("cloudID", src.getCloudID());
        serverElement.addProperty("description",src.getDescription());
        serverElement.addProperty("dateCreated", src.getDateCreated());
        serverElement.addProperty("address", src.getAddress());
        serverElement.addProperty("addressType", src.getAddressType());
        serverElement.addProperty("subnetMask", src.getSubnetMask());
        serverElement.addProperty("port", src.getPort());
        serverElement.addProperty("os", src.getOs());
        serverElement.addProperty("type", src.getType());
        serverElement.addProperty("admin", src.getAdmin());
        serverElement.addProperty("password", src.getPassword());
        serverElement.addProperty("serverType", src.getServerType());
        
        //the metadata, since it is a HashMap GSON requires a type token what type
        //of data is contained in the HashMap
        TypeToken<HashMap<String, String>> metadataType = new TypeToken<HashMap<String, String>>() {};
        serverElement.add("metadata", gson.toJsonTree(src.getMetadata(), metadataType.getType()));

        //the security group names
        JsonArray secGrpNames = new JsonArray();
        src.getSecurityGroupNames().stream().forEach(sg -> {
            secGrpNames.add(new JsonPrimitive(sg));
        });
        serverElement.add("securityGroupNames", secGrpNames);
        
        return serverElement;
    }

}
