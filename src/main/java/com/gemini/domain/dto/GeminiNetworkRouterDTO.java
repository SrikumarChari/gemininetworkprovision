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

    public List<GeminiSubnetDTO> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<GeminiSubnetDTO> interfaces) {
        this.interfaces = interfaces;
    }
}
