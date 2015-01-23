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
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class NetworkProviderDefaultImpl implements NetworkProvider {

    @Override
    public String provisioningDesc() {
        return "Default implementation class for Network Provisioning module. WARNING: it will throw an exception"
                + "for every function call!!!";
    }

    @Override
    public List<GeminiEnvironment> getEnvironments(GeminiTenant tenant) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiNetwork> getExternalGateways(GeminiTenant tenant, GeminiEnvironment env) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiNetwork> getNetworks(GeminiTenant tenant, GeminiEnvironment env) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType createNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<NetworkProviderResponseType> bulkCreateNetwork(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetwork> networks) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType deleteNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType updateNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiSubnet> getSubnets(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType createSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, GeminiSubnet subnet) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<NetworkProviderResponseType> bulkCreateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, List<GeminiSubnet> subnets) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType updateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet network) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType deleteSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet network) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiNetworkRouter> getAllRouters(GeminiTenant tenant) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiNetworkRouter> getEnvRouters(GeminiTenant tenant, GeminiEnvironment env) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType createRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter route) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<NetworkProviderResponseType> bulkCreateRouter(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetworkRouter> routes) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType updateRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter route) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType deleteRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter route) {
        Logger.error("Incorrect usage: Network Provisioning provider has not been");
        throw new UnsupportedOperationException("Incorrect usage: Network Provisioning provider has not been"); //To change body of generated methods, choose Tools | Templates.
    }
}
