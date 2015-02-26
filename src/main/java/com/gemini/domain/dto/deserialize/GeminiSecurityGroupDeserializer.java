/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiSecurityGroupDTO;
import com.gemini.domain.dto.GeminiSecurityGroupRuleDTO;
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
 * @author Srikumar
 */
public class GeminiSecurityGroupDeserializer implements JsonDeserializer<GeminiSecurityGroupDTO> {

    @Override
    public GeminiSecurityGroupDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiSecurityGroupDTO newSecGroup = new GeminiSecurityGroupDTO();

        //first the name and description
        try {
            newSecGroup.setName(json.getAsJsonObject().get("name").getAsString());
            newSecGroup.setName(json.getAsJsonObject().get("cloudID").getAsString());
            newSecGroup.setDescription(json.getAsJsonObject().get("description").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group object, no name or description provided");
        }

        //now the list of security group rules
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(GeminiSecurityGroupRuleDTO.class, new GeminiSecurityGroupRuleDeserializer())
                    .create();

            JsonArray rules = json.getAsJsonObject().get("rules").getAsJsonArray();
            //parse all the security group objects
            for (JsonElement e : rules) {
                GeminiSecurityGroupRuleDTO n = gson.fromJson(e, GeminiSecurityGroupRuleDTO.class);
                newSecGroup.addSecurityRule(n);
                n.setParent(newSecGroup);
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rules for server {}", newSecGroup.getName());
        }
        return newSecGroup;
    }
}
