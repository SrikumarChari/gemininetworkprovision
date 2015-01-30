/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.base;

import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.provision.loadbalancer.openstack.LoadBalancerProviderOpenStackImpl;
import com.google.inject.AbstractModule;

/**
 *
 * @author schari
 */
public class LoadBalancerProviderModule extends AbstractModule {
    private GeminiEnvironmentType type;

    public LoadBalancerProviderModule(GeminiEnvironmentType type) {
        this.type = type;
    }

    @Override
    protected void configure() {
        if (type == GeminiEnvironmentType.OPENSTACK) {
            bind(LoadBalancerProvider.class).to(LoadBalancerProviderOpenStackImpl.class);
        } else {
            //network provisioning provider has not been set!!! use the default.
            bind(LoadBalancerProvider.class).to(LoadBalancerProviderDefaultImpl.class);
        }
    }
}
