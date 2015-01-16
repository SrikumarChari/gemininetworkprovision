/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
@Entity
public class GeminiNetwork extends EntityMongoDB {
    //general 
    private String name;
    
    //DISCOVERY RELATED
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
    
    //PROVISIONING RELATED
    //is the provisioning complete
    private boolean provisioned;
    
    //the provisioned IP address
    private InetAddress provisionedAddress;
    
    //the servers on this network
    @Reference
    List<GeminiServer> servers;
    
    //will be used to set the ID returned for this network from the cloud provider
    private String cloudID;

    public GeminiNetwork() {
        this.networkType = "";
        this.name = "";
        discovered = false;
        provisioned = false;
        servers = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

    public InetAddress getProvisionedAddress() {
        return provisionedAddress;
    }

    public void setProvisionedAddress(InetAddress provisionedAddress) {
        this.provisionedAddress = provisionedAddress;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public void addServer(GeminiServer s) {
        if (servers.contains(s)) {
            Logger.info("Did not add server:{} already exists in network start: {} end: {}", s, getDiscNetStart(), getDiscNetEnd());
        } else {
            if (!servers.add(s)) {
                Logger.error("Failed to add server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
            } else {
                //add a connection between this discNetwork and the server
                Logger.debug("Successfully added server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
                //s.setNetwork(this);
            }
        }
    }

    public boolean deleteServer(GeminiServer s) {
        if (servers.contains(s)) {
            if (!servers.remove(s)) {
                Logger.error("Failed to delete server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
                return false;
            } else {
                //remove the connection between this discNetwork and the server
                //s.setNetwork(null);
                Logger.debug("Successfully deleted server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
                return true;
            }
        } else {
            Logger.info("Did not delete server, server does not exist in networkserver: {} network: {}",
                    ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
            return false;
        }
    }

    public List<GeminiServer> getServers() {
        Logger.debug("Network getServers: {}",
                ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
        return servers;
    }

    public String getCloudID () {
        return cloudID;
    }
    
    public void setCloudID(String id) {
        this.cloudID = id;
    }
}
