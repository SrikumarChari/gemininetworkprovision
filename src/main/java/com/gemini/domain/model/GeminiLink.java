/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.gemini.common.repository.impl.EntityMongoDB;
import java.net.URL;
import org.mongodb.morphia.annotations.Embedded;

/**
 *
 * @author Srikumar
 */
@Embedded
public class GeminiLink extends EntityMongoDB {
    private URL link;
    private String rel;
    private String type;

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
