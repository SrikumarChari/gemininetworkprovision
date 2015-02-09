/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.security.openstack;

import com.gemini.domain.common.GeminiSecurityGroupRuleDirection;
import com.gemini.domain.common.IPAddressType;
import com.gemini.domain.common.Protocol;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiSecurityGroup;
import com.gemini.domain.model.GeminiSecurityGroupRule;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.model.GeminiSubnetAllocationPool;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.security.base.SecurityProvider;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ClientResponseException;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.compute.SecGroupExtension.Rule;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;
import org.openstack4j.openstack.OSFactory;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
@Singleton
public class SecurityProviderOpenStackImpl implements SecurityProvider {

    /**
     *
     * @return
     *
     * A brief description of this Security Provider. Currently (2/5/15) returns
     * unsupported functions as part of the text
     */
    @Override
    public String provisioningDesc() {
        return "OpenStack Security Provider. Attach/Detach Interface functions not supported.";
    }

    /**
     *
     * @param tenant
     * @param env
     * @return
     *
     * List all the security groups.
     */
    @Override
    public List<GeminiSecurityGroup> listAllSecurityGroups(GeminiTenant tenant, GeminiEnvironment env) {
        List<GeminiSecurityGroup> listSecGrps = Collections.synchronizedList(new ArrayList());

        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get the list from OpenStack
        List<? extends SecurityGroup> osSecGrps = os.networking().securitygroup().list();
        osSecGrps.stream().filter(osSecGrp -> osSecGrp != null).forEach(osSecGrp -> {
            //see if this security group already exists in the environment
            GeminiSecurityGroup tmpSecGrp = env.getSecurityGroups().stream()
                    .filter(s -> s.getName().equals(osSecGrp.getName()))
                    .findFirst().get();
            GeminiSecurityGroup newGemSecGrp = null;
            if (tmpSecGrp == null) {
                //The OpenStack security group hasn't been mapped to an object on the Gemini side, so create it and add to the environment
                newGemSecGrp = new GeminiSecurityGroup();
                newGemSecGrp.setCloudID(osSecGrp.getId());
                newGemSecGrp.setName(osSecGrp.getName());
                newGemSecGrp.setDescription(osSecGrp.getDescription());
                env.addSecurityGroup(newGemSecGrp);
            }

            //check to see if this group's rules are mapped on the Gemini side
            List<? extends SecurityGroupRule> osSecGrpRules = osSecGrp.getRules();
            final GeminiSecurityGroup gemSecGrp = tmpSecGrp == null ? newGemSecGrp : tmpSecGrp;
            osSecGrpRules.stream().filter(osSecGrpRule -> osSecGrpRule != null).forEach(osSecGrpRule -> {
                GeminiSecurityGroupRule gemSecGrpRule = gemSecGrp.getSecurityRules().stream()
                        .filter(sr -> sr.getCloudID().equals(osSecGrpRule.getId()))
                        .findFirst().get();
                if (gemSecGrpRule == null) {
                    //the rule has not been mapped on the Gemini side, so create it
                    gemSecGrpRule = new GeminiSecurityGroupRule();
                }
                gemSecGrpRule.setCloudID(osSecGrpRule.getId());
                gemSecGrpRule.setPortRangeMin(osSecGrpRule.getPortRangeMin());
                gemSecGrpRule.setPortRangeMax(osSecGrpRule.getPortRangeMax());
                gemSecGrpRule.setProtocol(Protocol.fromString(osSecGrpRule.getProtocol()));
                gemSecGrpRule.setDirection(GeminiSecurityGroupRuleDirection.valueOf(osSecGrpRule.getDirection()));
                gemSecGrpRule.setRemoteGroupId(osSecGrpRule.getRemoteGroupId());
                gemSecGrpRule.setRemoteIpPrefix(osSecGrpRule.getRemoteIpPrefix());
                gemSecGrpRule.setIpAddressType(IPAddressType.valueOf(osSecGrpRule.getEtherType()));
                //gemSecGrpRule.setCidr(osSecGrpRule.getRange().getCidr());
                gemSecGrpRule.setParent(gemSecGrp);
                gemSecGrp.addSecurityRule(gemSecGrpRule);
            });
            listSecGrps.add(gemSecGrp);
        });

        Logger.debug("Successfully retrieved all security groups Tenant: {} Env: {}", tenant.getName(), env.getName());
        return listSecGrps;
    }

