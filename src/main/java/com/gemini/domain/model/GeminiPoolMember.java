package com.gemini.domain.model;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.ProvisionState;


/**
 * @author t.varada.
 */
public class GeminiPoolMember {
    private String cloudID;//uuid returned by cloud provider
    private String ipAddress;
    private GeminiSubnet subnet;
    private int weight;
    private AdminState adminState;
    private ProvisionState provisionState;
    private int protocolPort;
    private String poolId;

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
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

}
