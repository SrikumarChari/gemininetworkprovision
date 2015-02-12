/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.repository.impl;

import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.repository.GeminiApplicationRepository;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.MongoClient;
import java.util.List;
import org.mongodb.morphia.Morphia;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiApplicationRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<GeminiApplication, String>
        implements GeminiApplicationRepository {

    @Inject
    public GeminiApplicationRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, @Assisted String dbName) {
        //create the database and collection
        super(GeminiApplication.class, mongoClient, morphia, dbName);
    }

    @Override
    public List<GeminiNetwork> getNetworks(String appName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
