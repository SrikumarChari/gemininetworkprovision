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
    private String adminUserName;
    private String adminPassword;
    private String endPoint;
    private String domainName;

    private List<GeminiTenantUserDTO> users = Collections.synchronizedList(new ArrayList());
    private List<GeminiEnvironmentDTO> environments = Collections.synchronizedList(new ArrayList());
//    List<GeminiNetworkRouterDTO> routers = Collections.synchronizedList(new ArrayList());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean addUser(GeminiTenantUserDTO user) {
        //this function provided only for a java client - it is not used by the mapper
        if (users.stream().filter(u -> u.getName().equals(user.getName())).count() == 0) {
            return users.add(user);
        } else {
            return false;
        }        
    }
    
    public boolean deleteUser(GeminiTenantUserDTO user) {
        //this function provided only for a java client - it is not used by the mapper
        return users.removeIf(u -> u.getName().equals(user.getName()));
    }
    
    public List<GeminiEnvironmentDTO> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<GeminiEnvironmentDTO> environments) {
        this.environments = environments;
    }

    public boolean addEnvironment(GeminiEnvironmentDTO env) {
        //this function provided only for a java client - it is not used by the mapper
        if (environments.stream().filter(e -> e.getName().equals(env.getName())).count() == 0) {
            return environments.add(env);
        } else {
            return false;
        }        
    }
    
    public boolean deleteEnvironment(GeminiEnvironmentDTO env) {
        //this function provided only for a java client - it is not used by the mapper
        return environments.removeIf(e -> e.getName().equals(env.getName()));
    }
    
//    public List<GeminiNetworkRouterDTO> getRouters() {
//        return routers;
//    }
//
//    public void setRouters(List<GeminiNetworkRouterDTO> routers) {
//        this.routers = routers;
//    }
//
//    public boolean addRouter(GeminiNetworkRouterDTO router) {
//        //this function provided only for a java client - it is not used by the mapper
//        if (routers.stream().filter(r -> r.getName().equals(router.getName())).count() == 0) {
//            return routers.add(router);
//        } else {
//            return false;
//        }        
//    }
//    
//    public boolean deleteRouter(GeminiNetworkRouterDTO router) {
//        //this function provided only for a java client - it is not used by the mapper
//        return routers.removeIf(r -> r.getName().equals(router.getName()));
//    }
}
