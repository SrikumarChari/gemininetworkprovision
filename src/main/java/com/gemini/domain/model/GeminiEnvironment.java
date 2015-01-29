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
    private GeminiNetwork gateway;

    @Embedded
    private List<GeminiApplication> applications = Collections.synchronizedList(new ArrayList());

//    @Reference
//    private List<GeminiNetwork> networks = new ArrayList();
//
//    @Reference
//    private List<GeminiServer> servers = new ArrayList();

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

    public GeminiNetwork getGateway() {
        return gateway;
    }

    public void setGateway(GeminiNetwork gateway) {
        this.gateway = gateway;
    }

    public List<GeminiApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<GeminiApplication> applications) {
        this.applications = applications;
    }

    public boolean addApplication(GeminiApplication app) {
        if (applications.stream().filter(a -> a.getName().equals(app.getName())).count() == 0) {
            return applications.add(app);
        } else {
            return false;
        }
    }

    public boolean deleteApplication(GeminiApplication app) {
        return applications.removeIf(a -> a.getName().equals(app.getName()));
    }

//    public List<GeminiNetwork> getNetworks() {
//        return networks;
//    }
//
//    public void setNetworks(List<GeminiNetwork> networks) {
//        this.networks = networks;
//    }
//
//    public boolean addNetwork(GeminiNetwork net) {
//        if (networks.stream().filter(n -> n.getName().equals(net.getName())).count() == 0) {
//            return networks.add(net);
//        } else {
//            return false;
//        }
//    }
//
//    public boolean deleteNetwork(GeminiNetwork net) {
//        return networks.removeIf(n -> n.getName().equals(net.getName()));
//    }
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
}
