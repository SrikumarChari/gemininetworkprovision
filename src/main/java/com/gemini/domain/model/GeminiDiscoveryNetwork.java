/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.impl.EntityMongoDB;
import java.net.InetAddress;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author schari
 */
@Entity
public class GeminiDiscoveryNetwork extends EntityMongoDB {
    //network range discovery type
    private InetAddress discNetStart;
    private InetAddress discNetEnd;
    
    //subnet discNetworkMask discovery type
    private InetAddress discNetwork;
    private Integer discNetworkMask;
    
    //single discHost discovery type
    private String discHost;
    
    //type of discNetwork
    private String networkType;

    //is the discNetwork discovery complete
    private boolean discovered;    

    public InetAddress getDiscNetStart() {
        return discNetStart;
    }

    public void setDiscNetStart(InetAddress discNetStart) {
        this.discNetStart = discNetStart;
    }

    public InetAddress getDiscNetEnd() {
        return discNetEnd;
    }

    public void setDiscNetEnd(InetAddress discNetEnd) {
        this.discNetEnd = discNetEnd;
    }

    public InetAddress getDiscNetwork() {
        return discNetwork;
    }

    public void setDiscNetwork(InetAddress discNetwork) {
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
