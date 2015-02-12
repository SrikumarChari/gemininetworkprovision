/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.impl.EntityMongoDB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public boolean addRoute(String nextHop, String dest) {
        String v = routes.get(nextHop);
        if (v == null || !v.equals(dest)) {
            routes.put(nextHop, dest);
            return true;
        }
        return false;
    }
    
    public boolean deleteRoute(String nextHop, String dest) {
        return routes.remove(nextHop, dest);
    }

    public List<GeminiSubnet> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<GeminiSubnet> interfaces) {
        this.interfaces = interfaces;
    }
    
    public boolean addInterface (GeminiSubnet subnet) {
        if (interfaces.stream().filter(s -> s.equals(subnet)).count() == 0) {
            return interfaces.add(subnet);
        } else {
            return false;
        }
    }
    
    public boolean deleteInterface(GeminiSubnet subnet) {
        return interfaces.removeIf(s -> s.equals(subnet));
    }
}
