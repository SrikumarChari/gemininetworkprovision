package com.gemini.domain.model;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.Protocol;
import com.gemini.domain.common.ProvisionState;

/**
 * @author t.varada.
 */
public class GeminiLoadBalancerListener {
    private String cloudID;//uuid returned by cloud provider
    private String name;
    private String description;
    private Protocol protocol;
    private int port;
    private GeminiLoadBalancer loadBalancer;
    private AdminState adminState;
    private ProvisionState provisionState;
    private int connectionLimit;
    private GeminiLoadBalancerPool defaultPool;

    public GeminiLoadBalancerPool getDefaultPool() {
        return defaultPool;
    }

    public void setDefaultPool(GeminiLoadBalancerPool defaultPool) {
        this.defaultPool = defaultPool;
    }

    public int getConnectionLimit() {
        return connectionLimit;
    }

    public void setConnectionLimit(int connectionLimit) {
        this.connectionLimit = connectionLimit;
    }

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

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public GeminiLoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(GeminiLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public AdminState getAdminState() {
        return adminState;
    }

    public void setAdminState(AdminState adminState) {
        this.adminState = adminState;
    }


}
