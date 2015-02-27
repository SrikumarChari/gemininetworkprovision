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
public class GeminiNetworkDTO {
    //general 
    private String name = "";
    private String description = "";
    private String networkType = "";
    private String cloudID = "";

    //is the provisioning complete
    private boolean provisioned;
    
    //the subnets
    List<GeminiSubnetDTO> subnets = Collections.synchronizedList(new ArrayList());

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

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

    public List<GeminiSubnetDTO> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<GeminiSubnetDTO> subnets) {
        this.subnets = subnets;
    }
    
    public boolean addSubnet (GeminiSubnetDTO subnet) {
        if (subnets.stream().filter(s -> s.equals(subnet)).count() == 0) {
            return subnets.add(subnet);
        } else {
            return false;
        }
    }
    
    public boolean deleteSubnet (GeminiSubnetDTO subnet) {
        return subnets.removeIf(s -> s.equals(subnet));
    }
}
