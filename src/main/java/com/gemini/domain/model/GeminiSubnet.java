/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author schari
 */
public class GeminiSubnet extends GeminiNetwork {

    //parent network that contains this subnet
    private GeminiNetwork parent;

    //address pool
    private List<GeminiSubnetAllocationPool> allocationPool;
    private InetAddress subnetEnd;

    //network string with mask
    private String cidr;

    //network gateway
    private InetAddress gateway;

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

    public void setGateway(InetAddress gateway) {
        this.gateway = gateway;
    }

    public InetAddress getGateway() {
        return gateway;
    }

    public void addAllocationPool(InetAddress start, InetAddress end) {
        allocationPool.add(new GeminiSubnetAllocationPool(start, end));
    }

    public void addAllocationPool(GeminiSubnetAllocationPool pool) {
        allocationPool.add(pool);
    }
    public void deleteAllocationPool(InetAddress start, InetAddress end) {
        allocationPool.removeIf(s -> s.getStart().getHostAddress().equals(start.getHostAddress())
                && s.getEnd().getHostAddress().equals(end.getHostAddress()));
    }

    public List<String> getAllocationPoolsAsString() {
        return allocationPool
                .stream()
                .map(p -> p.toString())
                .collect(Collectors.toList());
    }
    
    public List<GeminiSubnetAllocationPool> getAllocationPools() {
        return allocationPool;
    }
}
