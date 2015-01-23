/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class GeminiNetworkRouter extends EntityMongoDB {
    private String name;
    private String status;
    private String cloudID;
    
    @Reference
    private GeminiNetwork gateway;
    private Map<String, String> routes = Collections.synchronizedMap(new HashMap<String, String>());

    @Reference
    private List<GeminiSubnet> interfaces = Collections.synchronizedList(new ArrayList());

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

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public GeminiNetwork getGateway() {
        return gateway;
    }

    public void setGateway(GeminiNetwork gateway) {
        this.gateway = gateway;
    }

    public Map<String, String> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, String> routes) {
        this.routes = routes;
    }

    public void addRoute(String nextHop, String dest) {
        routes.putIfAbsent(nextHop, dest);
    }
    
    public void deleteRoute(String nextHop, String dest) {
        routes.remove(nextHop, dest);
    }

    public List<GeminiSubnet> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<GeminiSubnet> interfaces) {
        this.interfaces = interfaces;
    }
    
    public void addInterface (GeminiSubnet subnet) {
        if (!interfaces.stream().anyMatch(s -> s.equals(subnet))) {
            interfaces.add(subnet);
        }
    }
    
    public void deleteInterface(GeminiSubnet subnet) {
        interfaces.removeIf(s -> s.equals(subnet));
    }
}
