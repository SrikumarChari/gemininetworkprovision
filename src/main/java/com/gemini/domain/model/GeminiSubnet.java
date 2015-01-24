/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import java.net.InetAddress;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class GeminiSubnet extends GeminiNetwork {

    //parent network that contains this subnet
    @Reference
    private GeminiNetwork parent;

    //address pool
    @Embedded
    private List<GeminiSubnetAllocationPool> allocationPool;

    //network string with mask
    private String cidr;

    //network gateway
    private GeminiNetwork gateway;

    public GeminiNetwork getParent() {
        return parent;
    }

    public void setParent(GeminiNetwork parent) {
        this.parent = parent;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public void setGateway(GeminiNetwork gateway) {
        this.gateway = gateway;
    }

    public GeminiNetwork getGateway() {
        return gateway;
    }

    public void addAllocationPool(InetAddress start, InetAddress end) {
        allocationPool.add(new GeminiSubnetAllocationPool(start, end));
    }

    public boolean addAllocationPool(GeminiSubnetAllocationPool pool) {
        if (allocationPool.stream().filter(p -> p.getStart().getHostAddress().equals(pool.getStart().getHostAddress())
                && p.getEnd().getHostAddress().equals(pool.getEnd().getHostAddress())).count() == 0) {
            return allocationPool.add(pool);
        } else {
            return false;
        }
    }

    public void deleteAllocationPool(InetAddress start, InetAddress end) {
        allocationPool.removeIf(s -> s.getStart().getHostAddress().equals(start.getHostAddress())
                && s.getEnd().getHostAddress().equals(end.getHostAddress()));
    }

    public List<GeminiSubnetAllocationPool> getAllocationPools() {
        return allocationPool;
    }

    public void setAllocationPools(List<GeminiSubnetAllocationPool> allocationPool) {
        this.allocationPool = allocationPool;
    }
}
