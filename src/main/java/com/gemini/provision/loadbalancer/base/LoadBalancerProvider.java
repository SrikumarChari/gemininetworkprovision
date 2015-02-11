/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.base;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiLoadBalancer;
import com.gemini.domain.model.GeminiLoadBalancerHealthMonitor;
import com.gemini.domain.model.GeminiLoadBalancerPool;
import com.gemini.domain.model.GeminiPoolMember;
import com.gemini.domain.model.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import java.util.List;

/**
 *
 * @author schari
 */
public interface LoadBalancerProvider {

    //vip related functions

    public List<GeminiLoadBalancer> listAllVIPs(GeminiTenant tenant, GeminiEnvironment env);
    public ProvisioningProviderResponseType createVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb);
    public GeminiLoadBalancer getVIP(GeminiTenant tenant, GeminiEnvironment env, String vipID);
    public ProvisioningProviderResponseType updateVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb);
    public ProvisioningProviderResponseType deleteVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb);

    //health monnitor related functions - not all providers may support this
    public List<GeminiLoadBalancerHealthMonitor> listAllHealthMonitors(GeminiTenant tenant, GeminiEnvironment env);
    public GeminiLoadBalancerHealthMonitor getHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, String lb);
    public ProvisioningProviderResponseType createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm);
    public ProvisioningProviderResponseType updateHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm);
    public ProvisioningProviderResponseType deleteHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm);

    //pool related functions
    public List<GeminiLoadBalancerPool> listAllPools(GeminiTenant tenant, GeminiEnvironment env);
    public GeminiLoadBalancerPool getPool(GeminiTenant tenant, GeminiEnvironment env, String poolID); //used to populate a Pool model object
    public ProvisioningProviderResponseType createLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool);
    public ProvisioningProviderResponseType updateLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool);
    public ProvisioningProviderResponseType deleteLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool);
    public ProvisioningProviderResponseType associateHealthMonitorToPool (GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm);
    public ProvisioningProviderResponseType disassociateHealthMonitorFromPool (GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm);

    //pool member related functions
    public List<GeminiPoolMember> getPoolMembers(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool);
    public ProvisioningProviderResponseType addPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember);
    public GeminiPoolMember getPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, String poolMemberID);
    public ProvisioningProviderResponseType updatePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember);
    public ProvisioningProviderResponseType deletePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember);
}
