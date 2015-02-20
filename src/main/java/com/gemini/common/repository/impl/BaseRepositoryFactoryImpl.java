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
import com.google.inject.Provider;

/**
 *
 * @author schari
 */
public class BaseRepositoryFactoryImpl implements BaseRepositoryFactory {

    private final GeminiMongoClientProvider mongoClientProvider;
    private final GeminiMorphiaProvider morphiaProvider;
    private final Provider<GeminiProperties> propertiesProvider;

    @Inject
    public BaseRepositoryFactoryImpl(GeminiMongoClientProvider mongoClientProvider,
            GeminiMorphiaProvider morphiaProvider, Provider<GeminiProperties> propertiesProvider) {
        this.mongoClientProvider = mongoClientProvider;
        this.morphiaProvider = morphiaProvider;
        this.propertiesProvider = propertiesProvider;
    }

    @Override
    public BaseRepository create(Class<?> tableType) {
        return new BaseRepositoryMongoDBImpl(mongoClientProvider.get(),
                morphiaProvider.get(),
                propertiesProvider.get(),
                tableType);
    }
}
