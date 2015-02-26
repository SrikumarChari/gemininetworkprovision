/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto.serialize;

import com.gemini.domain.model.GeminiApplication;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author schari
 */
public class GeminiApplicationSerializer implements JsonSerializer<GeminiApplication> {

    @Override
    public JsonElement serialize(GeminiApplication src, Type typeOfSrc, JsonSerializationContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
