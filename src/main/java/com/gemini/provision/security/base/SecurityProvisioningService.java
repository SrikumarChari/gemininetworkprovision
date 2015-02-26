/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.security.base;

import com.google.inject.Inject;

/**
 *
 * @author schari
 */
public class SecurityProvisioningService {

    public SecurityProvider getProvider() {
        return securityProvisioningService;
    }

    @Inject
    private void setSecurityProvisioningService(SecurityProvider securityProvisioningService) {
        this.securityProvisioningService = securityProvisioningService;
    }
    private SecurityProvider securityProvisioningService;
}
