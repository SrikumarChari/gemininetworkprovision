/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiSecurityGroupRuleDTO;
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
class GeminiSecurityGroupRuleDeserializer implements JsonDeserializer<GeminiSecurityGroupRuleDTO> {

    @Override
    public GeminiSecurityGroupRuleDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiSecurityGroupRuleDTO newRule = new GeminiSecurityGroupRuleDTO();

        //first the name
        try {
            newRule.setName(json.getAsJsonObject().get("name").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object, no name specified");
        }

        //ignore the parent object, it will be set when the parent security group is being deserialized
        try {
            newRule.setDirection(json.getAsJsonObject().get("direction").getAsString());
            newRule.setIpAddressType(json.getAsJsonObject().get("ipAddressType").getAsString());
            newRule.setPortRangeMax(json.getAsJsonObject().get("portRangeMax").getAsInt());
            newRule.setPortRangeMin(json.getAsJsonObject().get("portRangeMin").getAsInt());
            newRule.setProtocol(json.getAsJsonObject().get("protocol").getAsString());
            //newRule.setRemoteGroupId(json.getAsJsonObject().get("remoteGroupId").getAsString());
            newRule.setRemoteIpPrefix(json.getAsJsonObject().get("remoteIpPrefix").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object {}", newRule.getName());
        }
        return newRule;
    }
}
