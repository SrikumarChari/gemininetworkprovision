/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiServerDTO;
import com.gemini.domain.dto.GeminiSubnetAllocationPoolDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiSubnetAllocationPoolDeserializer implements JsonDeserializer<GeminiSubnetAllocationPoolDTO> {

    @Override
    public GeminiSubnetAllocationPoolDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GeminiSubnetAllocationPoolDTO newAllocPool = new GeminiSubnetAllocationPoolDTO();
        newAllocPool.setStart(json.getAsJsonObject().get("start").getAsString());
        newAllocPool.setEnd(json.getAsJsonObject().get("end").getAsString());

        //ignore the parent, it will be set when the parent subnet is deserialized
        String parent = json.getAsJsonObject().get("parent").getAsString();

        //now the servers
        try {
            JsonArray serverArray = json.getAsJsonObject().get("servers").getAsJsonArray();
            for (JsonElement e : serverArray) {
                newAllocPool.addServer(new Gson().fromJson(e, GeminiServerDTO.class));
            }
        } catch (NullPointerException npe) {
            Logger.debug("No servers in allocation pool {} {}", newAllocPool.getStart(), newAllocPool.getEnd());
        }
        return newAllocPool;
    }
}
