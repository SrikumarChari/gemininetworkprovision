/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.openstack;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiLoadBalancer;
import com.gemini.domain.model.GeminiLoadBalancerHealthMonitor;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.loadbalancer.base.LoadBalancerProvider;
import java.util.List;

/**
 *
 * @author schari
 */
public class LoadBalancerProviderOpenStackImpl implements LoadBalancerProvider {

    @Override
    public List<GeminiLoadBalancer> listAllVIPs(GeminiTenant tenant, GeminiEnvironment env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiLoadBalancer getVIP(GeminiTenant tenant, GeminiEnvironment env, String vipID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiLoadBalancerHealthMonitor> listAllHealthMonitors(GeminiTenant tenant, GeminiEnvironment env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiLoadBalancerHealthMonitor createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, String lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
