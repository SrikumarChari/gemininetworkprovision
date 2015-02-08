/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.security.openstack;

import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.security.base.SecurityProviderModule;
import com.gemini.provision.security.base.SecurityProvisioningService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openstack4j.api.OSClient;

/**
 *
 * @author Srikumar
 */
public class SecurityProviderOpenStackTest {
    static GeminiTenant tenant = new GeminiTenant();
    static GeminiEnvironment env = new GeminiEnvironment();
    static GeminiApplication newApp = new GeminiApplication();
    static GeminiNetwork newNet = new GeminiNetwork();
    static SecurityProvisioningService provisioningService;
    static OSClient os;

    
    public SecurityProviderOpenStackTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        //for now all the sample values are hard-coded
        //TODO: Convert it such that it reads from YAML
        //setup the tenant 
        tenant.setAdminUserName("sri");
        tenant.setName("Gemini-network-prj");
        tenant.setAdminPassword("srikumar12");
        tenant.setEndPoint("http://198.11.209.34:5000/v2.0");
        tenant.setDomainName("");
        tenant.setTenantID("6be32222eddb483e8d45d2c56d4bf2df");

        //setup the environment
        env.setName("Test Project");
        env.setType(GeminiEnvironmentType.OPENSTACK);
        tenant.addEnvironment(env);

        //setup the application
        newApp = new GeminiApplication();
        newApp.setName("JUnit test app");
        newApp.setLocation("In my closet");
        newApp.setBackupSize(Integer.SIZE);
        newApp.setCustom("Some custom string");
        newApp.setDescription("JUnit test app description");

        newNet = new GeminiNetwork();
        newNet.setName("JUnit test");
        newNet.setNetworkType("Class C");
        newNet.setProvisioned(false);

        newApp.addNetwork(newNet);

        //create the provisioning service
        Injector provisioningInjector = Guice.createInjector(
                new SecurityProviderModule(env.getType()));
        provisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
