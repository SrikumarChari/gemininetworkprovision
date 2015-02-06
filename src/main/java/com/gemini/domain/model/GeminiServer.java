/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.EntityMongoDB;
import com.gemini.domain.common.IPAddressType;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jersey.repackaged.com.google.common.net.InetAddresses;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author schari
 */
@Entity
public class GeminiServer extends EntityMongoDB {

    private String name;
    private String description;
    private Date dateCreated;
    private InetAddress address;
    private IPAddressType addressType;
    private String serverType;
    private String subnetMask;
    private Integer port;
    private String os;
    private String admin;
    private String password;
    private Map<String, String> metadata = Collections.synchronizedMap(new HashMap<String, String>());
    private GeminiServerImage image;
    private List<String> securityGroupNames = Collections.synchronizedList(new ArrayList());

    public GeminiServer() {
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getDateCreateString() {
        return dateCreated.toString();
    }
    
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getAddressString() {
        return InetAddresses.toAddrString(address);
    }

    public IPAddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(IPAddressType addressType) {
        this.addressType = addressType;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getSubnetMask() {
        return subnetMask;
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetaData(Map<String, String> metadata) {
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
    
    public GeminiServerImage getImage() {
        return image;
    }

    public void setImage(GeminiServerImage image) {
        this.image = image;
    }

    public List<String> getSecGroups() {
        return securityGroupNames;
    }

    public void setSecGroups(List<String> secGroups) {
        this.securityGroupNames = secGroups;
    }
    
    public boolean addSecGroup(String secGroupName) {
        if (securityGroupNames.stream().filter(s -> s.equals(secGroupName)).count() == 0) {
            return securityGroupNames.add(secGroupName);
        } else {
            return false;
        }
    }
    
    public boolean deleteSecGroup (GeminiSecurityGroup secGroup) {
        return securityGroupNames.removeIf(s -> s.equals(secGroup));
    }
}
