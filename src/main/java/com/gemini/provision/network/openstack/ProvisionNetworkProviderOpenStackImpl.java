/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.openstack;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.network.base.BaseProvisionNetworkProvider;
import java.util.ArrayList;
import java.util.Collections;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.api.OSClient;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openstack4j.api.Builders;
import org.openstack4j.common.Buildable;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.NetworkUpdate;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.network.builder.NetworkUpdateBuilder;
import org.openstack4j.openstack.networking.domain.NeutronNetwork;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class ProvisionNetworkProviderOpenStackImpl implements BaseProvisionNetworkProvider {

    @Override
    public String provisioningDesc() {
        return "OpenStack network provisioning service";
    }

    @Override
    public Integer createNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork newNetwork) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return -1;
        }

        //check to see if this network exists
        List<? extends Network> networks = os.networking().network().list();
        if (networks.stream()
                .filter(n -> n.getName().equals(newNetwork.getName()))
                .count() != 0) {
            Logger.error("Failed to create network - already exists. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return -2;
        }

        //create the network
        Network network = os.networking().network()
                .create(Builders.network()
                        .name(newNetwork.getName())
                        .tenantId(os.identity().tenants().getByName(tenant.getName()).getId())
                        .build());

        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (network == null) {
            Logger.error("Failed to create network Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return -3;
        }
        newNetwork.setCloudID(network.getId());
        Logger.debug("Successfully added network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
        return 0;
    }

    @Override
    public List<Integer> bulkCreateNetwork(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetwork> networks) {
        List<Integer> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        networks.stream().forEach(n -> retValues.set(0, createNetwork(tenant, env, n)));
        return retValues;
    }

    @Override
    public Integer deleteNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork delNetwork) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();

        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return -1;
        }

        //check to see if this network exists
        Network n = os.networking().network().get(delNetwork.getCloudID());
        if (n == null) {
            Logger.error("Failed to delete network - does not exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(delNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return -4;
        }

        os.networking().network().delete(delNetwork.getCloudID());
        Logger.debug("Successfully deleted network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(delNetwork, ToStringStyle.MULTI_LINE_STYLE));
        return 0;
    }

    @Override
    public Integer updateNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return -1;
        }

        // Get a network by ID
        Network network = os.networking().network().get(n.getCloudID());
        if (network == null) {
            Logger.error("Failed to update network - doesn't exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(n, ToStringStyle.MULTI_LINE_STYLE));
            return -4;
        }

        //update the network
        Network updatedNetwork = os.networking().network().update(n.getCloudID(), Builders.networkUpdate().name(n.getName()).build());
        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (updatedNetwork == null) {
            Logger.error("Failed to update network Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(n, ToStringStyle.MULTI_LINE_STYLE));
            return -3;
        }

        Logger.debug("Successfully updated the network. Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(n, ToStringStyle.MULTI_LINE_STYLE));
        return 0;
    }

    @Override
    public Integer createSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, GeminiSubnet newSubnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return -1;
        }

        //check to see if this subnet exists
        if (os.networking().subnet().list().stream().filter(n -> n.getName().equals(newSubnet.getName())).count() != 0) {
            Logger.error("Failed to create subnet - already exists. Tenant: {} Environment: {} subnet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newSubnet, ToStringStyle.MULTI_LINE_STYLE));
            return -2;        
        }
        
        //create the subnet
        Subnet subnet = os.networking().subnet().create(Builders.subnet()
                  .name(newSubnet.getName())
                  .networkId(parent.getCloudID())
                  .tenantId(os.identity().tenants().getByName(tenant.getName()).getId())
                  .addPool(newSubnet.getSubnetStart().getHostAddress(), newSubnet.getSubnetEnd().getHostAddress())
                  .ipVersion(IPVersionType.V4)
                  .cidr(newSubnet.getCidr())
                  .build());
        if (subnet == null) {
            Logger.error("Failed to create subnet Tenant: {} Environment: {} Subnet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newSubnet, ToStringStyle.MULTI_LINE_STYLE));
            return -3;
        }
        
        //copy the id to the domain object
        newSubnet.setCloudID(subnet.getId());
        Logger.debug("Successfully added network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(newSubnet, ToStringStyle.MULTI_LINE_STYLE));
        return 0;
    }

    @Override
    public List<Integer> bulkCreateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, List<GeminiSubnet> subnets) {
        List<Integer> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        subnets.stream().forEach(n -> retValues.set(0, createSubnet(tenant, env, parent, n)));
        return retValues;
    }

    @Override
    public Integer updateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet subnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return -1;
        }

        // Get a subnet by ID
        Subnet s = os.networking().subnet().get(subnet.getCloudID());
        if (s == null) {
            Logger.error("Failed to update subnet - doesn't exist. Tenant: {} Environment: {} Sbunet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return -4;
        }

        //update the subnet
        Subnet updatedSubnet = os.networking().subnet().update(s.toBuilder()
                .addPool(subnet.getSubnetStart().getHostAddress(), subnet.getSubnetEnd().getHostAddress())
                .cidr(subnet.getCidr())
                .name(subnet.getName())
                .networkId(subnet.getParent().getCloudID())
                .build());
        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (updatedSubnet == null) {
            Logger.error("Failed to update subnet Tenant: {} Environment: {} Subnet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return -3;
        }

        Logger.debug("Successfully updated the subnet. Tenant: {} Environment: {} Subnet: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
        return 0;
    }

    @Override
    public Integer deleteSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet subnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();

        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return -1;
        }

        // Get a subnet by ID
        Subnet s = os.networking().subnet().get(subnet.getCloudID());
        if (s == null) {
            Logger.error("Failed to delete network - does not exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return -4;
        }

        os.networking().subnet().delete(subnet.getCloudID());
        Logger.debug("Successfully deleted network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
        return 0;
    }
}
