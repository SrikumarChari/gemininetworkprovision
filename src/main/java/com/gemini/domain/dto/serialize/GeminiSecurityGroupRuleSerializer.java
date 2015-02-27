/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiSecurityGroupRuleDTO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author schari
 */
public class GeminiSecurityGroupRuleSerializer implements JsonSerializer<GeminiSecurityGroupRuleDTO> {

    @Override
    public JsonElement serialize(GeminiSecurityGroupRuleDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject secGrpRuleElement = new JsonObject();
        
        //only primitives, so straigh forward
        secGrpRuleElement.addProperty("name", src.getName());
        secGrpRuleElement.addProperty("cloudID", src.getCloudID());
        secGrpRuleElement.addProperty("direction", src.getDirection());
        secGrpRuleElement.addProperty("ipAddressType", src.getIpAddressType());
        secGrpRuleElement.addProperty("portRangeMax", src.getPortRangeMax());
        secGrpRuleElement.addProperty("portRangeMin", src.getPortRangeMin());
        secGrpRuleElement.addProperty("protocol", src.getProtocol());
        secGrpRuleElement.addProperty("remoteIpPrefix", src.getRemoteIpPrefix());
        secGrpRuleElement.addProperty("remoteGroupId", src.getRemoteGroupId());        
        
        return secGrpRuleElement;
    }
    
}
