/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author schari
 */
public class GeminiSubnetDTO extends GeminiBaseDTO {
    private String name; 
    //network string with mask
    private String cidr;
    //network gateway
    private String gateway;
    
    //parent network that contains this subnet
    private GeminiNetworkDTO parent;

    //address pool
    private List<GeminiSubnetAllocationPoolDTO> allocationPool = Collections.synchronizedList(new ArrayList());

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeminiSubnetDTO other = (GeminiSubnetDTO) obj;
        if (!Objects.equals(getParent(), other.getParent())) {
            return false;
        }
        if (!Objects.equals(this.getAllocationPool(), other.getAllocationPool())) {
            return false;
        }
        if (!Objects.equals(this.cidr, other.cidr)) {
            return false;
        }
        if (!Objects.equals(this.gateway, other.gateway)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.parent);
        hash = 59 * hash + Objects.hashCode(this.cidr);
        hash = 59 * hash + Objects.hashCode(this.gateway);
        return hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
