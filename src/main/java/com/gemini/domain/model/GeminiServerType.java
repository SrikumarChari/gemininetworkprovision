/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author Srikumar
 *
 * This class encapsulates the type of server flavors (Openstack, rackspace) or
 * instance types (AWS).
 */
@Entity
public class GeminiServerType extends EntityMongoDB {
    private String name;
    private String cloudID;
    private Integer disk;
    @Embedded
    private List<GeminiLink> links = Collections.synchronizedList(new ArrayList());
    private Integer ram;
    private Integer vcpu;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public Integer getDisk() {
        return disk;
    }

    public void setDisk(Integer disk) {
        this.disk = disk;
    }

    public List<GeminiLink> getLinks() {
        return links;
    }

    public void setLinks(List<GeminiLink> links) {
        this.links = links;
    }
    
    public boolean addLink(GeminiLink newLink) {
        if (links.stream().filter(l -> l.equals(newLink)).count() == 0) {
            return links.add(newLink);
        } else {
            return false;
        }
    }

    public boolean deleteLink(GeminiLink link) {
        return links.removeIf(l -> l.equals(link));
    }
    
    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Integer getVcpu() {
        return vcpu;
    }

    public void setVcpu(Integer vcpu) {
        this.vcpu = vcpu;
    }
}
