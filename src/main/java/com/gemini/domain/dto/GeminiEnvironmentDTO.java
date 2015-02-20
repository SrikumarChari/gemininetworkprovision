/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author schari
 */
public class GeminiEnvironmentDTO extends GeminiBaseDTO {

    private String name;
    private String type;
    private String adminUserName;
    private String adminPassword;
    private String endPoint;

    private List<GeminiNetworkDTO> gateways = Collections.synchronizedList(new ArrayList());

    private List<GeminiApplicationDTO> applications = Collections.synchronizedList(new ArrayList());
    private List<GeminiNetworkRouterDTO> routers = Collections.synchronizedList(new ArrayList());
    private List<GeminiSecurityGroupDTO> securityGroups = Collections.synchronizedList(new ArrayList());
    private List<GeminiNetworkDTO> orphanNetworks = Collections.synchronizedList(new ArrayList());

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public List<GeminiApplicationDTO> getApplications() {
        return applications;
    }

    public void setApplications(List<GeminiApplicationDTO> applications) {
        this.applications = applications;
    }

    public boolean addApplication(GeminiApplicationDTO app) {
        if (applications.stream().noneMatch(a -> a.getName().equals(app.getName()))) {
            return applications.add(app);
        } else {
            return false;
        }
    }

    public boolean deleteApplication(GeminiApplicationDTO app) {
        return applications.removeIf(a -> a.getName().equals(app.getName()));
    }

    public List<GeminiNetworkDTO> getGateways() {
        return gateways;
    }

    public void setGateways(List<GeminiNetworkDTO> gateways) {
        this.gateways = gateways;
    }

    public boolean addGateway(GeminiNetworkDTO gateway) {
        if (gateways.stream().noneMatch(g -> g.getName().equals(gateway.getName()))) {
            return gateways.add(gateway);
        } else {
            return false;
        }
    }

    public boolean deleteGateway(GeminiNetworkDTO gateway) {
        return gateways.removeIf(g -> g.getName().equals(gateway.getName()));
    }

//
//    public boolean deleteNetwork(GeminiNetworkDTO net) {
//        return networks.removeIf(n -> n.getName().equals(net.getName()));
//    }
//
//    public List<GeminiServerDTO> getServers() {
//        return servers;
//    }
//
//    public void setServers(List<GeminiServerDTO> servers) {
//        this.servers = servers;
//    }
//
//    public boolean addServer(GeminiServerDTO srv) {
//        if (servers.stream().filter(s -> s.getName().equals(srv.getName())).count() == 0) {
//            return servers.add(srv);
//        } else {
//            return false;
//        }
//    }
//
//    public boolean deleteServer(GeminiServerDTO srv) {
//        return servers.removeIf(s -> s.getName().equals(srv.getName()));
//    }
    public List<GeminiNetworkRouterDTO> getRouters() {
        return routers;
    }

    public void setRouters(List<GeminiNetworkRouterDTO> routers) {
        this.routers = routers;
    }

    public boolean addRouter(GeminiNetworkRouterDTO router) {
        //this function provided only for a java client - it is not used by the mapper
        if (routers.stream().noneMatch(r -> r.getName().equals(router.getName()))) {
            return routers.add(router);
        } else {
            return false;
        }
    }

    public boolean deleteRouter(GeminiNetworkRouterDTO router) {
        //this function provided only for a java client - it is not used by the mapper
        return routers.removeIf(r -> r.getName().equals(router.getName()));
    }

    public List<GeminiSecurityGroupDTO> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<GeminiSecurityGroupDTO> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public boolean addSecurityGroup(GeminiSecurityGroupDTO secGroup) {
        if (securityGroups.stream().noneMatch(r -> r.getName().equals(secGroup.getName()))) {
            return securityGroups.add(secGroup);
        } else {
            return false;
        }
    }

    public boolean deleteSecurityGroup(GeminiSecurityGroupDTO secGroup) {
        return securityGroups.removeIf(r -> r.getName().equals(secGroup.getName()));
    }

    public List<GeminiNetworkDTO> getOrphanNetworks() {
        return orphanNetworks;
    }

    public void setOrphanNetworks(List<GeminiNetworkDTO> orphanNetworks) {
        this.orphanNetworks = orphanNetworks;
    }

    public boolean addOrphanNetwork(GeminiNetworkDTO orphanNetwork) {
        if (orphanNetworks.stream().noneMatch(r -> r.getName().equals(orphanNetwork.getName()))) {
            return orphanNetworks.add(orphanNetwork);
        } else {
            return false;
        }
    }

    public boolean deleteOrphanNetwork(GeminiNetworkDTO orphanNetwork) {
        return orphanNetworks.removeIf(r -> r.getName().equals(orphanNetwork.getName()));
    }

}
