/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.base;

import com.google.inject.Inject;

/**
 *
 * @author schari
 */
public class LoadBalancerProvisioningService {
    private LoadBalancerProvider loadBalancerProvisioningService;

    public LoadBalancerProvider getLoadBalancerProvisioningService() {
        return loadBalancerProvisioningService;
    }

    @Inject
    public void setLoadBalancerProvisioningService(LoadBalancerProvider loadBalancerProvisioningService) {
        this.loadBalancerProvisioningService = loadBalancerProvisioningService;
    }   
}
