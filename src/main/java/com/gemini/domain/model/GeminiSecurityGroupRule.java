/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import com.gemini.domain.common.GeminiSecurityGroupRuleDirection;
import com.gemini.domain.common.IPAddressType;

/**
 *
 * @author schari
 */
public class GeminiSecurityGroupRule extends EntityMongoDB {
    private String name;
    private GeminiSecurityGroup parent;
    private GeminiSecurityGroupRuleDirection direction;
    private String cloudID;
    private String cidr;
    private IPAddressType ipAddressType;
    private Integer protocol;
    private Integer portRangeMin;
    private Integer portRangeMax;
    private String remoteGroupId;
    private String remoteIpPrefix;

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

    public Integer getProtocol() {
        return protocol;
    }

    public void setProtocol(Integer protocol) {
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
}
