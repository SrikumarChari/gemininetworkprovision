package com.gemini.provision.main;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiEnvironmentType;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.network.base.NetworkProvider;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.network.base.NetworkProvisioningService;
import com.gemini.provision.network.openstack.NetworkProviderOpenStackImpl;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author schari
 */
public class GeminiProvisionMain {

    static GeminiTenant tenant = new GeminiTenant();
    static GeminiEnvironment env = new GeminiEnvironment();
    static NetworkProvisioningService provisioningService;

    public static void main(String[] args) {
        //setup the tenant 
        tenant.setAdminUserName("srikumar.chari@apoll.edu");
        tenant.setAdminPassword("Slov6ni5");
        tenant.setEndPoint("http://158.85.165.2:9696");
        tenant.setDomainName("");
        tenant.setTenantID("eeb8072a87af464280531ae2f7a07c65");

        //setup the environment
        env.setName("Test Project");
        env.setType(GeminiEnvironmentType.OPENSTACK);
        tenant.addEnvironment(env);

        for (GeminiEnvironmentType c : GeminiEnvironmentType.values()) {
            System.out.println(c);
        }

        //create the provisioning service 
        Injector injector = Guice.createInjector(
                new NetworkProviderModule(env.getType()));
        provisioningService = injector.getInstance(NetworkProvisioningService.class);

//        List<GeminiNetwork> gateways = openStack.getExternalGateways(tenant, env);
//        //we should only one have one gateway
//        assert (gateways.size() == 1);
//        gateways.stream().forEach(System.out::println);
    }
}
