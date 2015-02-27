/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiNetworkRouterDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author schari
 */
public class GeminiNetworkRouterSerializer implements JsonSerializer<GeminiNetworkRouterDTO> {

    @Override
    public JsonElement serialize(GeminiNetworkRouterDTO src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject routerElement = new JsonObject();

        //register all the required custom serializers
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiNetworkDTO.class, new GeminiNetworkSerializer())
                .create();

        //first the primitives
        routerElement.addProperty("name", src.getName());
        routerElement.addProperty("cloudID", src.getCloudID());
        routerElement.add("gateway", gson.toJsonTree(src.getGateway(), GeminiNetworkDTO.class));

        //now the routes
        JsonArray routeArray = new JsonArray();
        src.getRoutes().entrySet().stream().forEach(e -> 
                routeArray.add(new JsonPrimitive(new StringBuilder()
                        .append(e.getKey())
                        .append(",")
                        .append(e.getValue())
                        .append("\n").toString())));
        routerElement.add("routes", routeArray);

        //now the interfaces - while each interface is a GeminiSubnetDTO object 
        //we will only add the name and cloudID because it is only a reference to 
        //a subnet created under applications
        JsonArray interfaceArray = new JsonArray();
        src.getInterfaces().stream().forEach(i -> {
            JsonObject intElement = new JsonObject();
            intElement.add("name", new JsonPrimitive(i.getName()));
            intElement.add("cloudID", new JsonPrimitive(i.getCloudID()));
            interfaceArray.add(intElement);
        });
        routerElement.add("interfaces", routeArray);
        
        return routerElement;
    }
}
