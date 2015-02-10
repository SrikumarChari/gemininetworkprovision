/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author Srikumar
 */
@Entity
public class GeminiServerImage extends EntityMongoDB {
    private String name;
    private String cloudID;
    @Embedded
    private List<GeminiLink> links = Collections.synchronizedList(new ArrayList());
    private Map<String, String> metadata = Collections.synchronizedMap(new HashMap<String, String>());
    private Integer minDisk;
    private Integer minRam;
    private Long size;
    private Date updated;
    private String status;
    private Integer progress;

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

    public List<GeminiLink> getLinks() {
        return links;
    }

    public void setLinks(List<GeminiLink> links) {
        this.links = links;
    }

    public boolean addLink(GeminiLink link) {
        if (links.stream().filter(l -> l.equals(link)).count() == 0) {
            return links.add(link);
        } else
            return false;
    }
    
    public boolean removeLink(GeminiLink link) {
        return links.removeIf(l -> l.equals(link));
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, String value) {
        metadata.putIfAbsent(key, value);
    }

    public void updateMetadata(String key, String value) {
        metadata.replace(key, value);
    }
    
    public void deleteMetadata(String key, String value) {
        metadata.remove(key, value);
    }
    
    public Integer getMinDisk() {
        return minDisk;
    }

    public void setMinDisk(Integer minDisk) {
        this.minDisk = minDisk;
    }

    public Integer getMinRam() {
        return minRam;
    }

    public void setMinRam(Integer minRam) {
        this.minRam = minRam;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }    
}
