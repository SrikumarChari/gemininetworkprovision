/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.security.base;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiSecurityGroup;
import com.gemini.domain.model.GeminiSecurityGroupRule;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import java.util.List;

/**
 *
 * @author schari
 */
public class SecurityProviderDefaultImpl implements SecurityProvider {

    @Override
    public String provisioningDesc() {
        return "Default Security Provisioning Implementation. Does not provide any functionality"
                + " - it is primarily used a dependency injection helper";
    }

    @Override
    public List<GeminiSecurityGroup> listAllSecurityGroups(GeminiTenant tenant, GeminiEnvironment env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiSecurityGroup> listServerSecurityGroups(GeminiTenant tenant, GeminiEnvironment env, GeminiServer server) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiSecurityGroupRule> listSecurityGroupRules(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType getSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType getSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType attachInterface(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType detachInterface(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType attachIpRange(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType detachIpRange(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
