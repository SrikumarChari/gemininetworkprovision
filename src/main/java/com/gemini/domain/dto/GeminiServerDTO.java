/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schari
 */
public class GeminiServerDTO extends GeminiBaseDTO {

    private String name;
    private String description;
    private String dateCreated;
    private String address;
    private String addressType;
    private String serverType;
    private String subnetMask;
    private Integer port;
    private String os;
    private String type; //TODO: Convert to an enum when the types are finalized
    private String admin;
    private String password;
    private Map<String, String> metadata = Collections.synchronizedMap(new HashMap<String, String>());
    private GeminiServerImageDTO image;
    private List<String> securityGroupNames = Collections.synchronizedList(new ArrayList());

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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public String getType() {
        return type;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
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

    public GeminiServerImageDTO getImage() {
        return image;
    }

    public void setImage(GeminiServerImageDTO image) {
        this.image = image;
    }

    public List<String> getSecGroupNames() {
        return securityGroupNames;
    }

    public void setSecGroupNames(List<String> secGroups) {
        this.securityGroupNames = secGroups;
    }
    
    public boolean addSecGroupName(String secGroupName) {
        if (securityGroupNames.stream().noneMatch(s -> s.equals(secGroupName))) {
            return securityGroupNames.add(secGroupName);
        } else {
            return false;
        }
    }
    
    public boolean deleteSecGroupName (String secGroup) {
        return securityGroupNames.removeIf(s -> s.equals(secGroup));
    }
}
