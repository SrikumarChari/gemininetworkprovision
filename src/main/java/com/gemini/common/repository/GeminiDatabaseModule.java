/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository;

import com.gemini.common.repository.impl.BaseRepositoryFactoryImpl;
import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 * @author schari
 */
public class GeminiDatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(BaseRepository.class, BaseRepositoryMongoDBImpl.class)
                .build(BaseRepositoryFactoryImpl.class));
    }

}
