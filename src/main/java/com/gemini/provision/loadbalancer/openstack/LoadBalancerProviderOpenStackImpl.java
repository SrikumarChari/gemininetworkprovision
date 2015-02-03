/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.openstack;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiLoadBalancer;
import com.gemini.domain.model.GeminiLoadBalancerHealthMonitor;
import com.gemini.domain.model.GeminiLoadBalancerPool;
import com.gemini.domain.model.GeminiPoolMember;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.loadbalancer.base.LoadBalancerProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class LoadBalancerProviderOpenStackImpl implements LoadBalancerProvider {

    @Override
    public List<GeminiLoadBalancer> listAllVIPs(GeminiTenant tenant, GeminiEnvironment env) {
        List<GeminiLoadBalancer> vips = Collections.synchronizedList(new ArrayList());

        //authenticate with the server
        URL url;
        try {
            url = new URL(tenant.getEndPoint());
        } catch (MalformedURLException ex) {
            Logger.error("Invalid Endpoint - {} not a valid URL. Exception: {}", tenant.getEndPoint(), ex.getMessage());
            return null;
        }
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(url.getHost(), 443),
                new UsernamePasswordCredentials(tenant.getAdminUserName(), tenant.getAdminPassword()));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpGet httpGet = new HttpGet(tenant.getEndPoint());
            //System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpGet);
            try {
                //get the response status code 
                String respStatus = response.getStatusLine().getReasonPhrase();
                
                //get the response body
                EntityUtils.consume(response.getEntity());
                int bytes = response.getEntity().getContent().available();
                InputStream body = response.getEntity().getContent();
                BufferedReader in =  new BufferedReader (new InputStreamReader (body));
                String line;
                StringBuffer sbJSON = new StringBuffer();
                while ( (line = in.readLine()) != null) {
                    sbJSON.append(line);
                }
                String json = sbJSON.toString();
            } catch (IOException ex) {
                Logger.error(ex);
            } finally {
                response.close();
            }
        } catch (IOException ex) {
            Logger.error(ex);
        } finally {
            //lots of exceptions, just the connection and exit
            try {
                httpclient.close();
            } catch (IOException ex) {
                Logger.error(ex);
                return null;
            }
        }

        return vips;
    }

    @Override
    public ProvisioningProviderResponseType createVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiLoadBalancer getVIP(GeminiTenant tenant, GeminiEnvironment env, String vipID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteVIP(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancer lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiLoadBalancerHealthMonitor> listAllHealthMonitors(GeminiTenant tenant, GeminiEnvironment env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiLoadBalancerHealthMonitor getHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, String lb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiLoadBalancerPool> listAllPools(GeminiTenant tenant, GeminiEnvironment env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiLoadBalancerPool getPool(GeminiTenant tenant, GeminiEnvironment env, String poolID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType createLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updateLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deleteLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType associateHealthMonitorToPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType disassociateHealthMonitorFromPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeminiPoolMember> getPoolMembers(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType addPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GeminiPoolMember getPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, String poolMemberID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType updatePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ProvisioningProviderResponseType deletePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
