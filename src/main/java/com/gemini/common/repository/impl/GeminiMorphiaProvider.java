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
import org.mongodb.morphia.Morphia;

/**
 *
 * @author schari
 */
public class GeminiMorphiaProvider implements Provider<Morphia> {

    @Override
    public Morphia get() {
        Injector propInjector = Guice.createInjector(new GeminiPropertiesModule());
        GeminiProperties properties = propInjector.getInstance(GeminiProperties.class);        Morphia morphia = new Morphia();
        morphia.mapPackage(properties.getProperties().getProperty("MODEL_PACKAGE"), true); //map all the model object
        return morphia;
    }
}
