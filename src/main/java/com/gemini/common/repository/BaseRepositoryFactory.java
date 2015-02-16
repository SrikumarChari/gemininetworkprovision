/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository;

import com.google.inject.assistedinject.Assisted;

/**
 *
 * @author schari
 */
public interface BaseRepositoryFactory {
    public BaseRepository create(@Assisted("dbName") String dbName, @Assisted("classType")Class<?> tableType);
}
