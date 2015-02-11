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
import com.gemini.domain.model.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import java.util.List;

/**
 *
 * @author schari
 */
public interface SecurityProvider {
    String provisioningDesc();
    
    //list functions
    List<GeminiSecurityGroup> listAllSecurityGroups(GeminiTenant tenant, GeminiEnvironment env);
    List<GeminiSecurityGroup> listServerSecurityGroups (GeminiTenant tenant, GeminiEnvironment env, GeminiServer server);
    List<GeminiSecurityGroupRule> listSecurityGroupRules(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    
    //Security group CRUD functions
    ProvisioningProviderResponseType getSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    ProvisioningProviderResponseType createSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    ProvisioningProviderResponseType updateSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    ProvisioningProviderResponseType deleteSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    
    //security group rule CRUD functions
    ProvisioningProviderResponseType getSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule);
    ProvisioningProviderResponseType createSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule);
    ProvisioningProviderResponseType updateSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule);
    ProvisioningProviderResponseType deleteSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule);
    
    //action functions
    ProvisioningProviderResponseType attachInterface(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    ProvisioningProviderResponseType detachInterface(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    ProvisioningProviderResponseType attachIpRange(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
    ProvisioningProviderResponseType detachIpRange(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup);
}
