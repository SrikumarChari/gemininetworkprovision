/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository.impl;

import com.gemini.properties.GeminiProperties;
import com.gemini.properties.GeminiPropertiesModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiMongoClientProvider implements Provider<MongoClient> {

    @Override
    public MongoClient get() {
        Injector propInjector = Guice.createInjector(new GeminiPropertiesModule());
        GeminiProperties properties = propInjector.getInstance(GeminiProperties.class);
        MongoClient mongoClient = null;

        try {
            mongoClient = new MongoClient(properties.getProperties().getProperty("DATABASE_HOST"));
        } catch (UnknownHostException ex) {
            Logger.error("Severe Error: Unknown database host", ex);
        }
        
        return mongoClient;
    }
}
