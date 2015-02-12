/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.repository;

import com.gemini.common.repository.BaseRepository;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiNetwork;
import java.util.List;


/**
 * 
 * Methods are inherited, additional methods can be added if required. All streaming,
 * database or file implementation will inherit from this class
 * 
 * 
 * @author schari
 */
public interface GeminiApplicationRepository extends BaseRepository<GeminiApplication, String>  {
    //find an applicaiton by name
    public List<GeminiNetwork> getNetworks(String appName);
}
