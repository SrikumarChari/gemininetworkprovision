/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.impl.EntityMongoDB;
import org.mongodb.morphia.annotations.Embedded;

/**
 *
 * @author schari
 */
@Embedded
public class GeminiTenantUser extends EntityMongoDB {
    private String userID = "";
    private String name = "";
    private String password = "";
    private String preferences = ""; //will eventually become another with granular preference values

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
