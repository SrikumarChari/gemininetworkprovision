/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository.impl;

import com.gemini.common.repository.BaseRepository;
import com.gemini.common.repository.BaseRepositoryFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author schari
 */
public class BaseRepositoryFactoryImpl implements BaseRepositoryFactory {
    private final Provider<MongoClient> mongoClientProvider;
    private final Provider<Morphia> morphiaProvider;

    @Inject
    public BaseRepositoryFactoryImpl(Provider<MongoClient> mongoClientProvider, Provider<Morphia> morphiaProvider) {
        this.mongoClientProvider = mongoClientProvider;
        this.morphiaProvider = morphiaProvider;
    }

    @Override
    public BaseRepository create(String dbName, Class<?> tableClass) {
        return new BaseRepositoryMongoDBImpl (mongoClientProvider.get(), morphiaProvider.get(), tableClass, dbName);
    }
}
