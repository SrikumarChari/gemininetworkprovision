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
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.network.base.NetworkProviderResponseType;
import com.gemini.provision.network.base.NetworkProvisioningService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstack4j.api.OSClient;

/**
 *
 * @author schari
 */
public class GeminiOpenStackJUnitTest {

    static GeminiTenant tenant = new GeminiTenant();
    static GeminiEnvironment env = new GeminiEnvironment();
    static GeminiApplication newApp = new GeminiApplication();
    static GeminiNetwork newNet = new GeminiNetwork();
    static NetworkProvisioningService provisioningService;
    static OSClient os;

    public GeminiOpenStackJUnitTest() {
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
                new NetworkProviderModule(env.getType()));
        provisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);
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
    public void listNetworks() {
        List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
        System.out.println("Networks in the cloud");
        assert (networks.size() == 2);
        
        //add the networks to the application
        networks.stream().forEach(n -> newApp.addNetwork(n));
        networks.stream().forEach(n -> System.out.println(n.getName()));
    }

    @Test
    public void listGateways() {
        List<GeminiNetwork> gateways = provisioningService.getProvider().getExternalGateways(tenant, env);
        //we should only one have one gateway
        assert (gateways.size() == 1);
        
        //keep a record of the first one, required for further tests
        env.setGateway(gateways.get(0));
        
        System.out.println("Gateways in the cloud");
        gateways.stream().forEach(n -> System.out.println(n.getName()));
    }

    @Test
    public void createNetwork() {
        //create the network
        NetworkProviderResponseType result = provisioningService.getProvider().createNetwork(tenant, env, newNet);
        //check the return value
        assert (result == NetworkProviderResponseType.SUCCESS);

        //now check to see if the network was actually created
        List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
        assert (networks.stream().filter(s -> s.getName().equals(newNet.getName())).count() == 1);
    }

    @Test
    public void deleteNetwork() {
        //now delete the network
        NetworkProviderResponseType result = provisioningService.getProvider().deleteNetwork(tenant, env, newNet);
        //check the return value
        assert (result == NetworkProviderResponseType.SUCCESS);

        //now check to see if the network was actually created
        List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
        assert (networks.stream().filter(s -> s.getName().equals(newNet.getName())).count() == 0);
    }
    
    @Test
    public void updateNetwork () {
        //change the name of the network
        String oldName = newNet.getName();
        newNet.setName("Temporary Change");
        NetworkProviderResponseType result = provisioningService.getProvider().updateNetwork(tenant, env, newNet);
        //check the return value
        assert (result == NetworkProviderResponseType.SUCCESS);

        //change the name based to the old name
        newNet.setName(oldName);
        result = provisioningService.getProvider().updateNetwork(tenant, env, newNet);
        //check the return value
        assert (result == NetworkProviderResponseType.SUCCESS);
    }
    
    @Test
    public void listAllSubnets() {
        List<GeminiSubnet> networks = provisioningService.getProvider().getAllSubnets(tenant, env);
        System.out.println("Subnet in the cloud");
        networks.stream().forEach(n -> System.out.println(n.getName()));
    }
    
    @Test
    public void listNetworkSubnets() {
        //gets all the networks and lists their subnets
        List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
        networks.stream().forEach(n -> {
            List<GeminiSubnet> subnets = provisioningService.getProvider().getSubnets(tenant, env, n);
            System.out.println("Subnets for " + n.getName());
            subnets.stream().forEach(s -> System.out.println(s.getName()));
        });
    }
}
