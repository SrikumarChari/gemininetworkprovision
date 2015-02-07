/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.base;

/**
 *
 * @author schari
 */
public enum ProvisioningProviderResponseType {

    /**
     * Failure to authenticate with the cloud
     */
    CLOUD_AUTH_FAILURE, 

    /**
     * Attempting to create a cloud object that already exists
     */
    OBJECT_EXISTS, 

    /**
     * Attempting to modify or delete an object that doesn't exist
     */
    OBJECT_NOT_FOUND, 
    
    /**
     * Cloud operation (CRUD) failed
     */
    CLOUD_FAILURE, 

    /**
     * Cloud operation was successful
     */
    SUCCESS, 

    /**
     * Exception at Cloud - most likely trying to access a empty list, etc.
     */
    CLOUD_EXCEPTION,
    
    /**
     * Most likely the Cloud object represented by the Gemini object are mismatched
     */
    CLOUD_MISMATCH
}
