/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.base;

import com.gemini.domain.model.GeminiEnvironmentType;
import com.gemini.provision.network.openstack.NetworkProviderOpenStackImpl;
import com.google.inject.AbstractModule;

/**
 *
 * @author schari
 */
public class NetworkProviderModule extends AbstractModule {

    private GeminiEnvironmentType type;

    public NetworkProviderModule(GeminiEnvironmentType type) {
        this.type = type;
    }

    @Override
    protected void configure() {
        if (type == GeminiEnvironmentType.OPENSTACK) {
            bind(NetworkProvider.class).to(NetworkProviderOpenStackImpl.class);
        } else {
            //network provisioning provider has not been set!!! use the default.
            bind(NetworkProvider.class).to(NetworkProviderDefaultImpl.class);
        }
    }
}
