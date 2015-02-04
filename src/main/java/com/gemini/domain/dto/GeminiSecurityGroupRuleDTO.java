/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

/**
 *
 * @author Srikumar
 */
public class GeminiSecurityGroupRuleDTO {
    private GeminiSecurityGroupDTO parent;
    private String direction;
    private String ipAddressType;
    private Integer protocol;
    private Integer portRangeMin;
    private Integer portRangeMax;
    private String remoteGroupId;
    private String remoteIpPrefix;

    public GeminiSecurityGroupDTO getParent() {
        return parent;
    }

    public void setParent(GeminiSecurityGroupDTO parent) {
        this.parent = parent;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getIpAddressType() {
        return ipAddressType;
    }

    public void setIpAddressType(String ipAddressType) {
        this.ipAddressType = ipAddressType;
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
