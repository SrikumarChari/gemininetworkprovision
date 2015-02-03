/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.openstack;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.LoadBalancerAlgorithm;
import com.gemini.domain.common.Protocol;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiLoadBalancer;
import com.gemini.domain.model.GeminiLoadBalancerHealthMonitor;
import com.gemini.domain.model.GeminiLoadBalancerPool;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiPoolMember;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.loadbalancer.base.LoadBalancerProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jersey.repackaged.com.google.common.net.InetAddresses;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ClientResponseException;
import org.openstack4j.model.network.ext.LbPool;
import org.openstack4j.model.network.ext.Vip;
import org.openstack4j.openstack.OSFactory;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class LoadBalancerProviderOpenStackImpl implements LoadBalancerProvider {

    @Override
    public List<GeminiLoadBalancer> listAllVIPs(GeminiTenant tenant, GeminiEnvironment env) {
        List<GeminiLoadBalancer> vips = Collections.synchronizedList(new ArrayList());

        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //try to get the list of VIP's
        List<? extends Vip> osVips;
        try {
            osVips = os.networking().loadbalancers().vip().list();
        } catch (ClientResponseException e) {
            Logger.error("Cloud failure - could not retrieve load balancer Vips. Exception: {}", e);
            return null;
        }

        //copy them to the gemini load balancer objects
        osVips.stream().filter(v -> v != null).forEach(v -> {
            GeminiLoadBalancer newLB = new GeminiLoadBalancer();

            //the simple stuff
            newLB.setCloudID(v.getId());
            newLB.setVirtualPvtIP(InetAddresses.forString(v.getAddress()));

            //Load balancer object references a subnet - this subnet must be 
            //previously created and therefore MSUT BE AVAILABLE in the environment
            //scan the environment networks and find the subnet
            GeminiSubnet subnet = env.getApplications().stream()
                    .map(GeminiApplication::getNetworks)
                    .flatMap(List::stream)
                    .map(GeminiNetwork::getSubnets)
                    .flatMap(List::stream)
                    .filter(s -> s.getCloudID().equals(v.getId()))
                    .findFirst().get();
            if (subnet == null) {
                Logger.info("Load Balancer cloud ID {} references a subnet not available in environment {}  Subnet ID: {}",
                        v.getId(), env.getName(), v.getSubnetId());
            } else {
                newLB.setVirtualPvtSubnet(subnet);
            }
            
            //now the pool
            String poolID = v.getPoolId();
            String protocol = v.getProtocol();
            vips.add(newLB);
        });
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
        List<GeminiLoadBalancerPool> lbPools = Collections.synchronizedList(new ArrayList());

        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }
        List<? extends LbPool> osLbPools = os.networking().loadbalancers().lbPool().list();

        osLbPools.stream().filter(lbPool -> lbPool != null).forEach( lbPool -> {
                    GeminiLoadBalancerPool loadBalancerPool = new GeminiLoadBalancerPool();
                    loadBalancerPool.setCloudID(lbPool.getId());
                    loadBalancerPool.setName(lbPool.getName());
                    loadBalancerPool.setDescription(lbPool.getDescription());
                    //TODO get the VpId from the pool
                    loadBalancerPool.setVipID(lbPool.getVipId());
                    loadBalancerPool.setProtocol(Protocol.fromString(lbPool.getProtocol()));
                    loadBalancerPool.setLoadBalancerAlgorithm(LoadBalancerAlgorithm.fromString(lbPool.getLbMethod()));
                    //TODO set the pool member
                    loadBalancerPool.setAdminState(lbPool.isAdminStateUp()? AdminState.ADMIN_UP : AdminState.ADMIN_DOWN);

                    GeminiSubnet subnet = env.getApplications().stream()
                            .map(GeminiApplication::getNetworks)
                            .flatMap(List::stream)
                            .map(GeminiNetwork::getSubnets)
                            .flatMap(List::stream)
                            .filter(s -> s.getCloudID().equals(lbPool.getId()))
                            .findFirst().get();
                    if (subnet == null) {
                        Logger.info("Load Balancer cloud ID {} references a subnet not available in environment {}  Subnet ID: {}",
                                lbPool.getId(), env.getName(), lbPool.getSubnetId());
                    } else {
                        loadBalancerPool.setGeminiSubnet(subnet);
                    }
                    lbPools.add(loadBalancerPool);
                }
        );


        return lbPools;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
