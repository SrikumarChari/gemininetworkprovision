/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

/**
 *
 * @author schari
 */
public class GeminiDiscoveryNetworkDTO extends GeminiBaseDTO  {
    private String discNetStart;
    private String discNetEnd;
    private String discNetwork;
    private Integer discNetworkMask;
    private String discHost;
    private String networkType;
    private boolean discovered = false;

    public GeminiDiscoveryNetworkDTO () {
        this.networkType = "";
    }

    public String getDiscNetStart() {
        return discNetStart;
    }

    public void setDiscNetStart(String discNetStart) {
        this.discNetStart = discNetStart;
    }

    public String getDiscNetEnd() {
        return discNetEnd;
    }

    public void setDiscNetEnd(String discNetEnd) {
        this.discNetEnd = discNetEnd;
    }

    public String getDiscNetwork() {
        return discNetwork;
    }

    public void setDiscNetwork(String discNetwork) {
        this.discNetwork = discNetwork;
    }

    public Integer getDiscNetworkMask() {
        return discNetworkMask;
    }

    public void setDiscNetworkMask(Integer discNetworkMask) {
        this.discNetworkMask = discNetworkMask;
    }

    public String getDiscHost() {
        return discHost;
    }

    public void setDiscHost(String discHost) {
        this.discHost = discHost;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }
}
