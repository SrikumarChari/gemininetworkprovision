/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.dto.GeminiTenantDTO;
import com.gemini.domain.dto.GeminiTenantUserDTO;
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
public class GeminiTenantDeserializer implements JsonDeserializer<GeminiTenantDTO> {

    @Override
    public GeminiTenantDTO deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        GeminiTenantDTO newTenant = new GeminiTenantDTO();

        try {
            newTenant.setName(json.getAsJsonObject().get("name").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - no tenant name specified");
        }

        try {
            newTenant.setDomainName(json.getAsJsonObject().get("domainName").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - no domain name for tenant {}", newTenant.getName());
        }

        //now the users
        try {
            JsonArray userArray = json.getAsJsonObject().get("users").getAsJsonArray();
            for (JsonElement u : userArray) {
                newTenant.addUser(new Gson().fromJson(u, GeminiTenantUserDTO.class));
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
            Logger.debug("No users for tenant {}", newTenant.getName());
        }

        //now the environments
        try {
            JsonArray envArray = json.getAsJsonObject().get("environments").getAsJsonArray();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(GeminiEnvironmentDTO.class, new GeminiEnvironmentDeserializer())
                    .create();
            for (JsonElement e : envArray) {
                newTenant.addEnvironment(gson.fromJson(e, GeminiEnvironmentDTO.class));
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
            Logger.debug("No environments for tenant: {}", newTenant.getName());
        }
        return newTenant;
    }
}
