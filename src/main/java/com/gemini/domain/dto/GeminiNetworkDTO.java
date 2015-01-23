/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.List;

/**
 *
 * @author schari
 */
public class GeminiNetworkDTO {
    //general 
    private String name;

    //is the provisioning complete
    private boolean provisioned;
    
    //the provisioned IP address
    private String provisionedAddress;
    
    //the servers on this network
    List<GeminiServerDTO> servers;    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

    public String getProvisionedAddress() {
        return provisionedAddress;
    }

    public void setProvisionedAddress(String provisionedAddress) {
        this.provisionedAddress = provisionedAddress;
    }

    public List<GeminiServerDTO> getServers() {
        return servers;
    }

    public void setServers(List<GeminiServerDTO> servers) {
        this.servers = servers;
    }
    
    public void addServer(GeminiServerDTO server) {
        if (servers.stream().filter(s -> s.getName().equals(server.getName())).count() == 0) {
            servers.add(server);
        }
    }
    
    public void deleteServer(GeminiServerDTO server) {
        servers.removeIf(s -> s.getName().equals(server.getName()));
    }
}
