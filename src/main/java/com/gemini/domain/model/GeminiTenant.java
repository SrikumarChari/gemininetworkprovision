/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.impl.EntityMongoDB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author schari
 */
@Entity
public class GeminiTenant extends EntityMongoDB {
    private String name;
    private String tenantID;
    private String domainName;
    private boolean initialized = false;
    
    @Embedded
    private List<GeminiTenantUser> users = Collections.synchronizedList(new ArrayList());
    
    @Embedded
    private List<GeminiEnvironment> environments = Collections.synchronizedList(new ArrayList());
    
    @Embedded
    List<GeminiNetworkRouter> routers = Collections.synchronizedList(new ArrayList());

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

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<GeminiTenantUser> getUsers() {
        return users;
    }

    public void setUsers(List<GeminiTenantUser> users) {
        this.users = users;
    }

    public boolean addUser (GeminiTenantUser user) {
        if (users.stream().filter(u -> u.getName().equals(user.getName())).count() == 0) {
            return users.add(user);
        } else {
            return false;
        }
    }
    
    public boolean deleteUser(GeminiTenantUser user) {
        return users.removeIf(u -> u.getName().equals(user.getName()));
    }
    
    public List<GeminiEnvironment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<GeminiEnvironment> environments) {
        this.environments = environments;
    }
    
    public boolean addEnvironment (GeminiEnvironment env) {
        if (environments.stream().filter(e -> e.getName().equals(env.getName())).count() == 0) {
            return environments.add(env);
        } else {
            return false;
        }
    }
    
    public boolean deleteEnvironment(GeminiEnvironment env) {
        return environments.removeIf(e -> e.getName().equals(env.getName()));
    }

    public List<GeminiNetworkRouter> getRouters() {
        return routers;
    }

    public void setRouters(List<GeminiNetworkRouter> routers) {
        this.routers = routers;
    }

    public boolean addRouter(GeminiNetworkRouter nRouter) {
        if (routers.stream().filter(r -> r.getName().equals(nRouter.getName())).count() == 0) {
            return routers.add(nRouter);            
        }
        return false;
    }
    
    public boolean deleteRouter(GeminiNetworkRouter nRouter) {
        return routers.removeIf(r -> r.equals(nRouter));
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }    
}
