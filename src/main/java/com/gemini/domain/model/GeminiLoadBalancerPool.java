package com.gemini.domain.model;

import com.gemini.domain.common.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t.varada.
 */
public class GeminiLoadBalancerPool {
    private String cloudID;//uuid returned by cloud provider
    private String name;
    private String description;
    private Protocol protocol;
    private LoadBalancerAlgorithm loadBalancerAlgorithm;
    private AdminState adminState;
    private ProvisionState provisionState;
    private LoadBalancerSessionType sessionType;
    private List<GeminiPoolMember> poolMembers = new ArrayList<>();
    private GeminiLoadBalancerHealthMonitor loadBalancerHealthMonitor;
    private String vipID;//TODO change to domain object
    private GeminiSubnet geminiSubnet;

    public GeminiSubnet getGeminiSubnet() {
        return geminiSubnet;
    }

    public void setGeminiSubnet(GeminiSubnet geminiSubnet) {
        this.geminiSubnet = geminiSubnet;
    }

    public String getVipID() {
        return vipID;
    }
    public void setVipID(String vipID) {
        this.vipID = vipID;
    }

    public GeminiLoadBalancerHealthMonitor getLoadBalancerHealthMonitor() {
        return loadBalancerHealthMonitor;
    }

    public void setLoadBalancerHealthMonitor(GeminiLoadBalancerHealthMonitor loadBalancerHealthMonitor) {
        this.loadBalancerHealthMonitor = loadBalancerHealthMonitor;
    }

    public LoadBalancerSessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(LoadBalancerSessionType sessionType) {
        this.sessionType = sessionType;
    }

    public List<GeminiPoolMember> getPoolMembers() {
        return poolMembers;
    }

    public void setPoolMembers(List<GeminiPoolMember> poolMembers) {
        this.poolMembers = poolMembers;
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

    public LoadBalancerAlgorithm getLoadBalancerAlgorithm() {
        return loadBalancerAlgorithm;
    }

    public void setLoadBalancerAlgorithm(LoadBalancerAlgorithm loadBalancerAlgorithm) {
        this.loadBalancerAlgorithm = loadBalancerAlgorithm;
    }

    public AdminState getAdminState() {
        return adminState;
    }

    public void setAdminState(AdminState adminState) {
        this.adminState = adminState;
    }


}
