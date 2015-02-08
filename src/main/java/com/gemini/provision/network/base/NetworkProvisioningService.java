/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.base;

import com.google.inject.Inject;

/**
 *
 * @author schari
 */
public class NetworkProvisioningService {
    private NetworkProvider networkProvisioningService;
    
    @Inject
    private void setProvider(NetworkProvider svc) {
        networkProvisioningService = svc;
    }
    
    //for now provide a get service
    //TODO: add functions to match the NetworkProvider service, i.e., hide the service
    public NetworkProvider getProvider() {
        return networkProvisioningService;
    }
}
