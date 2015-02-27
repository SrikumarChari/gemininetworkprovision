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
public class GeminiSecurityGroupRuleDeserializer implements JsonDeserializer<GeminiSecurityGroupRuleDTO> {

    @Override
    public GeminiSecurityGroupRuleDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiSecurityGroupRuleDTO newRule = new GeminiSecurityGroupRuleDTO();

        //first the name
        try {
            newRule.setName(json.getAsJsonObject().get("name").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object, no name specified");
        }

        try {
            newRule.setCloudID(json.getAsJsonObject().get("cloudID").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.debug("no cloud id for security group rule {}", newRule.getName());
        }

        //ignore the parent object, it will be set when the parent security group is being deserialized
        try {
            newRule.setDirection(json.getAsJsonObject().get("direction").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object, no direction specified {}", newRule.getName());
        }

        try {
            newRule.setIpAddressType(json.getAsJsonObject().get("ipAddressType").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object, no address type specified {}", newRule.getName());
        }
        try {
            newRule.setPortRangeMax(json.getAsJsonObject().get("portRangeMax").getAsInt());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object, no maxPort specified {}", newRule.getName());
        }
        try {
            newRule.setPortRangeMin(json.getAsJsonObject().get("portRangeMin").getAsInt());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object no minPort value specified {}", newRule.getName());
        }
        try {
            newRule.setProtocol(json.getAsJsonObject().get("protocol").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - invalid security group rule object, no protocol specified {}", newRule.getName());
        }
        try {
            newRule.setRemoteGroupId(json.getAsJsonObject().get("remoteGroupId").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.debug("Malformed JSON - invalid security group rule object no group ID specified {}", newRule.getName());
        }
        try {
            newRule.setRemoteIpPrefix(json.getAsJsonObject().get("remoteIpPrefix").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.debug("Malformed JSON - invalid security group rule object, no remoteIPPrefix specified {}", newRule.getName());
        }

        //one last check... the rule must have either an ip prefix or group id
        if (newRule.getRemoteGroupId().isEmpty() && newRule.getRemoteIpPrefix().isEmpty()) {
            Logger.error("Malformed JSON - invalid security group rule object, no remoteIPPrefix sor remoteGroupID pecified {}", newRule.getName());
        }

        return newRule;
    }
}
