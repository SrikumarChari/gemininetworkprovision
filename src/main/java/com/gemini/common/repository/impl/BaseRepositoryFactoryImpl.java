/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository.impl;

import com.gemini.common.repository.BaseRepository;
import com.gemini.common.repository.BaseRepositoryFactory;
import com.gemini.properties.GeminiProperties;
import com.google.inject.Inject;

/**
 *
 * @author schari
 */
public class BaseRepositoryFactoryImpl implements BaseRepositoryFactory {

    private final GeminiMongoClientProvider mongoClientProvider;
    private final GeminiMorphiaProvider morphiaProvider;

    @Inject
    public BaseRepositoryFactoryImpl(GeminiMongoClientProvider mongoClientProvider,
            GeminiMorphiaProvider morphiaProvider) {
        this.mongoClientProvider = mongoClientProvider;
        this.morphiaProvider = morphiaProvider;
    }

    @Override
    public BaseRepository create(Class<?> tableClass) {
        GeminiProperties properties = new GeminiProperties();
        return new BaseRepositoryMongoDBImpl(mongoClientProvider.get(),
                morphiaProvider.get(),
                properties.getProperties().getProperty("DATABASE_HOST"),
                tableClass);
    }
}
