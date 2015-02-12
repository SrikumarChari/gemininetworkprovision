/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository;

import java.util.List;

/**
 *
 * @author schari
 * @param <T> - the type of object represented by this DAO
 * @param <PK> - the primary key 
 */
public interface BaseRepository<T extends Entity, PK> {

    /**
     * Persist the newInstance object into database
     *
     * @param newInstance
     */
    public void add(T newInstance);

    /**
     * Retrieve an object that was previously persisted to the database using
     * the indicated id as primary key
     *
     * @param id
     * @return
     */
    public T get(PK id);

    /**
     * Save changes made to a persistent object.
     *
     * @param id
     * @param transientObject
     */
    public void update(PK id, T transientObject);

    /**
     * Remove an object from persistent storage in the database
     *
     * @param id
     */
    public void remove(PK id);

    /**
     *
     * @return List of all objects (represented by this database)
     */
    public List<T> list();
}
