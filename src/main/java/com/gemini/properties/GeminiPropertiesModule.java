/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.properties;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiPropertiesModule extends AbstractModule {

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream ("/Users/schari/NetBeansProjects/GeminiNetworkProvision/Properties/Gemini.properties"));
        } catch (IOException | NullPointerException ex) {
            Logger.error("Could not read properties file. Exception: ", ex);
        }
                
        bind(Properties.class).toInstance(properties);
        bind(GeminiProperties.class).in(Singleton.class);
    }
}
