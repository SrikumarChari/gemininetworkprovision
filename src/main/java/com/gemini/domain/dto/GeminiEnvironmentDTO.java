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
    private List<GeminiNetworkDTO> networks  = Collections.synchronizedList(new ArrayList());
    private List<GeminiServerDTO> servers = Collections.synchronizedList(new ArrayList());
    private List<GeminiNetworkRouterDTO> routes = Collections.synchronizedList(new ArrayList());

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

    public List<GeminiNetworkDTO> getNetworks() {
        return networks;
    }

    public void setNetworks(List<GeminiNetworkDTO> networks) {
        this.networks = networks;
    }

    public List<GeminiServerDTO> getServers() {
        return servers;
    }

    public void setServers(List<GeminiServerDTO> servers) {
        this.servers = servers;
    }    

    public GeminiNetworkDTO getGateway() {
        return gateway;
    }

    public void setGateway(GeminiNetworkDTO gateway) {
        this.gateway = gateway;
    }

    public List<GeminiNetworkRouterDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GeminiNetworkRouterDTO> routes) {
        this.routes = routes;
    }
}
