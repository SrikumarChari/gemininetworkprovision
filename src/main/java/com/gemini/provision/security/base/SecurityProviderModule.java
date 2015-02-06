/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.security.base;

import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.provision.security.openstack.SecurityProviderOpenStackImpl;
import com.google.inject.AbstractModule;

/**
 *
 * @author schari
 */
public class SecurityProviderModule extends AbstractModule {
    private GeminiEnvironmentType type;

    public SecurityProviderModule(GeminiEnvironmentType type) {
        this.type = type;
    }

    @Override
    protected void configure() {
        if (type == GeminiEnvironmentType.OPENSTACK) {
            bind(SecurityProvider.class).to(SecurityProviderOpenStackImpl.class);
        } else {
            //network provisioning provider has not been set!!! use the default.
            bind(SecurityProvider.class).to(SecurityProviderDefaultImpl.class);
        }
    }
}