    @Override
    public List<GeminiSecurityGroup> listServerSecurityGroups(GeminiTenant tenant, GeminiEnvironment env, GeminiServer server) {
        List<GeminiSecurityGroup> listSecGrps = Collections.synchronizedList(new ArrayList());

        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get the list from OpenStack
        List<? extends SecGroupExtension> osSecGrps = os.compute().securityGroups().listServerGroups(server.getCloudID());
        osSecGrps.stream().forEach(osSecGrp -> {
            //see if this security group already exists in the environment
            GeminiSecurityGroup tmpSecGrp = env.getSecurityGroups().stream()
                    .filter(s -> s.getName().equals(osSecGrp.getName()))
                    .findFirst().get();
            GeminiSecurityGroup newGemSecGrp = null;
            if (tmpSecGrp == null) {
                //The OpenStack security group hasn't been mapped to an object on the Gemini side, so create it and add to the environment
                newGemSecGrp = new GeminiSecurityGroup();
                newGemSecGrp.setCloudID(osSecGrp.getId());
                newGemSecGrp.setName(osSecGrp.getName());
                newGemSecGrp.setDescription(osSecGrp.getDescription());
                env.addSecurityGroup(newGemSecGrp);
            }

            //check to see if this group's rules are mapped on the Gemini side
            List<? extends Rule> osSecGrpRules = osSecGrp.getRules();
            final GeminiSecurityGroup gemSecGrp = tmpSecGrp == null ? newGemSecGrp : tmpSecGrp;
            osSecGrpRules.stream().filter(osSecGrpRule -> osSecGrpRule != null).forEach(osSecGrpRule -> {
                GeminiSecurityGroupRule gemSecGrpRule = gemSecGrp.getSecurityRules().stream()
                        .filter(sr -> sr.getName().equals(osSecGrpRule.getName()))
                        .findFirst().get();
                if (gemSecGrpRule == null) {
                    //the rule has not been mapped on the Gemini side, so create it
                    gemSecGrpRule = new GeminiSecurityGroupRule();
                }
                gemSecGrpRule.setCloudID(osSecGrpRule.getId());
                gemSecGrpRule.setPortRangeMin(osSecGrpRule.getFromPort());
                gemSecGrpRule.setPortRangeMax(osSecGrpRule.getToPort());
                gemSecGrpRule.setProtocol(Protocol.fromString(osSecGrpRule.getIPProtocol().toString()));
                gemSecGrpRule.setCidr(osSecGrpRule.getRange().getCidr());
                gemSecGrp.addSecurityRule(gemSecGrpRule);
            });

            //check if this security group is attached to server on the gemini side
            if (server.getSecGroupNames().stream().noneMatch(s -> s.equals(osSecGrp.getName()))) {
                //it isn't so add it to the server
                server.addSecGroupName(osSecGrp.getName());
            }

            listSecGrps.add(gemSecGrp);
        });

        Logger.debug("Successfully retrieved security groups Tenant: {} Env: {} server {}{",
                tenant.getName(), env.getName(), server.getName());
        return listSecGrps;
    }

