package com.gemini.domain.model;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.ProvisionState;

import java.net.InetAddress;

/**
 * @author t.varada.
 */
public class GeminiLoadBalancer {

    private String cloudID;//uuid returned by cloud provider
    private String name;
    private String description;
    private InetAddress virtualPvtIP;
    private GeminiSubnet virtualPvtSubnet;
    private AdminState adminState;
    private ProvisionState provisionState;

    public ProvisionState getProvisionState() {
        return provisionState;
    }

    public void setProvisionState(ProvisionState provisionState) {
        this.provisionState = provisionState;
    }

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InetAddress getVirtualPvtIP() {
        return virtualPvtIP;
    }

    public void setVirtualPvtIP(InetAddress virtualPvtIP) {
        this.virtualPvtIP = virtualPvtIP;
    }

    public GeminiSubnet getVirtualPvtSubnet() {
        return virtualPvtSubnet;
    }

    public void setVirtualPvtSubnet(GeminiSubnet virtualPvtSubnet) {
        this.virtualPvtSubnet = virtualPvtSubnet;
    }

    public AdminState getAdminState() {
        return adminState;
    }

    public void setAdminState(AdminState adminState) {
        this.adminState = adminState;
    }


}
