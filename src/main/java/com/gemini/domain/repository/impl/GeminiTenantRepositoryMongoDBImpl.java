/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.repository.impl;

import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiTenant;
import com.gemini.domain.model.GeminiTenantUser;
import com.gemini.domain.repository.GeminiTenantRepository;
import com.mongodb.MongoClient;
import java.util.List;
import org.mongodb.morphia.Morphia;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiTenantRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<GeminiTenant, String>
        implements GeminiTenantRepository {

    public GeminiTenantRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(mongoClient, morphia, GeminiTenant.class, dbName);
    }

    public GeminiTenant getTenantByName(String name) {
        Logger.debug("get tenant by name :{}", name);
        return findOne(getDatastore().createQuery(GeminiTenant.class).filter("name", name));
    }

    public List<GeminiEnvironment> getTenantEnvironments(String name) {
        Logger.debug("get tenant (by name) environments: {}", name);
        return getTenantByName(name).getEnvironments();        
    }

    public List<GeminiTenantUser> getTenantUsers (String name) {
        Logger.debug("get tenant (by name) environments: {}", name);
        return getTenantByName(name).getUsers();        
    }
}