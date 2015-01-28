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

        //assumes there may 1-n tenants - this works even if there is ony one tenant
        JsonArray tenantArray = json.getAsJsonObject().get("tenant").getAsJsonArray();
        for (JsonElement t : tenantArray) {
            try {
                newTenant.setName(t.getAsJsonObject().get("name").getAsString());
                newTenant.setAdminUserName(t.getAsJsonObject().get("adminUserName").getAsString());
                newTenant.setAdminPassword(t.getAsJsonObject().get("adminPassword").getAsString());
                newTenant.setEndPoint(t.getAsJsonObject().get("endPoint").getAsString());
                newTenant.setDomainName(t.getAsJsonObject().get("domainName").getAsString());
            } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
                Logger.error("Malformed JSON in tenant deserialization");
            }

            //now the users
            try {
                JsonArray userArray = t.getAsJsonObject().get("users").getAsJsonArray();
                for (JsonElement u : userArray) {
                    newTenant.addUser(new Gson().fromJson(u, GeminiTenantUserDTO.class));
                }
            } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
                Logger.debug("No users for tenant {}", newTenant.getName());
            }

            //now the environments
            try {
                JsonArray envArray = t.getAsJsonObject().get("environments").getAsJsonArray();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(GeminiEnvironmentDTO.class, new GeminiEnvironmentDeserializer())
                        .create();
                for (JsonElement e : envArray) {
                    newTenant.addEnvironment(gson.fromJson(e, GeminiEnvironmentDTO.class));
                }
            } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
                Logger.debug("No environments for tenant: {}", newTenant.getName());
            }
        }
        return newTenant;
    }

}
