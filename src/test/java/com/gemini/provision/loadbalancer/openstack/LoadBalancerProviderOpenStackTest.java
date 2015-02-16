package com.gemini.provision.loadbalancer.openstack;

import com.gemini.domain.common.*;
import com.gemini.domain.model.*;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.loadbalancer.base.LoadBalancerProviderModule;
import com.gemini.provision.loadbalancer.base.LoadBalancerProvisioningService;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.network.base.NetworkProvisioningService;
import com.google.common.net.InetAddresses;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;

import java.util.List;

/**
 * @author t.varada.
 */
public class LoadBalancerProviderOpenStackTest {
    static GeminiTenant tenant = new GeminiTenant();
    static GeminiEnvironment env = new GeminiEnvironment();
    static GeminiApplication newApp = new GeminiApplication();
    static OSClient os;
    static LoadBalancerProvisioningService provisioningService;
    static NetworkProvisioningService networkProvisioningService;
    static GeminiNetwork lbNetwork = new GeminiNetwork();
    static GeminiSubnet subnet1 = new GeminiSubnet();

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

        Injector provisioningInjector = Guice.createInjector(
                new LoadBalancerProviderModule(env.getType()),new NetworkProviderModule(env.getType()));
        provisioningService = provisioningInjector.getInstance(LoadBalancerProvisioningService.class);
        networkProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);
    }

    /*
     Follow the convention of having LB as suffix for easy test cases that can fetch the
     network later that belongs to load balancer
     */
//    @Test
//    public void createNetwork(){
//        lbNetwork = new GeminiNetwork();
//        lbNetwork.setName("Application 1 Network-LB");
//        lbNetwork.setDescription("Load Balance Network");
//        lbNetwork.setNetworkType("Class C");
//        lbNetwork.setProvisioned(false);
//        newApp.addNetwork(lbNetwork);
//        ProvisioningProviderResponseType provisioningProviderResponseType = networkProvisioningService.getProvider().createNetwork(tenant,env,lbNetwork);
//        System.out.println("Network create response type:" + provisioningProviderResponseType);
//    }

//        @Test
//        public void getLBNetworks(){
//            List<GeminiNetwork> networks = networkProvisioningService.getProvider().getNetworks(tenant,env);
//            lbNetwork = networks.stream().filter(network -> network.getName().endsWith("LB") ).findFirst().get();
//            System.out.println("Network Name:" + lbNetwork);
//        }

    /*
     Use 192.168.1.255 range of address for load balancer so as not to conflict with
     other test cases that create subnets for its domain objects.
     */
//    @Test
//    public void createSubnetForLB(){
//        subnet1.setName("subnet-for-loadBalance-LB");
//        subnet1.setCidr("192.168.1.0/24");
//        subnet1.setNetworkType(IPAddressType.IPv4);
//        subnet1.setParent(lbNetwork);
//        subnet1.setProvisioned(false);
//        subnet1.setGateway(InetAddresses.forString("192.168.1.1"));
//        subnet1.addAllocationPool(InetAddresses.forString("192.168.1.1"), InetAddresses.forString("192.168.2.50"));
//        //subnet1.addAllocationPool(InetAddresses.forString("192.168.2.51"), InetAddresses.forString("192.168.2.100"));
//        lbNetwork.addSubnet(subnet1);
//        ProvisioningProviderResponseType provisioningProviderResponseType = networkProvisioningService.getProvider().createSubnet(tenant,env,lbNetwork,subnet1);
//        System.out.println("Subnet create response type:" + provisioningProviderResponseType);
//    }

    @Test
    public void getLBSubnet() {
        System.out.println("List All Subnets Test");
        List<GeminiSubnet> networks = networkProvisioningService.getProvider().getAllSubnets(tenant, env);

        subnet1 = networks.stream().filter(subnet -> subnet.getName().endsWith("LB")).findFirst().get();
        System.out.println("Subnet found:" + subnet1.getName());
        System.out.println("Clud id:" + subnet1.getCloudID());
    }


    /*@Test
    public void listAllLBMember(){
        List<GeminiPoolMember> poolMembers = provisioningService.getLoadBalancerProvisioningService().getPoolMembers(tenant, null, null);
        System.out.println("List of pool Members:" + poolMembers.size());
    }*/

    @Test
    public void createLBPool(){
        GeminiLoadBalancerPool loadBalancerPool = new GeminiLoadBalancerPool();
        loadBalancerPool.setLoadBalancerAlgorithm(LoadBalancerAlgorithm.ROUND_ROBIN);
        loadBalancerPool.setAdminState(AdminState.ADMIN_UP);
        loadBalancerPool.setDescription("Test Load Balancer Pool created by JUnit");
        loadBalancerPool.setName("Dev Application LB Pool");
        loadBalancerPool.setProtocol(Protocol.HTTP);
        loadBalancerPool.setGeminiSubnet(subnet1);
        ProvisioningProviderResponseType provisioningProviderResponseType = provisioningService.getLoadBalancerProvisioningService().createLBPool(tenant,env,loadBalancerPool);
        System.out.println(provisioningProviderResponseType);
    }
}
