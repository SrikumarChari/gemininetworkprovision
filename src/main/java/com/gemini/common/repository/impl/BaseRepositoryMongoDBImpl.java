/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository.impl;

import com.gemini.common.repository.BaseRepository;
import com.gemini.common.repository.Entity;
import com.gemini.properties.GeminiProperties;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.MongoClient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import java.util.List;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 * @param <T>
 * @param <String>
 */
public class BaseRepositoryMongoDBImpl<T extends Entity, String>
        extends BasicDAO<T, String> implements BaseRepository<T, String> {

    private final Class<T> type;

    @Inject
    public BaseRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia,
            GeminiProperties properties, @Assisted Class<T> type) {
        super(type, mongoClient, morphia, properties.getProperties().getProperty("DATABASE_NAME"));
        this.type = type;

        //map the class to the database
        morphia.map(type);
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public List<T> list() {
        Logger.debug("list-find:{}", type.getSimpleName());
        return getDatastore().createQuery(type).asList();
    }

    @Override
    public void update(String id, T transientObject) {
        Logger.debug("update: id: {} object: ", id, transientObject);
        save(transientObject);
    }

    @Override
    public T get(String id) {
        Logger.debug("get-find id: {}", id);
        return findOne(getDatastore().createQuery(type).filter("_id", id));
    }

    @Override
    public void add(T newInstance) {
        Logger.debug("add: {}", ToStringBuilder.reflectionToString(newInstance, ToStringStyle.MULTI_LINE_STYLE));
        save(newInstance);
    }

    @Override
    public void remove(String id) {
        Logger.debug("remove: {}", id);
        this.deleteById(id);
    }

    public T getByName(String name) {
        Logger.debug("get-find by name: {}", name);
        return findOne(getDatastore().createQuery(type).filter("name", name));
    }
}
