/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import java.util.ArrayList;
import java.util.Collections;
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
    private String description;
    private String networkType;

    //is the provisioning complete
    private boolean provisioned = false;
    
    @Reference
    List<GeminiSubnet> subnets = Collections.synchronizedList(new ArrayList());

    //will be used to set the ID returned for this network from the cloud provider
    private String cloudID;

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

    public String getCloudID () {
        return cloudID;
    }
    
    public void setCloudID(String id) {
        this.cloudID = id;
    }

    public List<GeminiSubnet> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<GeminiSubnet> subnets) {
        this.subnets = subnets;
    }
    
    public boolean addSubnet(GeminiSubnet subnet) {
        if (subnets.stream().filter(s -> s.equals(subnet)).count() == 0) {
            return subnets.add(subnet);
        } else {
            return false;
        }
    }
    
    public boolean deleteSubnet (GeminiSubnet subnet) {
        return subnets.removeIf(s -> s.equals(subnet));
    }
}
