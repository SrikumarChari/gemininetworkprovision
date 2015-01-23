/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class GeminiNetwork extends EntityMongoDB {
    //general 
    private String name;
    private String networkType;

    //is the provisioning complete
    private boolean provisioned;
    
    //the provisioned IP address
    private InetAddress provisionedAddress;

    //the servers on this network
    @Reference
    List<GeminiServer> servers;
    
    //will be used to set the ID returned for this network from the cloud provider
    private String cloudID;

    public GeminiNetwork() {
        this.name = "";
        provisioned = false;
        servers = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

    public InetAddress getProvisionedAddress() {
        return provisionedAddress;
    }

    public void setProvisionedAddress(InetAddress provisionedAddress) {
        this.provisionedAddress = provisionedAddress;
    }

    public boolean addServer(GeminiServer srv) {
        if(servers.stream().filter(s -> s.getName().equals(srv.getName())).count() == 0)
            return servers.add(srv);
        else
            return false;
    }

    public boolean deleteServer(GeminiServer srv) {
        return servers.removeIf(s -> s.getName().equals(srv.getName()));
    }

    public List<GeminiServer> getServers() {
        return servers;
    }

    public String getCloudID () {
        return cloudID;
    }
    
    public void setCloudID(String id) {
        this.cloudID = id;
    }
}
