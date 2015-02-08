/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.mapper;

import com.gemini.domain.common.GeminiEnvironmentType;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class URLCustomConverter implements CustomConverter {

    @Override
    public Object convert(Object destValue, Object sourceValue,
            Class<?> destinationClass, Class<?> sourceClass) {
        if (sourceValue == null) {
            Logger.error("URL Custom Converter used incorrectly, NULL values passed");
            return null;
        }
        if (sourceValue instanceof String) {
            try {
                //convert String to URL
                URL url = new URL((String) sourceValue);
                return url;
            } catch (MalformedURLException ex) {
                Logger.info("URL Custom Converter used incorrectly, invalid URL supplied as source: {}", (String)sourceValue);
            }
        } else if (sourceValue instanceof URL) {
            return ((URL) sourceValue).toString();
        } else {
            Logger.error("URL Custom Converterr used incorrectly. Arguments passed in were: source\n {} \n destination\n {}",
                    ToStringBuilder.reflectionToString(sourceValue, ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(destValue, ToStringStyle.MULTI_LINE_STYLE));
            throw new MappingException("Converter TestCustomConverter used incorrectly. Arguments passed in were:"
                    + ToStringBuilder.reflectionToString(destValue, ToStringStyle.MULTI_LINE_STYLE)
                    + " and "
                    + ToStringBuilder.reflectionToString(sourceValue, ToStringStyle.MULTI_LINE_STYLE));
        }
        return null;
    }
}
