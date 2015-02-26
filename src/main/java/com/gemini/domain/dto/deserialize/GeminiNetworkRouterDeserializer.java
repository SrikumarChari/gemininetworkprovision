/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiNetworkRouterDTO;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.List;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiNetworkRouterDeserializer implements JsonDeserializer<GeminiNetworkRouterDTO> {

    @Override
    public GeminiNetworkRouterDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiNetworkDTO.class, new GeminiNetworkDeserializer())
                .create();
        GeminiNetworkRouterDTO newRouter = new GeminiNetworkRouterDTO();

        //first the name
        try {
            newRouter.setName(json.getAsJsonObject().get("name").getAsString());
            newRouter.setCloudID(json.getAsJsonObject().get("cloudID").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON: no name specified for Network router");
        }

        //now the gateway
        try {
            newRouter.setGateway(gson.fromJson(json.getAsJsonObject().get("gateway"), GeminiNetworkDTO.class));
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON: No gateway specified for Network Router {}", newRouter.getName());
        }

        //now the routes, no straight forward way to convert to the HashMap
        try {
            JsonArray routeArray = json.getAsJsonObject().get("routes").getAsJsonArray();
            for (JsonElement e : routeArray) {
                //format is nextHop, dest
                String strRoute = e.getAsString();
                List<String> splitRoutes = Splitter.on(',').splitToList(strRoute);
                if (splitRoutes.size() != 2) {
                    Logger.error("Malformed JSON: Invalid route, does not have two map entries. Router {}", newRouter.getName());
                } else {
                    newRouter.addRouter(splitRoutes.get(0), splitRoutes.get(1));
                }
            }
        } catch (NullPointerException npe) {
            Logger.debug("No routes provided for {}", newRouter.getName());
        }

        //the interfaces will be handled at the environment level because they
        //need to added by reference instead of new objects
        return newRouter;
    }
}
