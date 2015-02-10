/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import com.gemini.domain.common.GeminiSecurityGroupRuleDirection;
import com.gemini.domain.common.IPAddressType;
import com.gemini.domain.common.Protocol;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Embedded
public class GeminiSecurityGroupRule extends EntityMongoDB {
    private String name = "";
    @Reference
    private GeminiSecurityGroup parent;
    private GeminiSecurityGroupRuleDirection direction;
    private String cloudID = "";
    private String cidr = "";
    private IPAddressType ipAddressType;
    private Protocol protocol;
    private Integer portRangeMin = 0;
    private Integer portRangeMax = 0;
    private String remoteGroupId = "";
    private String remoteIpPrefix = "";
    private boolean provisioned = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IPAddressType getIpAddressType() {
        return ipAddressType;
    }

    public void setIpAddressType(IPAddressType ipAddressType) {
        this.ipAddressType = ipAddressType;
    }

    
    public GeminiSecurityGroup getParent() {
        return parent;
    }

    public void setParent(GeminiSecurityGroup parent) {
        this.parent = parent;
    }

    public GeminiSecurityGroupRuleDirection getDirection() {
        return direction;
    }

    public void setDirection(GeminiSecurityGroupRuleDirection direction) {
        this.direction = direction;
    }

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Integer getPortRangeMin() {
        return portRangeMin;
    }

    public void setPortRangeMin(Integer portRangeMin) {
        this.portRangeMin = portRangeMin;
    }

    public Integer getPortRangeMax() {
        return portRangeMax;
    }

    public void setPortRangeMax(Integer portRangeMax) {
        this.portRangeMax = portRangeMax;
    }

    public String getRemoteGroupId() {
        return remoteGroupId;
    }

    public void setRemoteGroupId(String remoteGroupId) {
        this.remoteGroupId = remoteGroupId;
    }

    public String getRemoteIpPrefix() {
        return remoteIpPrefix;
    }

    public void setRemoteIpPrefix(String remoteIpPrefix) {
        this.remoteIpPrefix = remoteIpPrefix;
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

    @Override
    public String toString() {
        return "GeminiSecurityGroupRule{" + "name=" + name + ", direction=" + direction + ", cloudID=" + cloudID + ", cidr=" + cidr + ", ipAddressType=" + ipAddressType + ", protocol=" + protocol + ", portRangeMin=" + portRangeMin + ", portRangeMax=" + portRangeMax + ", remoteGroupId=" + remoteGroupId + ", remoteIpPrefix=" + remoteIpPrefix + '}';
    }    
}