    /*
     * Assumes that the GeminiSecurityGroup passed is already mapped.
     */
    @Override
    public List<GeminiSecurityGroupRule> listSecurityGroupRules(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        List<GeminiSecurityGroupRule> listSecGrpRules = Collections.synchronizedList(new ArrayList());

        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        List<? extends SecurityGroupRule> osSecGrpRules = null;
        try {
            osSecGrpRules = os.networking().securityrule().list();
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Failed to get rules for tenant: {} env: {} and security Group {}", tenant.getName(), env.getName(), securityGroup.getName());
            return null;
        }

        osSecGrpRules.stream().filter(osSecGrpRule -> osSecGrpRule != null).forEach(osSecGrpRule -> {
            //find the rule in the security group object and add it to list
            GeminiSecurityGroupRule gemSecGrpRule = securityGroup.getSecurityRules().stream()
                    .filter(sr -> sr.getCloudID().equals(osSecGrpRule.getId())) //check cloud id too to ensure the mapping on Gemini is complete
                    .findFirst().get();
            if (gemSecGrpRule == null) {
                //the rule has not been mapped on the Gemini side, so create it
                gemSecGrpRule = new GeminiSecurityGroupRule();
                gemSecGrpRule.setCloudID(osSecGrpRule.getId());
                gemSecGrpRule.setPortRangeMin(osSecGrpRule.getPortRangeMin());
                gemSecGrpRule.setPortRangeMax(osSecGrpRule.getPortRangeMax());
                gemSecGrpRule.setProtocol(Protocol.fromString(osSecGrpRule.getProtocol()));
//                gemSecGrpRule.setCidr(osSecGrpRule.getRange().getCidr());
                gemSecGrpRule.setDirection(GeminiSecurityGroupRuleDirection.valueOf(osSecGrpRule.getDirection()));
                gemSecGrpRule.setRemoteGroupId(osSecGrpRule.getRemoteGroupId());
                gemSecGrpRule.setRemoteIpPrefix(osSecGrpRule.getRemoteIpPrefix());
                gemSecGrpRule.setIpAddressType(IPAddressType.valueOf(osSecGrpRule.getEtherType()));
                gemSecGrpRule.setParent(securityGroup);
                securityGroup.addSecurityRule(gemSecGrpRule);
            }
            listSecGrpRules.add(gemSecGrpRule);
        });

        Logger.debug("Successfully retrieved security groups rules Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        return listSecGrpRules;
    }

    /**
     *
     * @param tenant
     * @param env
     * @param securityGroup - assumes on the name is available in the object
     * @return
     *
     * Calls open stack to retrieve information about the security group.
     * Typically used when only a security group name is available and the
     * caller wants to get rest of the information
     */
    @Override
    public ProvisioningProviderResponseType getSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        SecurityGroup osSecGrp;
        try {
            osSecGrp = os.networking().securitygroup().get(securityGroup.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Security Group not found: tenant: {} env: {} security group: {}", tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //the basic stuff
        securityGroup.setCloudID(osSecGrp.getId());
        securityGroup.setName(osSecGrp.getName());
        securityGroup.setDescription(osSecGrp.getDescription());

        //now the rules
        List<? extends SecurityGroupRule> osSecGrpRules = osSecGrp.getRules();
        osSecGrpRules.stream().filter(osSecGrpRule -> osSecGrpRule != null).forEach(osSecGrpRule -> {
            GeminiSecurityGroupRule gemSecGrpRule = new GeminiSecurityGroupRule();
            gemSecGrpRule.setCloudID(osSecGrpRule.getId());
            gemSecGrpRule.setPortRangeMin(osSecGrpRule.getPortRangeMin());
            gemSecGrpRule.setPortRangeMax(osSecGrpRule.getPortRangeMax());
            gemSecGrpRule.setProtocol(Protocol.fromString(osSecGrpRule.getProtocol()));
//                gemSecGrpRule.setCidr(osSecGrpRule.getRange().getCidr());
            gemSecGrpRule.setDirection(GeminiSecurityGroupRuleDirection.valueOf(osSecGrpRule.getDirection()));
            gemSecGrpRule.setRemoteGroupId(osSecGrpRule.getRemoteGroupId());
            gemSecGrpRule.setRemoteIpPrefix(osSecGrpRule.getRemoteIpPrefix());
            gemSecGrpRule.setIpAddressType(IPAddressType.valueOf(osSecGrpRule.getEtherType()));
            gemSecGrpRule.setParent(securityGroup);
            securityGroup.addSecurityRule(gemSecGrpRule);
        });
        Logger.debug("Successfully retrieved security group information Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    /**
     *
     * @param tenant
     * @param env
     * @param securityGroup - the security group to be created
     * @return SUCCESS, AUTH_FAILURE, CLOUD_FAILURE, OBJECT_EXISTS
     *
     * Creates the Security group in OpenStack if it doesn't exist NOTE: it
     * creates all security rules in the group. A separate call to create rules
     * IS NOT required
     */
    @Override
    public ProvisioningProviderResponseType createSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        SecurityGroup osSecGrp;
        try {
            osSecGrp = os.networking().securitygroup().get(securityGroup.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Could not retrieve security group information in OpenStack. tenant: {} env: {} security group: {}", tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        if (osSecGrp != null) {
            Logger.error("Security Group exists: tenant: {} env: {} security group: {}", tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.OBJECT_EXISTS;
        }

        //create the security group
        osSecGrp = os.networking().securitygroup()
                .create(Builders.securityGroup()
                        .tenantId(tenant.getTenantID())
                        .name(securityGroup.getName())
                        .description(securityGroup.getDescription()).build());
        if (osSecGrp == null) {
            Logger.error("Security Group creation failure tenant: {} env: {} security group: {}", tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        //map the object and then create the security group rules in the cloud
        securityGroup.setCloudID(osSecGrp.getId());
        securityGroup.getSecurityRules().stream().forEach(s -> {
            SecurityGroupRule r = os.networking().securityrule()
                    .create(Builders.securityGroupRule()
                            .tenantId(tenant.getTenantID())
                            .securityGroupId(securityGroup.getCloudID())
                            .direction(s.getDirection().toString())
                            .ethertype(s.getIpAddressType().toString())
                            .portRangeMin(s.getPortRangeMin())
                            .portRangeMin(s.getPortRangeMin())
                            .protocol(s.getProtocol().toString())
                            .remoteGroupId(s.getRemoteGroupId())
                            .remoteIpPrefix(s.getRemoteIpPrefix())
                            .build());
            if (r == null) {
                Logger.error("Security Group rule creation failure. tenant: {} env: {} security group: {} rule {}",
                        tenant.getName(), env.getName(), securityGroup.getName(), s.getName());
            }
        });

        Logger.debug("Successfully created security group Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public ProvisioningProviderResponseType updateSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        SecurityGroup osSecGrp;
        try {
            osSecGrp = os.networking().securitygroup().get(securityGroup.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Could not retrieve security group information in OpenStack. tenant: {} env: {} security group: {}", tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        if (osSecGrp == null) {
            Logger.error("Security Group does not exist: tenant: {} env: {} security group: {}",
                    tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //looks like there no way to update a security group, we have to delete and re-create
        ActionResponse a = os.networking().securitygroup().delete(osSecGrp.getId());
        if (!a.isSuccess()) {
            Logger.error("Security Group update - could not delete the security group: tenant: {} env: {} security group: {} Error Message: {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), a.getFault());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        //now re-create the group with the updated info
        //create the security group
        osSecGrp = os.networking().securitygroup()
                .create(Builders.securityGroup()
                        .tenantId(tenant.getTenantID())
                        .name(securityGroup.getName())
                        .description(securityGroup.getDescription()).build());
        if (osSecGrp == null) {
            Logger.error("Security Group update creation failure. tenant: {} env: {} security group: {}", tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        //update the cloud ID since it has most likely changed and then create the security group rules in the cloud
        securityGroup.setCloudID(osSecGrp.getId());
        securityGroup.getSecurityRules().stream().forEach(s -> {
            SecurityGroupRule r = os.networking().securityrule()
                    .create(Builders.securityGroupRule()
                            .tenantId(tenant.getTenantID())
                            .securityGroupId(securityGroup.getCloudID())
                            .direction(s.getDirection().toString())
                            .ethertype(s.getIpAddressType().toString())
                            .portRangeMin(s.getPortRangeMin())
                            .portRangeMin(s.getPortRangeMin())
                            .protocol(s.getProtocol().toString())
                            .remoteGroupId(s.getRemoteGroupId())
                            .remoteIpPrefix(s.getRemoteIpPrefix())
                            .build());
            if (r == null) {
                Logger.error("Security Group rule creation failure. tenant: {} env: {} security group: {} rule {}",
                        tenant.getName(), env.getName(), securityGroup.getName(), s.getName());
            }
        });

        Logger.debug("Successfully updated security group Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public ProvisioningProviderResponseType deleteSecurityGroup(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        SecurityGroup osSecGrp;
        try {
            osSecGrp = os.networking().securitygroup().get(securityGroup.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Could not retrieve security group information in OpenStack. tenant: {} env: {} security group: {}",
                    tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        if (osSecGrp == null) {
            Logger.error("Security Group does not exist: tenant: {} env: {} security group: {}",
                    tenant.getName(), env.getName(), securityGroup.getName());
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //looks like there no way to update a security group, we have to delete and re-create
        ActionResponse a = os.networking().securitygroup().delete(osSecGrp.getId());
        if (!a.isSuccess()) {
            Logger.error("Security Group update - could not delete the security group: tenant: {} env: {} security group: {} Error Message: {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), a.getFault());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        //remove this security group from the environment
        env.deleteSecurityGroup(securityGroup);

        //now remove the deleted security group's name from the servers that use the deleted security group
        env.getApplications()
                .stream()
                .map(GeminiApplication::getNetworks)
                .flatMap(List::stream)
                .map(GeminiNetwork::getSubnets)
                .flatMap(List::stream)
                .map(GeminiSubnet::getAllocationPools)
                .flatMap(List::stream)
                .map(GeminiSubnetAllocationPool::getServers)
                .flatMap(List::stream)
                .forEach(s -> s.deleteSecGroupName(securityGroup.getName()));

        Logger.debug("Successfully deleted security group Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    /**
     * Get the security group rule details based on the cloud id provided in the
     * securityRule object.
     *
     * @param tenant
     * @param env
     * @param securityGroup - the parent security group
     * @param securityRule - the rule whose information needs to be retrieved,
     * the cloud ID must be provided.
     * @return SUCCESS - if all goes well AUTH_FAILURE - if the authentication
     * failed with the information in the tenant object EXCEPTION - if the cloud
     * raised an exception OBJECT_NOT_FOUND - if the rule with ID provided is
     * not found
     */
    @Override
    public ProvisioningProviderResponseType getSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        SecurityGroupRule osSecGrpRule;
        try {
            osSecGrpRule = os.networking().securityrule().get(securityRule.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Cloud Exception: Could not retrieve security group rule information in OpenStack. tenant: {} env: {} security group: {} security rule ID: {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getCloudID());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        if (osSecGrpRule == null) {
            Logger.error("Security rule not found: Could not find security group rule information in OpenStack. tenant: {} env: {} security group: {} security rule ID: {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getCloudID());
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //now copy the information into the gemini security rule object
        securityRule.setDirection(GeminiSecurityGroupRuleDirection.valueOf(osSecGrpRule.getDirection()));
        securityRule.setIpAddressType(IPAddressType.valueOf(osSecGrpRule.getEtherType()));
        securityRule.setParent(securityGroup);
        securityRule.setPortRangeMin(osSecGrpRule.getPortRangeMin());
        securityRule.setPortRangeMax(osSecGrpRule.getPortRangeMax());
        securityRule.setProtocol(Protocol.valueOf(osSecGrpRule.getProtocol()));
        securityRule.setRemoteGroupId(osSecGrpRule.getRemoteGroupId());
        securityRule.setRemoteIpPrefix(osSecGrpRule.getRemoteIpPrefix());

        Logger.debug("Successfully retrieved security groups rule Tenant: {} Env: {} security group {} security rule {}",
                tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    /**
     * createSecurityGroupRule. Creates the security rule in the cloud. It is
     * expected that the security rule object has all the relevant required for
     * the creation.
     *
     * @param tenant - contains the auth information
     * @param env - contains the security group
     * @param securityGroup - the parent security group
     * @param securityRule - represents the rule to be created in the cloud. All
     * values must be available in the object. The cloud provider may not create
     * it with partial information
     * @return SUCCESS - if all goes well, AUTH_FAILURE - if the authentication
     * failed with the information in the tenant object, EXCEPTION - if the
     * cloud raised an exception CLOUD_FAILURE - if the creation did not succeed
     */
    @Override
    public ProvisioningProviderResponseType createSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        SecurityGroupRule r;
        try {
            r = os.networking().securityrule()
                    .create(Builders.securityGroupRule()
                            .tenantId(tenant.getTenantID())
                            .securityGroupId(securityGroup.getCloudID())
                            .direction(securityRule.getDirection().toString())
                            .ethertype(securityRule.getIpAddressType().toString())
                            .portRangeMin(securityRule.getPortRangeMin())
                            .portRangeMin(securityRule.getPortRangeMin())
                            .protocol(securityRule.getProtocol().toString())
                            .remoteGroupId(securityRule.getRemoteGroupId())
                            .remoteIpPrefix(securityRule.getRemoteIpPrefix())
                            .build());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Cloud Exception: Could not creation the security group rule in OpenStack. tenant: {} env: {} security group: {} security rule ID: {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getCloudID());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }
        if (r == null) {
            Logger.error("Security Group rule creation failure. tenant: {} env: {} security group: {} rule {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        Logger.debug("Successfully created security group rule Tenant: {} Env: {} security group {} security rule {}",
                tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    /**
     * updateSecurityGroupRule - updates security rule information in the cloud.
     *
     * Currently OpenStack DOES NOT allow update of a security rule. This
     * function deletes the rule and recreates it with the updated information.
     *
     * @param tenant
     * @param env
     * @param securityGroup
     * @param securityRule
     * @return
     */
    @Override
    public ProvisioningProviderResponseType updateSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //since openstack doesn't allow update, we need to delete and re-create
        try {
            os.networking().securityrule().delete(securityRule.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Failed to delete security group rule Tenant: {} Env: {}, Security Group: {} security rule {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }
        SecurityGroupRule r;
        try {
            r = os.networking().securityrule()
                    .create(Builders.securityGroupRule()
                            .tenantId(tenant.getTenantID())
                            .securityGroupId(securityGroup.getCloudID())
                            .direction(securityRule.getDirection().toString())
                            .ethertype(securityRule.getIpAddressType().toString())
                            .portRangeMin(securityRule.getPortRangeMin())
                            .portRangeMin(securityRule.getPortRangeMin())
                            .protocol(securityRule.getProtocol().toString())
                            .remoteGroupId(securityRule.getRemoteGroupId())
                            .remoteIpPrefix(securityRule.getRemoteIpPrefix())
                            .build());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Cloud Exception: Could not creation the security group rule in OpenStack. tenant: {} env: {} security group: {} security rule ID: {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getCloudID());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }
        if (r == null) {
            Logger.error("Security Group rule creation failure. tenant: {} env: {} security group: {} rule {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        Logger.debug("Successfully updated security group rule Tenant: {} Env: {} security group {} security rule {}",
                tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public ProvisioningProviderResponseType deleteSecurityGroupRule(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup, GeminiSecurityGroupRule securityRule) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //since openstack doesn't allow update, we need to delete and re-create
        try {
            os.networking().securityrule().delete(securityRule.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Failed to delete security group rule Tenant: {} Env: {}, Security Group: {} security rule {}",
                    tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }
        Logger.debug("Successfully deleted security group rule Tenant: {} Env: {} security group {} security rule {}",
                tenant.getName(), env.getName(), securityGroup.getName(), securityRule.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public ProvisioningProviderResponseType attachInterface(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        Logger.error("attachInterface feature not supported in OpenStack. Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        throw new UnsupportedOperationException("attachInterface feature not supported in OpenStack."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType detachInterface(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        Logger.error("detachInterface feature not supported in OpenStack. Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        throw new UnsupportedOperationException("detachInterface feature not supported in OpenStack."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType attachIpRange(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        Logger.error("attachIpRange feature not supported in OpenStack. Use the createSecurityGroupRule function instead"
                + "Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        throw new UnsupportedOperationException("attachIpRange feature not supported in OpenStack. Create a new GeminiSecurityRule and add it to the security group"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType detachIpRange(GeminiTenant tenant, GeminiEnvironment env, GeminiSecurityGroup securityGroup) {
        Logger.error("detachIpRange feature not supported in OpenStack. Use deleteSecurityGroupRule instead. "
                + "Tenant: {} Env: {} security group {}",
                tenant.getName(), env.getName(), securityGroup.getName());
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
