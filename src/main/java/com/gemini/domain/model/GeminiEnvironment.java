/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.common.repository.EntityMongoDB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class GeminiEnvironment extends EntityMongoDB {

    //rackspace, openstack, etc.
    private String name;
    private GeminiEnvironmentType type;
    
    @Reference
    private List<GeminiNetwork> gateways = Collections.synchronizedList(new ArrayList());;

    @Embedded
    private List<GeminiApplication> applications = Collections.synchronizedList(new ArrayList());

    @Reference
    private List<GeminiNetwork> orphanNetworks = new ArrayList();

//
//    @Reference
//    private List<GeminiServer> servers = new ArrayList();

    @Reference
    private List<GeminiSecurityGroup> securityGroups = Collections.synchronizedList(new ArrayList());
    
    @Reference
    private List<GeminiServerImage> serverImages = Collections.synchronizedList(new ArrayList());
    
    @Reference
    private List<GeminiServerType> serverTypes = Collections.synchronizedList(new ArrayList());

    @Reference
    private List<GeminiNetworkRouter> routers = Collections.synchronizedList(new ArrayList());

    public GeminiEnvironmentType getType() {
        return type;
    }

    public void setType(GeminiEnvironmentType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GeminiNetwork> getGateways() {
        return gateways;
    }

    public void setGateways(List<GeminiNetwork> gateways) {
        this.gateways = gateways;
    }

    public boolean addGateway(GeminiNetwork gateway) {
        if (gateways.stream().noneMatch(g -> g.getName().equals(gateway.getName()))) {
            return gateways.add(gateway);
        } else {
            return false;
        }
    }
    
    public boolean deleteGateway(GeminiNetwork gateway) {
        return gateways.removeIf(g -> g.getName().equals(gateway.getName()));
    }

    public List<GeminiApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<GeminiApplication> applications) {
        this.applications = applications;
    }

    public boolean addApplication(GeminiApplication app) {
        if (applications.stream().noneMatch(a -> a.getName().equals(app.getName()))) {
            return applications.add(app);
        } else {
            return false;
        }
    }

    public boolean deleteApplication(GeminiApplication app) {
        return applications.removeIf(a -> a.getName().equals(app.getName()));
    }

    public List<GeminiNetwork> getOrphanNetworks() {
        return orphanNetworks;
    }

    public void setOrphanNetworks(List<GeminiNetwork> orphanNetworks) {
        this.orphanNetworks = orphanNetworks;
    }

    public boolean addOrphanNetwork(GeminiNetwork net) {
        if (orphanNetworks.stream().noneMatch(n -> n.getName().equals(net.getName()))) {
            return orphanNetworks.add(net);
        } else {
            return false;
        }
    }

    public boolean deleteOrphanNetwork(GeminiNetwork net) {
        return orphanNetworks.removeIf(n -> n.getName().equals(net.getName()));
    }
//
//    public List<GeminiServer> getServers() {
//        return servers;
//    }
//
//    public void setServers(List<GeminiServer> servers) {
//        this.servers = servers;
//    }
//
//    public boolean addServer(GeminiServer srv) {
//        if(servers.stream().filter(s -> s.getName().equals(srv.getName())).count() == 0) {
//            return servers.add(srv);
//        } else {
//            return false;
//        }
//    }
//
//    public boolean deleteServer(GeminiServer srv) {
//        return servers.removeIf(s -> s.getName().equals(srv.getName()));
//    }

    public List<GeminiNetworkRouter> getRouters() {
        return routers;
    }

    public void setRouters(List<GeminiNetworkRouter> routers) {
        this.routers = routers;
    }
    
    public boolean addRouter (GeminiNetworkRouter router) {
        if (routers.stream().filter(r -> r.getName().equals(router.getName())).count() == 0) {
            return routers.add(router);
        } else {
            return false;
        }
    }
    
    public boolean deleteRouter(GeminiNetworkRouter router) {
        return routers.removeIf(r -> r.getName().equals(router.getName()));
    }

    public List<GeminiSecurityGroup> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<GeminiSecurityGroup> securityGroups) {
        this.securityGroups = securityGroups;
    }
    
    public boolean addSecurityGroup (GeminiSecurityGroup secGroup) {
        if (securityGroups.stream().noneMatch(r -> r.getName().equals(secGroup.getName()))) {
            return securityGroups.add(secGroup);
        } else {
            return false;
        }
    }
    
    public boolean deleteSecurityGroup(GeminiSecurityGroup secGroup) {
        return securityGroups.removeIf(r -> r.getName().equals(secGroup.getName()));
    }

    public List<GeminiServerImage> getServerImages() {
        return serverImages;
    }

    public void setServerImages(List<GeminiServerImage> serverImages) {
        this.serverImages = serverImages;
    }
    
    public boolean addServerImage(GeminiServerImage serverImage) {
        if (serverImages.stream().noneMatch(s -> s.getName().equals(serverImage.getName()))) {
            return serverImages.add(serverImage);
        } else {
            return false;
        }
    }
    
    public boolean deleteServerImage(GeminiServerImage serverImage) {
        return serverImages.removeIf(s -> s.getName().equals(serverImage.getName()));
    }

    public List<GeminiServerType> getServerTypes() {
        return serverTypes;
    }

    public void setServerTypes(List<GeminiServerType> serverTypes) {
        this.serverTypes = serverTypes;
    }

    public boolean addServerType(GeminiServerType serverType) {
        if (serverTypes.stream().noneMatch(s -> s.getName().equals(serverType.getName()))) {
            return serverTypes.add(serverType);
        } else {
            return false;
        }
    }
    
    public boolean deleteServerType(GeminiServerType serverType) {
        return serverTypes.removeIf(s -> s.getName().equals(serverType.getName()));
    }

}
