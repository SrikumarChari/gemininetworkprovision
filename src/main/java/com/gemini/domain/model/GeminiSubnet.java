/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.impl.EntityMongoDB;
import com.gemini.domain.common.IPAddressType;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class GeminiSubnet extends EntityMongoDB {

    private String name = "";
    //network string with mask
    private String cidr = "";
    //network gateway
    private InetAddress gateway;

    private IPAddressType networkType;
    private boolean enableDHCP;
    
    //parent network that contains this subnet
    @Reference
    private GeminiNetwork parent;

    //address pool
    @Embedded
    private List<GeminiSubnetAllocationPool> allocationPools = Collections.synchronizedList(new ArrayList());

    private String cloudID = "";
    boolean provisioned = false;

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

    public boolean isEnableDHCP() {
        return enableDHCP;
    }

    public void setEnableDHCP(boolean enableDHCP) {
        this.enableDHCP = enableDHCP;
    }

    public IPAddressType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(IPAddressType networkType) {
        this.networkType = networkType;
    }

    public void addAllocationPool(InetAddress start, InetAddress end) {
        allocationPools.add(new GeminiSubnetAllocationPool(start, end));
    }

    public boolean addAllocationPool(GeminiSubnetAllocationPool pool) {
        if (allocationPools.stream().filter(p -> p.getStart().getHostAddress().equals(pool.getStart().getHostAddress())
                && p.getEnd().getHostAddress().equals(pool.getEnd().getHostAddress())).count() == 0) {
            return allocationPools.add(pool);
        } else {
            return false;
        }
    }

    public void deleteAllocationPool(InetAddress start, InetAddress end) {
        allocationPools.removeIf(s -> s.getStart().getHostAddress().equals(start.getHostAddress())
                && s.getEnd().getHostAddress().equals(end.getHostAddress()));
    }

    public List<GeminiSubnetAllocationPool> getAllocationPools() {
        return allocationPools;
    }

    public void setAllocationPools(List<GeminiSubnetAllocationPool> allocationPool) {
        this.allocationPools = allocationPool;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
