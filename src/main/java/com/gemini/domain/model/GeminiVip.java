package com.gemini.domain.model;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.Protocol;
import com.gemini.domain.common.ProvisionState;
import org.openstack4j.model.network.ext.SessionPersistence;

/**
 * Created by vthulasi on 2/16/2015.
 */
public class GeminiVip {
    private String cloudID;//uuid returned by cloud provider
    private String name;
    private String description;
    private AdminState adminState;
    private ProvisionState provisionState;
    private String tenantId;
    private int protocolPort;
    private GeminiSubnet geminiSubnet;
    private GeminiLoadBalancerPool geminiLoadBalancerPool;
    private String ipAddress;
    private Protocol protocol;
    private SessionPersistence sessionPersistence;
    private int connectionLimit;

    public int getConnectionLimit() {
        return connectionLimit;
    }

    public void setConnectionLimit(int connectionLimit) {
        this.connectionLimit = connectionLimit;
    }

    public SessionPersistence getSessionPersistence() {
        return sessionPersistence;
    }

    public void setSessionPersistence(SessionPersistence sessionPersistence) {
        this.sessionPersistence = sessionPersistence;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public int getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(int protocolPort) {
        this.protocolPort = protocolPort;
    }

    public GeminiSubnet getGeminiSubnet() {
        return geminiSubnet;
    }

    public void setGeminiSubnet(GeminiSubnet geminiSubnet) {
        this.geminiSubnet = geminiSubnet;
    }

    public GeminiLoadBalancerPool getGeminiLoadBalancerPool() {
        return geminiLoadBalancerPool;
    }

    public void setGeminiLoadBalancerPool(GeminiLoadBalancerPool geminiLoadBalancerPool) {
        this.geminiLoadBalancerPool = geminiLoadBalancerPool;
    }
}
