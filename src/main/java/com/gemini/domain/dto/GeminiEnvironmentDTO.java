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

    private String type;
    private String name;
    private GeminiNetworkDTO gateway;

    private List<GeminiApplicationDTO> applications = Collections.synchronizedList(new ArrayList());
    private List<GeminiNetworkDTO> networks = Collections.synchronizedList(new ArrayList());
    private List<GeminiServerDTO> servers = Collections.synchronizedList(new ArrayList());
    private List<GeminiNetworkRouterDTO> routers = Collections.synchronizedList(new ArrayList());

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

    public List<GeminiApplicationDTO> getApplications() {
        return applications;
    }

    public void setApplications(List<GeminiApplicationDTO> applications) {
        this.applications = applications;
    }
    
    public boolean addApplication(GeminiApplicationDTO app) {
        if (applications.stream().filter(a -> a.getName().equals(app.getName())).count() == 0) {
            return applications.add(app);
        } else {
            return false;
        }
    }

    public boolean deleteApplication(GeminiApplicationDTO app) {
        return applications.removeIf(a -> a.getName().equals(app.getName()));
    }
    
    public List<GeminiNetworkDTO> getNetworks() {
        return networks;
    }

    public void setNetworks(List<GeminiNetworkDTO> networks) {
        this.networks = networks;
    }

    public boolean addNetwork(GeminiNetworkDTO net) {
        if (networks.stream().filter(n -> n.getName().equals(net)).count() == 0) {
            return networks.add(net);
        } else {
            return false;
        }
    }

    public boolean deleteNetwork(GeminiNetworkDTO net) {
        return networks.removeIf(n -> n.getName().equals(net.getName()));
    }

    public List<GeminiServerDTO> getServers() {
        return servers;
    }

    public void setServers(List<GeminiServerDTO> servers) {
        this.servers = servers;
    }

    public boolean addServer(GeminiServerDTO srv) {
        if (servers.stream().filter(s -> s.getName().equals(srv.getName())).count() == 0) {
            return servers.add(srv);
        } else {
            return false;
        }
    }

    public boolean deleteServer(GeminiServerDTO srv) {
        return servers.removeIf(s -> s.getName().equals(srv.getName()));
    }

    public GeminiNetworkDTO getGateway() {
        return gateway;
    }

    public void setGateway(GeminiNetworkDTO gateway) {
        this.gateway = gateway;
    }

    public List<GeminiNetworkRouterDTO> getRoutes() {
        return routers;
    }

    public void setRoutes(List<GeminiNetworkRouterDTO> routes) {
        this.routers = routes;
    }

    public boolean addRouter(GeminiNetworkRouterDTO router) {
        //this function provided only for a java client - it is not used by the mapper
        if (routers.stream().filter(r -> r.getName().equals(router.getName())).count() == 0) {
            return routers.add(router);
        } else {
            return false;
        }
    }

    public boolean deleteRouter(GeminiNetworkRouterDTO router) {
        //this function provided only for a java client - it is not used by the mapper
        return routers.removeIf(r -> r.getName().equals(router.getName()));
    }
}
