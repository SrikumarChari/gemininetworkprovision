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
public class GeminiSubnetDTO extends GeminiBaseDTO {

    //parent network that contains this subnet
    private GeminiNetworkDTO parent;

    //address pool
    private List<GeminiSubnetAllocationPoolDTO> allocationPool;
    private String subnetEnd;

    //network string with mask
    private String cidr;

    //network gateway
    private String gateway;

    public GeminiNetworkDTO getParent() {
        return parent;
    }

    public void setParent(GeminiNetworkDTO parent) {
        this.parent = parent;
    }

    public List<GeminiSubnetAllocationPoolDTO> getAllocationPool() {
        return allocationPool;
    }

    public void setAllocationPool(List<GeminiSubnetAllocationPoolDTO> allocationPool) {
        this.allocationPool = allocationPool;
    }

    public String getSubnetEnd() {
        return subnetEnd;
    }

    public void setSubnetEnd(String subnetEnd) {
        this.subnetEnd = subnetEnd;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
}
