/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.openstack;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.tenant.GeminiTenant;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author schari
 */
public class GeminiOpenStackJUnitTest {
    
    static GeminiTenant tenant = new GeminiTenant();
    static GeminiEnvironment env = new GeminiEnvironment();
    static NetworkProviderOpenStackImpl openStack = new NetworkProviderOpenStackImpl();
    
    public GeminiOpenStackJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        //setup the tenant 
        tenant.setAdminUserName("srikumar.chari@apollo.edu");
        tenant.setName("srikumar.chari@apollo.edu");
        tenant.setAdminPassword("I7ixzxzN");
        tenant.setEndPoint("http://158.85.165.2:5000/v2.0");
        tenant.setDomainName("");
        tenant.setTenantID("eeb8072a87af464280531ae2f7a07c65");
        
        //setup the environment
        env.setName("Test Project");
        env.setType(GeminiEnvironmentType.OPENSTACK);
        tenant.addEnvironment(env);
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
    @Test
    public void getExternalGatewaysTest() {
        List<GeminiNetwork> gateways = openStack.getExternalGateways(tenant, env);
        //we should only one have one gateway
        assert(gateways.size() == 1);
        gateways.stream().forEach(System.out::println);
    }
}
