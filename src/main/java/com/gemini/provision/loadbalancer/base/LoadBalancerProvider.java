/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.base;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiLoadBalancer;
import com.gemini.domain.model.GeminiLoadBalancerHealthMonitor;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import java.util.List;

/**
 *
 * @author schari
 */
public interface LoadBalancerProvider {
    //vip related functions
    public List<GeminiLoadBalancer> listAllVIPs (GeminiTenant tenant, GeminiEnvironment env);
    public ProvisioningProviderResponseType createVIP (GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb);
    public GeminiLoadBalancer getVIP (GeminiTenant tenant, GeminiEnvironment env, String vipID);
    public ProvisioningProviderResponseType updateVIP (GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb);
    public ProvisioningProviderResponseType deleteVIP (GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb);
    
    //health monnitor related functions - not all providers may support this
    public List<GeminiLoadBalancerHealthMonitor> listAllHealthMonitors(GeminiTenant tenant, GeminiEnvironment env);
    public GeminiLoadBalancerHealthMonitor createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, String lb);
    public ProvisioningProviderResponseType createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor lb);
    public ProvisioningProviderResponseType updateHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor lb);
    public ProvisioningProviderResponseType deleteHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor lb);
}
