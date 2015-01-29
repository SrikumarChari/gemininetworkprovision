/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.openstack;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.network.base.NetworkProviderResponseType;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.network.Network;
import org.openstack4j.openstack.OSFactory;

/**
 *
 * @author schari
 */
public class GeminiOpenStackJUnitTest {

    static GeminiTenant tenant = new GeminiTenant();
    static GeminiEnvironment env = new GeminiEnvironment();
    static GeminiApplication newApp = new GeminiApplication();
    static GeminiNetwork newNet = new GeminiNetwork();
    static NetworkProviderOpenStackImpl openStack = new NetworkProviderOpenStackImpl();
    static OSClient os;

    public GeminiOpenStackJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
    public void listNetworksTest() {
        List<GeminiNetwork> networks = openStack.getNetworks(tenant, env);
        System.out.println("Networks in the cloud");
        assert(networks.size() == 2);
        networks.stream().forEach(n -> System.out.println(n.getName()));
    }

    @Test
    public void listGateways() {
        List<GeminiNetwork> gateways = openStack.getExternalGateways(tenant, env);
        //we should only one have one gateway
        assert (gateways.size() == 1);
        System.out.println("Gateways in the cloud");
        gateways.stream().forEach(n -> System.out.println(n.getName()));
    }
    
    @Test
    public void createNetwork() {
        //create the network
        NetworkProviderResponseType result = openStack.createNetwork(tenant, env, newNet);
        //check the return value
        assert (result == NetworkProviderResponseType.SUCCESS);
        
        //now check to see if the network was actually created
        List<GeminiNetwork> networks = openStack.getNetworks(tenant, env);
        assert(networks.stream().filter(s -> s.getName().equals(newNet.getName())).count() == 1);
    }
    
    @Test
    public void deleteNetwork() {
        //now delete the network
        NetworkProviderResponseType result = openStack.deleteNetwork(tenant, env, newNet);
        //check the return value
        assert (result == NetworkProviderResponseType.SUCCESS);

        //now check to see if the network was actually created
        List<GeminiNetwork> networks = openStack.getNetworks(tenant, env);
        assert(networks.stream().filter(s -> s.getName().equals(newNet.getName())).count() == 0);
        
    }
}
