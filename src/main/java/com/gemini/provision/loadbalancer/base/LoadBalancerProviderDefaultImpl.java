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
public class LoadBalancerProviderDefaultImpl implements LoadBalancerProvider {

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
    public GeminiLoadBalancerHealthMonitor getHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, String lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiLoadBalancerPool> listAllPools(GeminiTenant tenant, GeminiEnvironment env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiLoadBalancerPool getPool(GeminiTenant tenant, GeminiEnvironment env, String poolID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType associateHealthMonitorToPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType disassociateHealthMonitorFromPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiPoolMember> getPoolMembers(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType addPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiPoolMember getPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, String poolMemberID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updatePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deletePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
