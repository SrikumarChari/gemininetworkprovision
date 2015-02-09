/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.openstack;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.domain.common.IPAddressType;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.network.base.NetworkProvisioningService;
import com.google.common.net.InetAddresses;
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
public class NetworkProviderOpenStackTest {

    static GeminiTenant tenant = new GeminiTenant();
    static GeminiEnvironment env = new GeminiEnvironment();
    static GeminiApplication newApp = new GeminiApplication();
    static GeminiNetwork postgresNet = new GeminiNetwork();
    static GeminiSubnet subnet1 = new GeminiSubnet();
    static GeminiSubnet subnet2 = new GeminiSubnet();
    static NetworkProvisioningService provisioningService;
    static OSClient os;

    public NetworkProviderOpenStackTest() {
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
        env.addApplication(newApp);

        //create the test application network
        postgresNet = new GeminiNetwork();
        postgresNet.setName("Application 1 Network");
        postgresNet.setDescription("Postgres Network");
        postgresNet.setNetworkType("Class C");
        postgresNet.setProvisioned(false);
        newApp.addNetwork(postgresNet);

        //create the subnets and allocation pools
        subnet1.setName("subnet-for-databases");
        subnet1.setCidr("192.162.1.0/24");
        subnet1.setNetworkType(IPAddressType.IPv4);
        subnet1.setParent(postgresNet);
        subnet1.setProvisioned(false);
        subnet1.setGateway(InetAddresses.forString("192.168.0.1"));
        subnet1.addAllocationPool(InetAddresses.forString("192.161.2.1"), InetAddresses.forString("192.161.2.50"));
        subnet1.addAllocationPool(InetAddresses.forString("191.161.2.51"), InetAddresses.forString("192.161.2.100"));
        postgresNet.addSubnet(subnet1);

        subnet2.setName("subnet-for-servers-15-20");
        subnet2.setCidr("192.162.2.0/24");
        subnet1.setNetworkType(IPAddressType.IPv4);
        subnet2.setParent(postgresNet);
        subnet2.setProvisioned(false);
        subnet2.setGateway(InetAddresses.forString("192.168.0.1"));
        subnet2.addAllocationPool(InetAddresses.forString("192.161.1.1"), InetAddresses.forString("192.161.1.50"));
        subnet2.addAllocationPool(InetAddresses.forString("192.161.1.51"), InetAddresses.forString("192.161.1.100"));
        postgresNet.addSubnet(subnet2);

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

    @Test
    public void listGateways() {
        System.out.println("List Gateways Test");
        List<GeminiNetwork> gateways = provisioningService.getProvider().getExternalGateways(tenant, env);
        //we should only one have one gateway
        assert (gateways.size() == 1);

        //keep a record of the first one, required for further tests
        env.setGateways(gateways);

        System.out.print("Gateways in the cloud: ");
        env.getGateways().stream().forEach(n -> System.out.print(n.getName() + ", "));
        System.out.println();
        System.out.println();
    }

    @Test
    public void listNetworks() {
        System.out.println("List Networks Test");
        List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
        assert (networks.size() == 3);

        //add the networks to the application
        //networks.stream().forEach(n -> newApp.addNetwork(n));
        System.out.print("Networks in the cloud: ");
        networks.stream().forEach(n -> System.out.print(n.getName() + ", "));
        System.out.println();
        System.out.println();
    }

    @Test
    public void createNetwork() {
        System.out.println("Create Network Test");
        //first get the number of networks in the cloud
        int numNets = provisioningService.getProvider().getNetworks(tenant, env).size();
        System.out.printf("Number of networks: %d\n", numNets);

        //create the network
        ProvisioningProviderResponseType result = provisioningService.getProvider().createNetwork(tenant, env, postgresNet);

        //check the return value
        assert (result == ProvisioningProviderResponseType.SUCCESS);
        if (result == ProvisioningProviderResponseType.SUCCESS) {
            List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
            System.out.printf("Successfully created network: %s\n", postgresNet.getName());
            System.out.printf("Number of networks after creation: %d\n", networks.size());

            //now check to see if the network was actually created
            assert (numNets == networks.size() - 1);

            //now check to see if the new network was actually created
            assert (networks.stream().filter(s -> s.getName().equals(postgresNet.getName())).count() == 1);
            System.out.print("Networks in the cloud: ");
            networks.stream().forEach(n -> System.out.print(n.getName() + ", "));
            System.out.println();
        }
        System.out.println();
    }

//    @Test
//    public void deleteNetwork() {
//        System.out.println("Delete Network Test");
//        //first get the number of networks in the cloud
//        int numNets = provisioningService.getProvider().getNetworks(tenant, env).size();
//        System.out.printf("Number of networks before delete: %d\n", numNets);
//
//        //now delete the network
//        ProvisioningProviderResponseType result = provisioningService.getProvider().deleteNetwork(tenant, env, postgresNet);
//
//        //check the return value
//        assert (result == ProvisioningProviderResponseType.SUCCESS);
//        if (result == ProvisioningProviderResponseType.SUCCESS) {
//            List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
//            System.out.printf("Successfully deleted network: %s\n", postgresNet.getName());
//            System.out.printf("Number of networks after delete: %d\n", networks.size());
//            //now check to see if the network was actually created
//            assert (numNets == networks.size() + 1);
//            assert (networks.stream().filter(s -> s.getName().equals(postgresNet.getName())).count() == 0);
//            System.out.print("Networks in the cloud: ");
//            networks.stream().forEach(n -> System.out.print(n.getName() + ", "));
//            System.out.println();
//        }
//        System.out.println();
//    }

    @Test
    public void updateNetwork() {
        System.out.println("Update Network Test");
        //change the name of the network
        String oldName = postgresNet.getName();
        postgresNet.setName("Temporary Change");
        ProvisioningProviderResponseType result = provisioningService.getProvider().updateNetwork(tenant, env, postgresNet);
        //check the return value
        assert (result == ProvisioningProviderResponseType.SUCCESS);
        if (result == ProvisioningProviderResponseType.SUCCESS) {
            //change the name back to the old name
            postgresNet.setName(oldName);
            result = provisioningService.getProvider().updateNetwork(tenant, env, postgresNet);
            //check the return value
            assert (result == ProvisioningProviderResponseType.SUCCESS);
        }
        System.out.println();
    }

    @Test
    public void listAllSubnets() {
        System.out.println("List All Subnets Test");
        List<GeminiSubnet> networks = provisioningService.getProvider().getAllSubnets(tenant, env);

        System.out.print("Subnets in the cloud: ");
        networks.stream().forEach(n -> System.out.print(n.getName() + " ,"));
        System.out.println();
    }

    @Test
    public void listNetworkSubnets() {
        System.out.println("List Subnets for all networks Test");
        //gets all the networks and lists their subnets
        List<GeminiNetwork> networks = provisioningService.getProvider().getNetworks(tenant, env);
        networks.stream().forEach(n -> {
            List<GeminiSubnet> subnets = provisioningService.getProvider().getSubnets(tenant, env, n);
            System.out.printf("Subnets for %s: ", n.getName());
            subnets.stream().forEach(s -> System.out.print(s.getName() + " ,"));
            System.out.println();
        });
        System.out.println();
    }

    @Test
    public void createSubnet() {
        System.out.println("Create subnet Test");
        ProvisioningProviderResponseType result = provisioningService.getProvider().createSubnet(tenant, env, postgresNet, subnet1);
        assert (result == ProvisioningProviderResponseType.SUCCESS);

    }
}
