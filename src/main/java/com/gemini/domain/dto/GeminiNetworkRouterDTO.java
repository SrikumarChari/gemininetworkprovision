/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schari
 */
public class GeminiNetworkRouterDTO extends GeminiBaseDTO {

    private String name;
    private String status;
    private GeminiNetworkDTO gateway;
    private Map<String, String> routes = Collections.synchronizedMap(new HashMap<String, String>());
    private List<GeminiSubnetDTO> interfaces = Collections.synchronizedList(new ArrayList());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GeminiNetworkDTO getGateway() {
        return gateway;
    }

    public void setGateway(GeminiNetworkDTO gateway) {
        this.gateway = gateway;
    }

    public Map<String, String> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, String> routes) {
        this.routes = routes;
    }

    public boolean addRouter(String nextHop, String dest) {
        String v = routes.get(nextHop);
        if (v == null || !v.equals(dest)) {
            routes.put(nextHop, dest);
            return true;
        }
        return false;
    }

    public boolean deleteRouter(String nextHop, String dest) {
        return routes.remove(nextHop, dest);
    }

    public List<GeminiSubnetDTO> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<GeminiSubnetDTO> interfaces) {
        this.interfaces = interfaces;
    }

    public boolean addInterface(GeminiSubnetDTO subnet) {
        if (interfaces.stream().filter(s -> s.equals(subnet)).count() == 0) {
            return interfaces.add(subnet);
        } else {
            return false;
        }
    }

    public boolean deleteInterface(GeminiSubnetDTO subnet) {
        return interfaces.removeIf(s -> s.equals(subnet));
    }
}
