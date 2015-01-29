/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.base;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiNetworkRouter;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.tenant.GeminiTenant;
import java.util.List;

/**
 *
 * @author schari
 */
public interface NetworkProvider {
    
    public String provisioningDesc(); //returns a description of the provisioning
    
    //depending on the cloud provider this has different meanings... each implementation must accordingly 
    //map the information and create the appropriate environments.
    public List<GeminiEnvironment> getEnvironments(GeminiTenant tenant);

    //return a list of the external IP's
    public List<GeminiNetwork> getExternalGateways(GeminiTenant tenant, GeminiEnvironment env);

    //the network api
    public List<GeminiNetwork> getNetworks(GeminiTenant tenant, GeminiEnvironment env);
    public NetworkProviderResponseType createNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n);
    public List<NetworkProviderResponseType> bulkCreateNetwork(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetwork> networks);
    public NetworkProviderResponseType deleteNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n);
    public NetworkProviderResponseType updateNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n);
    
    //the subnet API
    public List<GeminiSubnet> getAllSubnets(GeminiTenant tenant, GeminiEnvironment env);
    public List<GeminiSubnet> getSubnets(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent);
    public NetworkProviderResponseType createSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, GeminiSubnet subnet);
    public List<NetworkProviderResponseType> bulkCreateSubnet (GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, List<GeminiSubnet> subnets);
    public NetworkProviderResponseType updateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet network);
    public NetworkProviderResponseType deleteSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet network);
    
    //the route API - some provisioning providers will return a no-op
    //each route will require a gateway - this is stored in the GeminiEnvironment
    //each router also requires a 1 to n host routes. This assumes that the GeminiSubnet will represent
    //the host route.
    public List<GeminiNetworkRouter> getAllRouters(GeminiTenant tenant);
    public List<GeminiNetworkRouter> getEnvRouters(GeminiTenant tenant, GeminiEnvironment env);
    public NetworkProviderResponseType createRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter route);
    public List<NetworkProviderResponseType> bulkCreateRouter(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetworkRouter> routes);
    public NetworkProviderResponseType updateRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter route);
    public NetworkProviderResponseType deleteRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter route);
}
