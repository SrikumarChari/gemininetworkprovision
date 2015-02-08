/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.mapper;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiDiscoveryNetworkDTO;
import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.dto.GeminiLinkDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiNetworkRouterDTO;
import com.gemini.domain.dto.GeminiSecurityGroupDTO;
import com.gemini.domain.dto.GeminiSecurityGroupRuleDTO;
import com.gemini.domain.dto.GeminiServerDTO;
import com.gemini.domain.dto.GeminiServerImageDTO;
import com.gemini.domain.dto.GeminiServerTypeDTO;
import com.gemini.domain.dto.GeminiTenantDTO;
import com.gemini.domain.dto.GeminiTenantUserDTO;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiDiscoveryNetwork;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiLink;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiNetworkRouter;
import com.gemini.domain.model.GeminiSecurityGroup;
import com.gemini.domain.model.GeminiSecurityGroupRule;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.model.GeminiServerImage;
import com.gemini.domain.model.GeminiServerType;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.domain.tenant.GeminiTenantUser;
import com.google.inject.Inject;
import org.dozer.DozerBeanMapper;

/**
 *
 * @author schari
 */
public class GeminiMapper {

    @Inject
    private DozerBeanMapper mapper;

    public GeminiApplication getAppFromDTO(GeminiApplicationDTO appDTO) {
        GeminiApplication newApp = mapper.map(appDTO, GeminiApplication.class);
        return newApp;
    }

    public GeminiApplicationDTO getDTOFromApp(GeminiApplication app) {
        GeminiApplicationDTO newAppDTO = mapper.map(app, GeminiApplicationDTO.class);
        return newAppDTO;
    }

    public GeminiDiscoveryNetwork getDiscNetFromDTO(GeminiDiscoveryNetworkDTO discNetDTO) {
        GeminiDiscoveryNetwork discNet = mapper.map(discNetDTO, GeminiDiscoveryNetwork.class);
        return discNet;
    }

    public GeminiDiscoveryNetworkDTO getDTOFromDiscNet(GeminiDiscoveryNetwork discNet) {
        GeminiDiscoveryNetworkDTO discNetDTO = mapper.map(discNet, GeminiDiscoveryNetworkDTO.class);
        return discNetDTO;
    }

    public GeminiEnvironmentDTO getDTOFromEnv(GeminiEnvironment env) {
        GeminiEnvironmentDTO envDTO = mapper.map(env, GeminiEnvironmentDTO.class);
        return envDTO;
    }

    public GeminiEnvironment getEnvFromDTO(GeminiEnvironmentDTO envDTO) {
        GeminiEnvironment env = mapper.map(envDTO, GeminiEnvironment.class);
        return env;
    }

    public GeminiNetwork getNetworkFromDTO(GeminiNetworkDTO netDTO) {
        GeminiNetwork net = mapper.map(netDTO, GeminiNetwork.class);
        return net;
    }

    public GeminiNetworkDTO getDTOFromNetwork(GeminiNetwork net) {
        GeminiNetworkDTO netDTO = mapper.map(net, GeminiNetworkDTO.class);
        return netDTO;
    }

    public GeminiNetworkRouter getNetRouteFromDTO(GeminiNetworkRouterDTO routeDTO) {
        return mapper.map(routeDTO, GeminiNetworkRouter.class);
    }

    public GeminiNetworkRouterDTO getDTOFromNetRoute(GeminiNetworkRouter route) {
        return mapper.map(route, GeminiNetworkRouterDTO.class);
    }

    public GeminiServer getServerFromDTO(GeminiServerDTO srvDTO) {
        GeminiServer srv = mapper.map(srvDTO, GeminiServer.class);
        return srv;
    }

    public GeminiServerDTO getDTOFromServer(GeminiServer srv) {
        GeminiServerDTO srvDTO = mapper.map(srv, GeminiServerDTO.class);
        return srvDTO;
    }

    public GeminiTenant getTenantFromDTO(GeminiTenantDTO tenantDTO) {
        GeminiTenant tenant = mapper.map(tenantDTO, GeminiTenant.class);
        return tenant;
    }

    public GeminiTenantDTO getDTOFromTenant(GeminiTenant tenant) {
        GeminiTenantDTO tenantDTO = mapper.map(tenant, GeminiTenantDTO.class);
        return tenantDTO;
    }

    public GeminiTenantUser getTenantUserFromDTO(GeminiTenantUserDTO tenantUserDTO) {
        return mapper.map(tenantUserDTO, GeminiTenantUser.class);
    }

    public GeminiTenantUserDTO getDTOFromTenantUser(GeminiTenantUser tenantUser) {
        return mapper.map(tenantUser, GeminiTenantUserDTO.class);
    }

    public GeminiSecurityGroup getSecurityGroupFromDTO(GeminiSecurityGroupDTO secGroupDTO) {
        return mapper.map(secGroupDTO, GeminiSecurityGroup.class);
    }

    public GeminiSecurityGroupDTO getDTOFromSecurityGroup(GeminiSecurityGroup secGroup) {
        return mapper.map(secGroup, GeminiSecurityGroupDTO.class);
    }

    public GeminiSecurityGroupRule getSecurityGroupRuleFromDTO(GeminiSecurityGroupRuleDTO secGrpRuleDTO) {
        return mapper.map(secGrpRuleDTO, GeminiSecurityGroupRule.class);
    }

    public GeminiSecurityGroupRuleDTO getDTOSecurityGroupRule(GeminiSecurityGroupRule secGrpRuleDTO) {
        return mapper.map(secGrpRuleDTO, GeminiSecurityGroupRuleDTO.class);
    }

    public GeminiLink getLinkFromDTO(GeminiLinkDTO linkDTO) {
        return mapper.map(linkDTO, GeminiLink.class);
    }

    public GeminiLinkDTO getDTOFromLink(GeminiLink link) {
        return mapper.map(link, GeminiLinkDTO.class);
    }

    public GeminiServerImage getServerImageFromDTO(GeminiServerImageDTO linkDTO) {
        return mapper.map(linkDTO, GeminiServerImage.class);
    }

    public GeminiServerImageDTO getDTOFromServerImage(GeminiServerImage link) {
        return mapper.map(link, GeminiServerImageDTO.class);
    }

    public GeminiServerType getServerTypeFromDTO(GeminiServerTypeDTO srvImageDTO) {
        return mapper.map(srvImageDTO, GeminiServerType.class);
    }

    public GeminiServerTypeDTO getDTOFromServerImage(GeminiServerType srvImage) {
        return mapper.map(srvImage, GeminiServerTypeDTO.class);
    }

}
