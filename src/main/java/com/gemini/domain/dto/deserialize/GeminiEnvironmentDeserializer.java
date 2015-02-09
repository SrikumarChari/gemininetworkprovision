/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.deserialize;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiNetworkRouterDTO;
import com.gemini.domain.dto.GeminiSecurityGroupDTO;
import com.gemini.domain.dto.GeminiSubnetDTO;
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
public class GeminiEnvironmentDeserializer implements JsonDeserializer<GeminiEnvironmentDTO> {

    @Override
    public GeminiEnvironmentDTO deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        GeminiEnvironmentDTO newEnv = new GeminiEnvironmentDTO();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeminiNetworkDTO.class, new GeminiNetworkDeserializer())
                .registerTypeAdapter(GeminiApplicationDTO.class, new GeminiApplicationDeserializer())
                .registerTypeAdapter(GeminiNetworkRouterDTO.class, new GeminiNetworkRouterDeserializer())
                .registerTypeAdapter(GeminiSecurityGroupDTO.class, new GeminiSecurityGroupDeserializer())
                .create();

        //using multiple try/catch blocks to enable error specific messaging
        //first the name
        try {
            newEnv.setName(json.getAsJsonObject().get("name").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            Logger.error("Malformed JSON - no name for environment");
        }

        //now the environment type
        try {
            newEnv.setType(json.getAsJsonObject().get("type").getAsString());
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException ex) {
            //TODO: SHOULD WE JUST RETURN AN ERROR AT THIS POINT?
            Logger.error("Malformed JSON - no type for environment {}", newEnv.getName());
        }

        //the security groups
        try {
            JsonArray secGrpArray = json.getAsJsonObject().get("securityGroups").getAsJsonArray();
            for (JsonElement e : secGrpArray) {
                GeminiSecurityGroupDTO newSecGrp = gson.fromJson(e, GeminiSecurityGroupDTO.class);
                newEnv.addSecurityGroup(newSecGrp);
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
            //no routers, ignore error and move on
            Logger.debug("No security groups for environment {}", newEnv.getName());
        }

        //now the gatweays
        try {
            JsonArray appArray = json.getAsJsonObject().get("gateways").getAsJsonArray();
            for (JsonElement e : appArray) {
                GeminiNetworkDTO newGateway = gson.fromJson(e, GeminiNetworkDTO.class);
                newEnv.addGateway(newGateway);
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
            Logger.debug("No gateways set for environment: {}", newEnv.getName());
        }

        //now the applications
        try {
            JsonArray appArray = json.getAsJsonObject().get("applications").getAsJsonArray();
            for (JsonElement e : appArray) {
                GeminiApplicationDTO newApp = gson.fromJson(e, GeminiApplicationDTO.class);
                newEnv.addApplication(newApp);
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
            //no applications, strange!!
            Logger.debug("No applications for environment: {}", newEnv.getName());
        }

        //now the routers
        try {
            JsonArray routerArray = json.getAsJsonObject().get("routers").getAsJsonArray();
            for (JsonElement e : routerArray) {
                //deserialize the router
                GeminiNetworkRouterDTO newRouter = gson.fromJson(e, GeminiNetworkRouterDTO.class);
                newEnv.addRouter(newRouter);

                //the subnet need to be added by reference (i.e., do not create
                //new subnet objects). The deserialization of the application objects - networks -subnet
                //would have created if it exists in the definition.
                JsonArray subnetArray = json.getAsJsonObject().get("interfaces").getAsJsonArray();
                for (JsonElement s : subnetArray) {
                    //get the name of the subnet and find it in the Gemini data model
                    String subnetName = json.getAsJsonObject().get("name").getAsString();
                    GeminiSubnetDTO foundSubnet = newEnv.getApplications()
                            .stream()
                            .map(GeminiApplicationDTO::getNetworks)
                            .flatMap(List::stream)
                            .map(GeminiNetworkDTO::getSubnets)
                            .flatMap(List::stream)
                            .filter(su -> su.getName().equals(subnetName))
                            .findAny()
                            .get();
                    if (foundSubnet != null) {
                        newRouter.addInterface(foundSubnet);
                    }
                }
            }
        } catch (NullPointerException | JsonSyntaxException | IllegalStateException npe) {
            //no routers, ignore error and move on
            Logger.debug("No routers for environment {}", newEnv.getName());
        }
        
        return newEnv;
    }
}
