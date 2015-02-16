/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository;

import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.gemini.common.repository.impl.GeminiMongoClientProvider;
import com.gemini.common.repository.impl.GeminiMorphiaProvider;
import com.gemini.properties.GeminiProperties;
import com.gemini.properties.GeminiPropertiesModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author schari
 */
public class GeminiDatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        Injector propInjector = Guice.createInjector(new GeminiPropertiesModule());
        GeminiProperties properties = propInjector.getInstance(GeminiProperties.class);

        //first the providers
        bind(MongoClient.class).toProvider(GeminiMongoClientProvider.class);
        bind(Morphia.class).toProvider(GeminiMorphiaProvider.class);

        //Guice doesn't support String injections, so....
//        bind(String.class)
//                .annotatedWith(Names.named("dbName"))
//                .toInstance(properties.getProperties().getProperty("DATABASE_NAME"));
//
        install(new FactoryModuleBuilder()
                .implement(BaseRepository.class, BaseRepositoryMongoDBImpl.class)
                .build(BaseRepositoryFactory.class));
    }
}
