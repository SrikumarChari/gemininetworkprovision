/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.net.InetAddress;
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

    public boolean addAllocationPool(GeminiSubnetAllocationPoolDTO pool) {
        if (allocationPool.stream().filter(p -> p.getStart().equals(pool.getStart())
                && p.getEnd().equals(pool.getEnd())).count() == 0) {
            return allocationPool.add(pool);
        } else {
            return false;
        }
    }

    public void deleteAllocationPool(InetAddress start, InetAddress end) {
        allocationPool.removeIf(s -> s.getStart().equals(start.getHostAddress())
                && s.getEnd().equals(end.getHostAddress()));
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
