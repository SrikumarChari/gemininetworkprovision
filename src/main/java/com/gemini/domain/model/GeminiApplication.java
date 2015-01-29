/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class GeminiApplication extends EntityMongoDB {

    private String name;
    private String description;
    private String custom; //string for any custom description, URL's etc.
    private Integer backupSize;
    private String location; //TODO: convert to a geo coordinate 

    @Reference
    private List<GeminiNetwork> networks;

//    @Reference
//    private final List<GeminiServer> servers;

    public GeminiApplication() {
        networks = new ArrayList();
        //servers = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public Integer getBackupSize() {
        return backupSize;
    }

    public void setBackupSize(Integer backupSize) {
        this.backupSize = backupSize;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

//    public boolean addServer(GeminiServer srv) {
//        if(servers.stream().filter(s -> s.getName().equals(srv.getName())).count() == 0)
//            return servers.add(srv);
//        else
//            return false;
//    }
//
//    public boolean deleteServer(GeminiServer srv) {
//        return servers.removeIf(s -> s.getName().equals(srv.getName()));
//    }
//
    public boolean addNetwork(GeminiNetwork net) {
        if (networks.stream().filter(n -> n.getName().equals(net.getName())).count() == 0)
            return networks.add(net);
        else
            return false;
    }

    public boolean deleteNetwork(GeminiNetwork net) {
        return networks.removeIf(n -> n.getName().equals(net.getName()));
    }

    public List<GeminiNetwork> getNetworks() {
        return networks;
    }

    public void setNetworks(List<GeminiNetwork> networks) {
        this.networks = networks;
    }
//    public List<GeminiServer> getServers() {
//        return servers;
//    }
//
}
