/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.base;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.tenant.GeminiTenant;
import java.util.List;

/**
 *
 * @author schari
 */
public interface BaseProvisionNetworkProvider {
    public String provisioningDesc(); //returns a description of the provisioning
    
    //the network api
    public Integer createNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n);
    public List<Integer> bulkCreateNetwork(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetwork> networks);
    public Integer deleteNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n);
    public Integer updateNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n);
    
    //the subnet API
    public Integer createSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, GeminiSubnet subnet);
    public List<Integer> bulkCreateSubnet (GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, List<GeminiSubnet> subnets);
    public Integer updateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet network);
    public Integer deleteSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet network);
}
