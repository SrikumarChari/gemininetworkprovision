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
public class GeminiSecurityGroup extends EntityMongoDB {
    private String name = "";
    private String description = "";
    private String cloudID = "";
    @Embedded
    private List<GeminiSecurityGroupRule> securityRules = Collections.synchronizedList(new ArrayList());
    private boolean provisioned = false;

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

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

    public List<GeminiSecurityGroupRule> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(List<GeminiSecurityGroupRule> securityRules) {
        this.securityRules = securityRules;
    }
    
    public boolean addSecurityRule (GeminiSecurityGroupRule newRule) {
        if (securityRules.stream().filter(sr -> sr.equals(newRule)).count() == 0) {
            securityRules.add(newRule);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean deleteSecurityRule(GeminiSecurityGroupRule rule) {
        return securityRules.removeIf(r -> r.equals(rule));
    }

    @Override
    public String toString() {
        return "GeminiSecurityGroup{" + "name=" + name + ", description=" + description + ", cloudID=" + cloudID + ", securityRules=" + securityRules + '}';
    }
}
