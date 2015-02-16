/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.properties;

import com.google.inject.Inject;
import java.util.Properties;

/**
 *
 * @author schari
 */
public class GeminiProperties {
    @Inject
    private Properties properties;
    
    public Properties getProperties () {
        return properties;
    }
}
