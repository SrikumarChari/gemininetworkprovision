/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.loadbalancer.openstack;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.LoadBalancerAlgorithm;
import com.gemini.domain.common.Protocol;
import com.gemini.domain.common.ProvisionState;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiLoadBalancer;
import com.gemini.domain.model.GeminiLoadBalancerHealthMonitor;
import com.gemini.domain.model.GeminiLoadBalancerPool;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiPoolMember;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.model.GeminiTenant;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.loadbalancer.base.LoadBalancerProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gemini.provision.loadbalancer.utils.GeminiLBUtils;
import jersey.repackaged.com.google.common.net.InetAddresses;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ClientResponseException;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.network.ext.*;
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
        List<GeminiLoadBalancerHealthMonitor> gHealthMonitors = new ArrayList<>();
        OSClient os = getOSClient(tenant);
        List<? extends HealthMonitor> healthMonitors = os.networking().loadbalancers().healthMonitor().list();
        healthMonitors.stream().filter(healthMonitor -> healthMonitor != null).forEach(
                healthMonitor -> {
                    GeminiLoadBalancerHealthMonitor gHealthMonitor = GeminiLBUtils.getAsGeminiHealthMonitor(healthMonitor);
                    gHealthMonitors.add(gHealthMonitor);
                }
        );
        return gHealthMonitors;
    }

    @Override
    public GeminiLoadBalancerHealthMonitor getHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, String lb) {
        OSClient os = getOSClient(tenant);
        HealthMonitor healthMonitor = os.networking().loadbalancers().healthMonitor().get(lb);
        return GeminiLBUtils.getAsGeminiHealthMonitor(healthMonitor);
    }

    @Override
    public ProvisioningProviderResponseType createHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        OSClient os = getOSClient(tenant);
        HealthMonitor healthMonitor = os.networking().loadbalancers().healthMonitor().create(GeminiLBUtils.createHealthMonitor(hm,tenant));
        return getResponseType(healthMonitor,tenant,env,"created");
    }

    @Override
    public ProvisioningProviderResponseType updateHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        OSClient os = getOSClient(tenant);
        HealthMonitor healthMonitor = os.networking().loadbalancers().healthMonitor().update(hm.getCloudID(),GeminiLBUtils.healthMonitorUpdate(hm));
        return getResponseType(healthMonitor,tenant,env,"updated");
    }

    @Override
    public ProvisioningProviderResponseType deleteHealthMonitor(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerHealthMonitor hm) {
        OSClient os = getOSClient(tenant);
        ActionResponse actionResponse = os.networking().loadbalancers().healthMonitor().delete(hm.getCloudID());
        return getResponseType(actionResponse,hm,tenant,env,"Deleted");
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
    }

    @Override
    public GeminiLoadBalancerPool getPool(GeminiTenant tenant, GeminiEnvironment env, String poolID) {
        OSClient os = getOSClient(tenant);
        LbPool lbPool = os.networking().loadbalancers().lbPool().get(poolID);
        return GeminiLBUtils.getAsGeminiLoadBalancerPool(lbPool,env);
    }

    @Override
    public ProvisioningProviderResponseType createLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool geminiLoadBalancerPool) {
        OSClient os = getOSClient(tenant);
        LbPool lbPool = os.networking().loadbalancers().lbPool().create(GeminiLBUtils.createLBPool(geminiLoadBalancerPool,tenant));
        return getResponseType(lbPool,tenant,env,"created");
    }

    @Override
    public ProvisioningProviderResponseType updateLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool geminiLbPool) {
        OSClient os = getOSClient(tenant);
        LbPool lbPool = os.networking().loadbalancers().lbPool().update(geminiLbPool.getCloudID(),GeminiLBUtils.updateLBPool(geminiLbPool));
        return getResponseType(lbPool,tenant,env,"updated");
    }

    @Override
    public ProvisioningProviderResponseType deleteLBPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool) {
        OSClient os = getOSClient(tenant);
        ActionResponse actionResponse = os.networking().loadbalancers().lbPool().delete(lbPool.getCloudID());
        return getResponseType(actionResponse,lbPool,tenant,env,"deleted");
    }

    @Override
    public ProvisioningProviderResponseType associateHealthMonitorToPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm) {
        OSClient os = getOSClient(tenant);
        HealthMonitorAssociate associate = Builders.lbPoolAssociateHealthMonitor().id(hm.getCloudID()).build();
        HealthMonitor result = os.networking().loadbalancers().lbPool().associateHealthMonitor(lbPool.getCloudID(), associate);
        return getResponseType(result,tenant,env,"Health monitor associated ");
    }

    @Override
    public ProvisioningProviderResponseType disassociateHealthMonitorFromPool(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool lbPool, GeminiLoadBalancerHealthMonitor hm) {
        OSClient os = getOSClient(tenant);
        ActionResponse actionResponse = os.networking().loadbalancers().lbPool()
                .disAssociateHealthMonitor(lbPool.getCloudID(), hm.getCloudID());
        return getResponseType(actionResponse,lbPool,tenant,env,"Health monitor dissociated");

    }

    @Override
    public List<GeminiPoolMember> getPoolMembers(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool) {
        List<GeminiPoolMember> lbPools = Collections.synchronizedList(new ArrayList());

        //authenticate the session with the OpenStack installation
        OSClient os = getOSClient(tenant);
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }
        List<? extends Member> members = os.networking().loadbalancers().member().list();
        members.stream().filter(member -> member != null).forEach(member -> {
            GeminiPoolMember geminiPoolMember = new GeminiPoolMember();
            geminiPoolMember.setCloudID(member.getId());
            geminiPoolMember.setAdminState(member.isAdminStateUp()?AdminState.ADMIN_UP:AdminState.ADMIN_DOWN);
            geminiPoolMember.setProvisionState(ProvisionState.fromString(member.getStatus()));
            geminiPoolMember.setIpAddress(member.getAddress());
            geminiPoolMember.setProtocolPort(member.getProtocolPort());
            geminiPoolMember.setWeight(member.getWeight());
            geminiPoolMember.setPoolId(member.getPoolId());
            lbPools.add(geminiPoolMember);
        });
        return lbPools;
    }

    @Override
    public ProvisioningProviderResponseType addPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        OSClient os = getOSClient(tenant);
        Member member = Builders.member().address(poolMember.getIpAddress())
                .adminStateUp(poolMember.getAdminState() == AdminState.ADMIN_DOWN? false :true)
                .poolId(poolMember.getPoolId())
                .protocolPort(poolMember.getProtocolPort())
                .weight(poolMember.getWeight())
                .tenantId(tenant.getTenantID())
                .weight(poolMember.getWeight()).build();
        Member newMember = os.networking().loadbalancers().member().create(member);
        return getResponseType(newMember,tenant,env,"created");
    }

    @Override
    public GeminiPoolMember getPoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, String poolMemberID) {
        OSClient os = getOSClient(tenant);
        Member member = os.networking().loadbalancers().member().get(poolMemberID);
        return GeminiLBUtils.getAsGeminiPoolMember(member);
    }

    @Override
    public ProvisioningProviderResponseType updatePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        OSClient os = getOSClient(tenant);
        Member updatedMember = os.networking().loadbalancers().member().update(poolMember.getCloudID(),GeminiLBUtils.updatePoolMember(poolMember));
        return getResponseType(updatedMember,tenant,env,"updated");

    }

    @Override
    public ProvisioningProviderResponseType deletePoolMember(GeminiTenant tenant, GeminiEnvironment env, GeminiLoadBalancerPool pool, GeminiPoolMember poolMember) {
        OSClient os = getOSClient(tenant);
        ActionResponse actionResponse = os.networking().loadbalancers().member().delete(poolMember.getCloudID());
        return getResponseType(actionResponse,poolMember,tenant,env,"deleted");
    }

    public OSClient getOSClient(GeminiTenant tenant){
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        return os;
    }

    public ProvisioningProviderResponseType getResponseType(Object object,GeminiTenant tenant, GeminiEnvironment env
                                            ,String operation){
        if (object == null) {
            Logger.error("Failed to {} {}, failure in Cloud provider. Tenant: {} Environment: {} Network: {}",operation,object.getClass().getName(),
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(object, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        Logger.debug("Successfully {} {} - Tenant: {} Environment: {} Network: {}",operation,object.getClass().getName(),
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(object, ToStringStyle.MULTI_LINE_STYLE));
        return ProvisioningProviderResponseType.SUCCESS;
    }

    public ProvisioningProviderResponseType getResponseType(ActionResponse actionResponse, Object object,GeminiTenant tenant, GeminiEnvironment env
            ,String operation){
        if (!actionResponse.isSuccess()) {
            Logger.error("Failed to {} {}, failure in Cloud provider. Tenant: {} Environment: {} Network: {}",operation,object.getClass().getName(),
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(object, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        Logger.debug("Successfully {} {} - Tenant: {} Environment: {} Network: {}",operation,object.getClass().getName(),
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(object, ToStringStyle.MULTI_LINE_STYLE));
        return ProvisioningProviderResponseType.SUCCESS;
    }
}
