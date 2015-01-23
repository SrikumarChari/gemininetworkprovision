/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author schari
 */
public class GeminiTenantDTO extends GeminiBaseDTO {

    private String name;
    private String tenantID;    
    private String adminUserName;
    private String adminPassword;
    private String endPoint;
    private String domainName;

    private List<GeminiTenantUserDTO> users = Collections.synchronizedList(new ArrayList());
    private List<GeminiEnvironmentDTO> environments = Collections.synchronizedList(new ArrayList());
    List<GeminiNetworkRouterDTO> routers = Collections.synchronizedList(new ArrayList());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<GeminiTenantUserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<GeminiTenantUserDTO> users) {
        this.users = users;
    }

    public List<GeminiEnvironmentDTO> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<GeminiEnvironmentDTO> environments) {
        this.environments = environments;
    }

    public List<GeminiNetworkRouterDTO> getRouters() {
        return routers;
    }

    public void setRouters(List<GeminiNetworkRouterDTO> routers) {
        this.routers = routers;
    }
}
