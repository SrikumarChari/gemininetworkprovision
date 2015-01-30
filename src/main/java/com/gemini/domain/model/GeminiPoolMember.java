package com.gemini.domain.model;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.ProvisionState;


/**
 * @author t.varada.
 */
public class GeminiPoolMember {
    private String cloudID;//uuid returned by cloud provider
    private GeminiNetwork ipAddress;
    private GeminiSubnet subnet;
    private int weight;//??
    private AdminState adminState;
    private ProvisionState provisionState;

    public int getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(int protocolPort) {
        this.protocolPort = protocolPort;
    }

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public GeminiNetwork getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(GeminiNetwork ipAddress) {
        this.ipAddress = ipAddress;
    }

    public GeminiSubnet getSubnet() {
        return subnet;
    }

    public void setSubnet(GeminiSubnet subnet) {
        this.subnet = subnet;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public AdminState getAdminState() {
        return adminState;
    }

    public void setAdminState(AdminState adminState) {
        this.adminState = adminState;
    }

    public ProvisionState getProvisionState() {
        return provisionState;
    }

    public void setProvisionState(ProvisionState provisionState) {
        this.provisionState = provisionState;
    }

    private int protocolPort;
}
