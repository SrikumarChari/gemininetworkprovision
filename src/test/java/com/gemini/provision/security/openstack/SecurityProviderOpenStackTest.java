/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.security.openstack;

import com.gemini.domain.common.GeminiEnvironmentType;
import com.gemini.domain.common.GeminiSecurityGroupRuleDirection;
import com.gemini.domain.common.IPAddressType;
import com.gemini.domain.common.Protocol;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiSecurityGroup;
import com.gemini.domain.model.GeminiSecurityGroupRule;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.security.base.SecurityProviderModule;
import com.gemini.provision.security.base.SecurityProvisioningService;
import com.google.common.net.InetAddresses;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openstack4j.api.OSClient;
import org.pmw.tinylog.Logger;

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
    static GeminiNetwork postgresNet = new GeminiNetwork();
    static GeminiSubnet subnet1 = new GeminiSubnet();
    static GeminiSubnet subnet2 = new GeminiSubnet();
    static GeminiSecurityGroup secGrp1 = new GeminiSecurityGroup();
    static GeminiSecurityGroupRule secGrp1Rule1 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroupRule secGrp1Rule2 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroupRule secGrp1Rule3 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroupRule secGrp1Rule4 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroup secGrp2 = new GeminiSecurityGroup();
    static GeminiSecurityGroupRule secGrp2Rule1 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroupRule secGrp2Rule2 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroup secGrp3 = new GeminiSecurityGroup();
    static GeminiSecurityGroupRule secGrp3Rule1 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroupRule secGrp3Rule2 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroup secGrp4 = new GeminiSecurityGroup();
    static GeminiSecurityGroupRule secGrp4Rule1 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroupRule secGrp4Rule2 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroup secGrp5 = new GeminiSecurityGroup();
    static GeminiSecurityGroupRule secGrp5Rule1 = new GeminiSecurityGroupRule();
    static GeminiSecurityGroupRule secGrp5Rule2 = new GeminiSecurityGroupRule();
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

        //security groups
        secGrp1.setName("Security Group 1");
        secGrp1.setDescription("Allow HTTP, HTTPS");
        secGrp1Rule1.setParent(secGrp1);
        secGrp1.addSecurityRule(secGrp1Rule1);
        secGrp1Rule1.setDirection(GeminiSecurityGroupRuleDirection.INGRESS);
        secGrp1Rule1.setIpAddressType(IPAddressType.IPv4);
        secGrp1Rule1.setName("secGrp1Rule1");
        secGrp1Rule1.setPortRangeMin(80);
        secGrp1Rule1.setPortRangeMax(80);
        secGrp1Rule1.setRemoteIpPrefix("192.162.1.0/24");
        secGrp1Rule1.setProtocol(Protocol.TCP);
        secGrp1Rule2.setParent(secGrp1);
        secGrp1.addSecurityRule(secGrp1Rule2);
        secGrp1Rule2.setDirection(GeminiSecurityGroupRuleDirection.EGRESS);
        secGrp1Rule2.setIpAddressType(IPAddressType.IPv4);
        secGrp1Rule2.setName("secGrp1Rule2");
        secGrp1Rule2.setPortRangeMin(80);
        secGrp1Rule2.setPortRangeMax(80);
        secGrp1Rule2.setRemoteIpPrefix("192.162.1.0/24");
        secGrp1Rule2.setProtocol(Protocol.TCP);
        secGrp1Rule3.setParent(secGrp1);
        secGrp1.addSecurityRule(secGrp1Rule3);
        secGrp1Rule3.setDirection(GeminiSecurityGroupRuleDirection.INGRESS);
        secGrp1Rule3.setIpAddressType(IPAddressType.IPv4);
        secGrp1Rule3.setName("secGrp1Rule3");
        secGrp1Rule3.setPortRangeMin(443);
        secGrp1Rule3.setPortRangeMax(443);
        secGrp1Rule3.setRemoteIpPrefix("192.162.1.0/24");
        secGrp1Rule3.setProtocol(Protocol.TCP);
        secGrp1Rule4.setParent(secGrp1);
        secGrp1.addSecurityRule(secGrp1Rule4);
        secGrp1Rule4.setDirection(GeminiSecurityGroupRuleDirection.EGRESS);
        secGrp1Rule4.setIpAddressType(IPAddressType.IPv4);
        secGrp1Rule4.setName("secGrp1Rule4");
        secGrp1Rule4.setPortRangeMin(443);
        secGrp1Rule4.setPortRangeMax(443);
        secGrp1Rule4.setRemoteIpPrefix("192.162.1.0/24");
        secGrp1Rule4.setProtocol(Protocol.TCP);
        env.addSecurityGroup(secGrp1);

        secGrp2.setName("Security Group 2");
        secGrp2.setDescription("Allow bi-directional SSH");
        secGrp2Rule1.setParent(secGrp2);
        secGrp2.addSecurityRule(secGrp2Rule1);
        secGrp2Rule1.setDirection(GeminiSecurityGroupRuleDirection.INGRESS);
        secGrp2Rule1.setIpAddressType(IPAddressType.IPv4);
        secGrp2Rule1.setName("secGrp2Rule1");
        secGrp2Rule1.setPortRangeMin(22);
        secGrp2Rule1.setPortRangeMax(22);
        secGrp2Rule1.setRemoteIpPrefix("192.162.1.0/24");
        secGrp2Rule1.setProtocol(Protocol.TCP);
        secGrp2Rule2.setParent(secGrp2);
        secGrp2.addSecurityRule(secGrp2Rule2);
        secGrp2Rule2.setDirection(GeminiSecurityGroupRuleDirection.EGRESS);
        secGrp2Rule2.setIpAddressType(IPAddressType.IPv4);
        secGrp2Rule2.setName("secGrp2Rule2");
        secGrp2Rule2.setPortRangeMin(22);
        secGrp2Rule2.setPortRangeMax(22);
        secGrp2Rule2.setRemoteIpPrefix("192.162.1.0/24");
        secGrp2Rule2.setProtocol(Protocol.TCP);
        env.addSecurityGroup(secGrp2);

        secGrp3.setName("Security Group 3");
        secGrp3.setDescription("Allow MongoDB instances to listen for connections");
        secGrp3Rule1.setParent(secGrp3);
        secGrp3.addSecurityRule(secGrp3Rule1);
        secGrp3Rule1.setDirection(GeminiSecurityGroupRuleDirection.INGRESS);
        secGrp3Rule1.setIpAddressType(IPAddressType.IPv4);
        secGrp3Rule1.setName("secGrp3Rule1");
        secGrp3Rule1.setPortRangeMin(27017);
        secGrp3Rule1.setPortRangeMax(27017);
        secGrp3Rule1.setRemoteIpPrefix("192.162.2.0/24");
        secGrp3Rule1.setProtocol(Protocol.TCP);
        secGrp3Rule2.setParent(secGrp3);
        secGrp3.addSecurityRule(secGrp3Rule2);
        secGrp3Rule2.setDirection(GeminiSecurityGroupRuleDirection.EGRESS);
        secGrp3Rule2.setIpAddressType(IPAddressType.IPv4);
        secGrp3Rule2.setName("secGrp3Rule2");
        secGrp3Rule2.setPortRangeMin(27017);
        secGrp3Rule2.setPortRangeMax(27017);
        secGrp3Rule2.setRemoteIpPrefix("192.162.2.0/24");
        secGrp3Rule2.setProtocol(Protocol.TCP);
        env.addSecurityGroup(secGrp3);

        secGrp4.setName("Security Group 4");
        secGrp4.setDescription("Allow Postgres instances to listen for connections and serve requests");
        secGrp4Rule1.setParent(secGrp4);
        secGrp4.addSecurityRule(secGrp4Rule1);
        secGrp4Rule1.setDirection(GeminiSecurityGroupRuleDirection.INGRESS);
        secGrp4Rule1.setIpAddressType(IPAddressType.IPv4);
        secGrp4Rule1.setName("secGrp4Rule1");
        secGrp4Rule1.setPortRangeMin(5432);
        secGrp4Rule1.setPortRangeMax(5432);
        secGrp4Rule1.setRemoteIpPrefix("192.162.2.0/24");
        secGrp4Rule1.setProtocol(Protocol.TCP);
        secGrp4Rule2.setParent(secGrp4);
        secGrp4.addSecurityRule(secGrp4Rule2);
        secGrp4Rule2.setDirection(GeminiSecurityGroupRuleDirection.EGRESS);
        secGrp4Rule2.setIpAddressType(IPAddressType.IPv4);
        secGrp4Rule2.setName("secGrp4Rule2");
        secGrp4Rule2.setPortRangeMin(5432);
        secGrp4Rule2.setPortRangeMax(5432);
        secGrp4Rule2.setRemoteIpPrefix("192.162.2.0/24");
        secGrp4Rule2.setProtocol(Protocol.TCP);
        env.addSecurityGroup(secGrp4);

        secGrp5.setName("Security Group 5");
        secGrp5.setDescription("Allow appPod (a python app) to talk on port 5000; only traffic from web tier will be able to talk to the servers, but they can talk to anybody");
        secGrp5Rule1.setParent(secGrp5);
        secGrp5Rule1.setDirection(GeminiSecurityGroupRuleDirection.INGRESS);
        secGrp5Rule1.setIpAddressType(IPAddressType.IPv4);
        secGrp5Rule1.setName("secGrp4Rule1");
        secGrp5Rule1.setPortRangeMin(5000);
        secGrp5Rule1.setPortRangeMax(5000);
        secGrp5Rule1.setRemoteIpPrefix("192.162.2.0/24");
        secGrp5Rule1.setProtocol(Protocol.TCP);
        secGrp5Rule2.setParent(secGrp5);
        secGrp5Rule2.setDirection(GeminiSecurityGroupRuleDirection.EGRESS);
        secGrp5Rule2.setIpAddressType(IPAddressType.IPv4);
        secGrp5Rule2.setName("secGrp4Rule2");
        secGrp5Rule2.setPortRangeMin(5000);
        secGrp5Rule2.setPortRangeMax(5000);
        secGrp5Rule2.setRemoteIpPrefix("0.0.0.0/0");
        secGrp5Rule2.setProtocol(Protocol.TCP);
        env.addSecurityGroup(secGrp5);

        //create the provisioning service
        Injector provisioningInjector = Guice.createInjector(
                new SecurityProviderModule(env.getType()));
        provisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);

        //delete the security groups and rules if they exist from a previous JUnit runs        
        List<GeminiSecurityGroup> listGrps = provisioningService.getSecurityProvisioningService().listAllSecurityGroups(tenant, env);
        listGrps.stream()
                .filter(sg -> !sg.getName().equals("default"))
                .forEach(sg -> {
                    List<GeminiSecurityGroupRule> lstRules = provisioningService.getSecurityProvisioningService().listSecurityGroupRules(tenant, env, sg);
                    lstRules.stream().forEach(sgr -> {
                        provisioningService.getSecurityProvisioningService().deleteSecurityGroupRule(tenant, env, sg, sgr);
                    });
                    provisioningService.getSecurityProvisioningService().deleteSecurityGroup(tenant, env, sg);
                });
        listGrps = provisioningService.getSecurityProvisioningService().listAllSecurityGroups(tenant, env);
        assert(listGrps.size() == 1); //only the default rule should exist
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
    public void getSecurityGroups() {
        System.out.println("Get Security Groups test");
        List<GeminiSecurityGroup> secGrps = provisioningService.getSecurityProvisioningService().listAllSecurityGroups(tenant, env);
        //env.setSecurityGroups(secGrps);
        System.out.printf("Security Groups for Tenant: %s and Environment: %s\n", tenant.getName(), env.getName());
        secGrps.stream().forEach(s -> System.out.println(s.toString()));
        System.out.println();
    }

    @Test
    public void createSecurityGroup() {
        System.out.println("Create Security Group test");

        //get the number of groups
        int numGrps = provisioningService.getSecurityProvisioningService().listAllSecurityGroups(tenant, env).size();
        System.out.printf("Number of security groups before create: %d\n", numGrps);

        //create the security group
        ProvisioningProviderResponseType result = provisioningService.getSecurityProvisioningService().createSecurityGroup(tenant, env,
                env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp1.getName())).findFirst().get());
        assert (result == ProvisioningProviderResponseType.SUCCESS);

        //get the new number of groups and check it is more than one
        int newNumGrps = provisioningService.getSecurityProvisioningService().listAllSecurityGroups(tenant, env).size();
        System.out.printf("Number of security groups before create: %d\n", newNumGrps);
        assert (numGrps == newNumGrps - 1);

        System.out.printf("Created Security Group %s for Tenant: %s and Environment: %s\n", secGrp1.getName(), tenant.getName(), env.getName());
        System.out.println(env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp1.getName())).findFirst().get());
        System.out.println();
    }

    @Test
    public void updateSecurityGroup() {
        System.out.println("Update Security Group test");

        //first create a new security group
            GeminiSecurityGroup tmpGrp = env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp2.getName())).findFirst().get();
        
        ProvisioningProviderResponseType result = provisioningService.getSecurityProvisioningService().createSecurityGroup(tenant, env, tmpGrp);
        assert (result == ProvisioningProviderResponseType.SUCCESS);

        if (tmpGrp.isProvisioned()) {
            //successfull, so now change the name 
            System.out.printf("Security Group before name change: %s\n", tmpGrp);
            String oldName = tmpGrp.getName();
            tmpGrp.setName("Name changed for testing");
            result = provisioningService.getSecurityProvisioningService().updateSecurityGroup(tenant, env, tmpGrp);
            assert (result == ProvisioningProviderResponseType.SUCCESS);
            System.out.printf("Security Group after name change: %s\n", tmpGrp);

            //now change the name back to what it was
            tmpGrp.setName(oldName);
            result = provisioningService.getSecurityProvisioningService().updateSecurityGroup(tenant, env, tmpGrp);
            assert (result == ProvisioningProviderResponseType.SUCCESS);
            System.out.println(env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp2.getName())).findFirst().get());
        }
        System.out.println();
    }

    @Test
    public void deleteSecurityGroup() {
        System.out.println("Delete Security Group test");
        //first create a new security group
        GeminiSecurityGroup tmpGrp = env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp3.getName())).findFirst().get();
        ProvisioningProviderResponseType result = ProvisioningProviderResponseType.CLOUD_FAILURE;
        if (!tmpGrp.isProvisioned()) {
            result = provisioningService.getSecurityProvisioningService().createSecurityGroup(tenant, env, tmpGrp);
        }

        if (result == ProvisioningProviderResponseType.SUCCESS) {
            List<GeminiSecurityGroup> lstGrps = provisioningService.getSecurityProvisioningService().listAllSecurityGroups(tenant, env);
            int numGrps = lstGrps.size();
            System.out.printf("Number of security groups before delete: %d\nSecurity Groups:\n", numGrps);
            lstGrps.stream().forEach(s -> System.out.println(s));
            result = provisioningService.getSecurityProvisioningService().deleteSecurityGroup(tenant, env, tmpGrp);
            assert (result == ProvisioningProviderResponseType.SUCCESS);

            lstGrps = provisioningService.getSecurityProvisioningService().listAllSecurityGroups(tenant, env);
            int newNumGrps = lstGrps.size();
            assert (numGrps == newNumGrps + 1);
            System.out.printf("Number of security groups after delete: %d\nSecurity Groups:\n", newNumGrps);
            lstGrps.stream().forEach(s -> System.out.println(s));
        }
        System.out.println();
    }

    @Test
    public void createSecurityGroupRule() {
        System.out.println("Create Security Group rule test");

        //first create the security group
        //create the security group
        GeminiSecurityGroup tmpGroup = env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get();
        ProvisioningProviderResponseType result = provisioningService.getSecurityProvisioningService().createSecurityGroup(tenant, env, tmpGroup);
        assert (result == ProvisioningProviderResponseType.SUCCESS);

        //now add the security group rule to group5
        tmpGroup.addSecurityRule(secGrp5Rule1);
        result = provisioningService.getSecurityProvisioningService().createSecurityGroupRule(tenant, env, tmpGroup, secGrp5Rule1);
        assert (result == ProvisioningProviderResponseType.SUCCESS);
        System.out.printf("Successfully created security rule %s for Security Group %s\n",
                secGrp5Rule1.getCloudID(), tmpGroup);
        System.out.println();
    }

    @Test
    public void updateSecurityGroupRule() {
        System.out.println("Update Security Group rule test");

        //first create the security group
        //create the security group
        GeminiSecurityGroup tmpGroup = env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get();
        ProvisioningProviderResponseType result = provisioningService.getSecurityProvisioningService().createSecurityGroup(tenant, env, tmpGroup);
        //don't assert here... chances are the group 5 may have already been created

        //now add the security group rule to group5
        tmpGroup.addSecurityRule(secGrp5Rule2);
        result = provisioningService.getSecurityProvisioningService().createSecurityGroupRule(tenant, env, tmpGroup, secGrp5Rule2);
        assert (result == ProvisioningProviderResponseType.SUCCESS);
        System.out.printf("Update rule test - successfully created security rule %s for Security Group %s\n", secGrp5Rule1.getCloudID(), tmpGroup.getName());

        //now change the security rule
        String oldIpPrefix = secGrp5Rule2.getRemoteIpPrefix();
        secGrp5Rule2.setRemoteIpPrefix("10.10.10.0/24");
        result = provisioningService.getSecurityProvisioningService().updateSecurityGroupRule(tenant, env,
                env.getSecurityGroups()
                .stream()
                .filter(sg -> sg.getName().equals(secGrp5.getName()))
                .findFirst().get(),
                secGrp5Rule2);
        assert (result == ProvisioningProviderResponseType.SUCCESS);
        System.out.printf("Update rule test - Successfully updated security rule %s old IP Prefix: %s New Ip Prefix: %s for Security Group %s\n",
                secGrp5Rule1.getCloudID(),
                oldIpPrefix, secGrp5Rule2.getRemoteIpPrefix(),
                env.getSecurityGroups()
                .stream()
                .filter(sg -> sg.getName().equals(secGrp5.getName()))
                .findFirst().get()
                .getName());
        System.out.println();
    }

    @Test
    public void deleteSecurityRule() {
        System.out.println("Delete Security Group rule test");

        //get the object that matches secGrp5Rule1
        GeminiSecurityGroupRule tmpRule = null;
        try {
            tmpRule = env.getSecurityGroups().stream()
                    .filter(s -> s.getName().equals(secGrp5.getName()))
                    .map(GeminiSecurityGroup::getSecurityRules)
                    .flatMap(List::stream)
                    .filter(sr -> sr.equals(secGrp5Rule1))
                    .findFirst().get();
        } catch (NoSuchElementException ex) {
            return;
        }

        if (tmpRule != null) {
            if (!tmpRule.isProvisioned()) {
                //the rule is not provisioned yet...
                ProvisioningProviderResponseType result = provisioningService.getSecurityProvisioningService().createSecurityGroupRule(tenant, env,
                        env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get(),
                        secGrp5Rule1);
                if (result != ProvisioningProviderResponseType.SUCCESS) {
                    Logger.error("Failed to create the security group rule required for deletion");
                }
            }

            //print the number of rules before deletion
            List<GeminiSecurityGroupRule> rules = provisioningService.getSecurityProvisioningService().listSecurityGroupRules(tenant, env,
                    env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get());
            int numBeforeDelete = rules.size();
            System.out.printf("Number of rules for Security Group: %s is %d before deletion",
                    env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get().getName(),
                    numBeforeDelete);

            //now delete it
            ProvisioningProviderResponseType result = provisioningService.getSecurityProvisioningService().deleteSecurityGroupRule(tenant, env,
                    env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get(),
                    secGrp5Rule1);
            assert (result == ProvisioningProviderResponseType.SUCCESS);

            //once again get the rules and see if it was successfully deleted
            rules = provisioningService.getSecurityProvisioningService().listSecurityGroupRules(tenant, env,
                    env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get());
            int numRulesAfterDelete = rules.size();
            assert (numBeforeDelete == numRulesAfterDelete + 1);

            System.out.printf("Number of rules for Security Group: %s is %d after deletion",
                    env.getSecurityGroups().stream().filter(sg -> sg.getName().equals(secGrp5.getName())).findFirst().get().getName(),
                    rules.size());
        }
        System.out.println();
    }
}
