/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.openstack;

import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiNetworkRouter;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.model.GeminiSubnetAllocationPool;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.provision.network.base.NetworkProvider;
import com.gemini.provision.network.base.NetworkProviderResponseType;
import com.google.common.net.InetAddresses;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.api.OSClient;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openstack4j.api.Builders;
import org.openstack4j.model.network.AttachInterfaceType;
import org.openstack4j.model.network.HostRoute;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.Subnet;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
@Singleton
public class NetworkProviderOpenStackImpl implements NetworkProvider {

    @Override
    public String provisioningDesc() {
        return "OpenStack network provisioning service - can be used for OpenStack, Rackspace and Mirantis";
    }

    @Override
    public List<GeminiNetwork> getExternalGateways(GeminiTenant tenant, GeminiEnvironment env) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
//                .domainName(tenant.getDomainName())
//                .tenantId(tenant.getTenantID())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get all the networks
        List<? extends Network> networks = os.networking().network().list();
        List<GeminiNetwork> gateways = new ArrayList();

        //map the list of network gateways and their subnets to gemini equivalents
        networks.stream().filter(n -> n.isRouterExternal()).forEach(n -> {
            GeminiNetwork gn = new GeminiNetwork();
            gn.setName(n.getName());
            gn.setCloudID(n.getId());
            gn.setNetworkType(n.getNetworkType().name());
            n.getNeutronSubnets().stream().forEach(s -> {
                GeminiSubnet gs = new GeminiSubnet();
                gs.setCloudID(s.getId());
                gs.setParent(gn);
                gs.setCidr(s.getCidr());
                s.getAllocationPools().stream().forEach(p -> {
                    GeminiSubnetAllocationPool gsap = new GeminiSubnetAllocationPool(InetAddresses.forString(p.getStart()),
                            InetAddresses.forString(p.getEnd()));
                    gsap.setParent(gs);
                    gs.addAllocationPool(gsap);
                });
            });
            gateways.add(gn);
        });
        return gateways;
    }

    @Override
    public List<GeminiNetwork> getNetworks(GeminiTenant tenant, GeminiEnvironment env) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get all the networks
        List<? extends Network> networks = os.networking().network().list();
        List<GeminiNetwork> gemNetworks = new ArrayList();

        //map the list of network gateways and their subnets to gemini equivalents
        networks.stream().forEach(n -> {
            GeminiNetwork gn = new GeminiNetwork();
            gn.setName(n.getName());
            gn.setCloudID(n.getId());
            gn.setNetworkType(n.getNetworkType().name());
            n.getNeutronSubnets().stream().forEach(s -> {
                GeminiSubnet gs = new GeminiSubnet();
                gs.setCloudID(s.getId());
                gs.setParent(gn);
                gs.setCidr(s.getCidr());
                s.getAllocationPools().stream().forEach(p -> {
                    GeminiSubnetAllocationPool gsap = new GeminiSubnetAllocationPool(InetAddresses.forString(p.getStart()),
                            InetAddresses.forString(p.getEnd()));
                    gsap.setParent(gs);
                    gs.addAllocationPool(gsap);
                });
            });
            gemNetworks.add(gn);
        });
        return gemNetworks;
    }

    @Override
    public NetworkProviderResponseType createNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork newNetwork) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //check to see if this network exists
        List<? extends Network> networks = os.networking().network().list();
        if (networks.stream().filter(n -> n.getName().equals(newNetwork.getName())).count() != 0) {
            Logger.error("Failed to create network - already exists. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_EXISTS;
        }

        //create the network
        Network network = os.networking().network().create(Builders.network()
                .name(newNetwork.getName())
                .tenantId(os.identity().tenants().getByName(tenant.getName()).getId())
                .build());

        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (network == null) {
            Logger.error("Failed to create network, failure in Cloud provider. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_FAILURE;
        }

        //copy the cloud ID to the Gemini object as it is required later
        newNetwork.setCloudID(network.getId());
        Logger.debug("Successfully added network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
        return NetworkProviderResponseType.SUCCESS;
    }

    @Override
    public List<NetworkProviderResponseType> bulkCreateNetwork(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetwork> networks) {
        List<NetworkProviderResponseType> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        networks.stream().forEach(n -> retValues.set(0, createNetwork(tenant, env, n)));
        return retValues;
    }

    @Override
    public NetworkProviderResponseType deleteNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork delNetwork) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();

        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //check to see if this network exists
        Network n = os.networking().network().get(delNetwork.getCloudID());
        if (n == null) {
            Logger.error("Failed to delete network - does not exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(delNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_NOT_FOUND;
        }

        os.networking().network().delete(delNetwork.getCloudID());
        Logger.debug("Successfully deleted network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(delNetwork, ToStringStyle.MULTI_LINE_STYLE));
        return NetworkProviderResponseType.SUCCESS;
    }

    @Override
    public NetworkProviderResponseType updateNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        // Get a network by ID
        Network network = os.networking().network().get(n.getCloudID());
        if (network == null) {
            Logger.error("Failed to update network - doesn't exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(n, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_NOT_FOUND;
        }

        //update the network
        Network updatedNetwork = os.networking().network().update(n.getCloudID(), Builders.networkUpdate().name(n.getName()).build());
        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (updatedNetwork == null) {
            Logger.error("Failed to update network, Cloud provider failure Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(n, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_FAILURE;
        }

        Logger.debug("Successfully updated the network. Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(n, ToStringStyle.MULTI_LINE_STYLE));
        return NetworkProviderResponseType.SUCCESS;
    }

    @Override
    public List<GeminiSubnet> getSubnets(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent) {
        //the call to getNetworks retrieves all subnet information. Nothing additional required here.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NetworkProviderResponseType createSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, GeminiSubnet newSubnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //check to see if this subnet exists
        if (os.networking().subnet().list().stream().filter(n -> n.getName().equals(newSubnet.getName())).count() != 0) {
            Logger.error("Failed to create subnet - already exists. Tenant: {} Environment: {} subnet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newSubnet, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_EXISTS;
        }

        //create the subnet
        Subnet subnet = os.networking().subnet().create(Builders.subnet()
                .name(newSubnet.getName())
                .networkId(parent.getCloudID())
                .tenantId(os.identity().tenants().getByName(tenant.getName()).getId())
                .ipVersion(IPVersionType.V4)
                .cidr(newSubnet.getCidr())
                .build());
        if (subnet == null) {
            Logger.error("Failed to create subnet, Cloud provider failure Tenant: {} Environment: {} Subnet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newSubnet, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_FAILURE;
        }

        //add the list of subnets (this will be an update the subnet just created)
        List<GeminiSubnetAllocationPool> pools = newSubnet.getAllocationPools();
        pools.stream().forEach(p -> {
            os.networking().subnet().update(subnet.toBuilder()
                    .addPool(p.getStart().getHostAddress(), p.getEnd().getHostAddress())
                    .build());
        });

        //copy the id to the domain object
        newSubnet.setCloudID(subnet.getId());
        Logger.debug("Successfully added network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(newSubnet, ToStringStyle.MULTI_LINE_STYLE));
        return NetworkProviderResponseType.SUCCESS;
    }

    @Override
    public List<NetworkProviderResponseType> bulkCreateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, List<GeminiSubnet> subnets) {
        List<NetworkProviderResponseType> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        subnets.stream().forEach(n -> retValues.set(0, createSubnet(tenant, env, parent, n)));
        return retValues;
    }

    @Override
    public NetworkProviderResponseType updateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet subnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        // Get a subnet by ID
        Subnet s = os.networking().subnet().get(subnet.getCloudID());
        if (s == null) {
            Logger.error("Failed to update subnet - doesn't exist. Tenant: {} Environment: {} Sbunet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_NOT_FOUND;
        }

        //update the subnet
        Subnet updatedSubnet = os.networking().subnet().update(s.toBuilder()
                //.addPool(subnet.getSubnetStart().getHostAddress(), subnet.getSubnetEnd().getHostAddress())
                .cidr(subnet.getCidr())
                .name(subnet.getName())
                .gateway(subnet.getGateway().getCloudID())
                .networkId(subnet.getParent().getCloudID())
                .build());

        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (updatedSubnet == null) {
            Logger.error("Failed to update subnet, Cloud provider failure. Tenant: {} Environment: {} Subnet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_FAILURE;
        }

        //add the list of subnets (this will be an update the subnet just created)
        List<GeminiSubnetAllocationPool> pools = subnet.getAllocationPools();
        pools.stream().forEach(p -> {
            os.networking().subnet()
                    .update(updatedSubnet.toBuilder()
                            .addPool(p.getStart().getHostAddress(), p.getEnd().getHostAddress())
                            .build());
        });

        Logger.debug("Successfully updated the subnet. Tenant: {} Environment: {} Subnet: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
        return NetworkProviderResponseType.SUCCESS;
    }

    @Override
    public NetworkProviderResponseType deleteSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet subnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();

        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        // Get a subnet by ID
        Subnet s = os.networking().subnet().get(subnet.getCloudID());
        if (s == null) {
            Logger.error("Failed to delete network - does not exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_NOT_FOUND;
        }

        if (os.networking().subnet().delete(subnet.getCloudID()).isSuccess()) {
            Logger.debug("Successfully deleted network - Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.SUCCESS;
        } else {
            Logger.error("Failed to delete network, cloud provider failure - Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_FAILURE;
        }
    }

    @Override
    public List<GeminiEnvironment> getEnvironments(GeminiTenant tenant) {
        //The environments loosely correlate to the Projects in Mirantis
        //TODO - need to research to see how rackspace and others handle their data.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * return all the route setup for the tenant
     */
    @Override
    public List<GeminiNetworkRouter> getAllRouters(GeminiTenant tenant) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get the list of routers from the cloud
        List<? extends Router> osRouters = os.networking().router().list();
        if (osRouters.isEmpty()) {
            Logger.debug("No routers found for Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //convert the Router to GeminiNetworkRouter 
        //TODO: change code to use Dozer Mapper
        List<GeminiNetworkRouter> routers = Collections.synchronizedList(new ArrayList());
        osRouters.stream().forEach(osRouter -> {
            GeminiNetworkRouter nRouter = new GeminiNetworkRouter();
            nRouter.setCloudID(osRouter.getId());
            nRouter.setName(osRouter.getName());
            String gID = osRouter.getExternalGatewayInfo().getNetworkId();
            if (!gID.isEmpty()) {
                //stream through envs, map to stream of GeminiNetwork objects, filter on 
                //the ID and then get the first object... note there will only be one
                //so we can use findOne or findAny
                GeminiNetwork gemGateway = tenant.getEnvironments()
                        .stream()
                        .map(GeminiEnvironment::getNetworks)
                        .flatMap(List::stream)
                        .filter(n -> n.getCloudID().equals(gID))
                        .findFirst()
                        .get();
                nRouter.setGateway(gemGateway);
            }

            //get the host routes
            List<? extends HostRoute> osHostRoutes = osRouter.getRoutes();
            osHostRoutes.stream().forEach(osHostRoute -> nRouter.addRoute(osHostRoute.getNexthop(),
                    osHostRoute.getDestination()));

            //get the interfaces attached to the router
            //OPEN STACK DOES NOT HAVE THIS FUNCTIONALITY - THIS IS RIDICULOUS!!!!!
            //WE HAVE TO CREATE IT EACH TIME
            //add it to the tenant
            routers.add(nRouter);
        });
        return routers;
    }

    /*
     * returns the routes for a given environment. 
     */
    @Override
    public List<GeminiNetworkRouter> getEnvRouters(GeminiTenant tenant, GeminiEnvironment env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NetworkProviderResponseType createRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter newRouter) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //see if this already exists
        List<? extends Router> existingRoutes = os.networking().router().list();
        if (existingRoutes.stream().anyMatch(r -> r.getName().equals(newRouter.getName()))) {
            Logger.error("Failed to add Router, already exists: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(newRouter, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_EXISTS;
        }

        //create the router
        Router createdRouter = os.networking().router().create(Builders.router()
                .name(newRouter.getName())
                .adminStateUp(true)
                .externalGateway(newRouter.getGateway().getCloudID())
                .build());
        if (createdRouter == null) {
            Logger.error("Failed to add Router, cloud provider failure: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(newRouter, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_FAILURE;
        }

        //now add the host routes
        newRouter.getRoutes().forEach((k, v) -> os.networking().router().update(createdRouter.toBuilder().route(k, v).build()));

        //now attach the interfaces
        newRouter.getInterfaces().forEach(s -> os.networking().router().attachInterface(createdRouter.getId(), AttachInterfaceType.SUBNET, s.getCloudID()));

        Logger.debug("Successfully added router - Tenant: {} Environment: {} Router: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(newRouter, ToStringStyle.MULTI_LINE_STYLE));
        return NetworkProviderResponseType.SUCCESS;
    }

    @Override
    public List<NetworkProviderResponseType> bulkCreateRouter(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetworkRouter> routes) {
        List<NetworkProviderResponseType> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        routes.stream().forEach(r -> retValues.set(0, createRouter(tenant, env, r)));
        return retValues;
    }

    @Override
    public NetworkProviderResponseType updateRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter routerToBeUpdated) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //see if this already exists
        Router existingRouter = os.networking().router().get(routerToBeUpdated.getCloudID());
        if (existingRouter == null) {
            Logger.error("Failed to update Router, it does not exist: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeUpdated, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_NOT_FOUND;
        }

        //first update the non-array/list items
        os.networking().router().update(existingRouter
                .toBuilder()
                .externalGateway(routerToBeUpdated.getGateway().getCloudID())
                .name(routerToBeUpdated.getName())
                .build());

        //now add the host routes
        routerToBeUpdated.getRoutes().forEach((k, v) -> os.networking().router().update(existingRouter.toBuilder().route(k, v).build()));

        //now attach the interfaces
        routerToBeUpdated.getInterfaces().forEach(s -> os.networking().router().attachInterface(existingRouter.getId(), AttachInterfaceType.SUBNET, s.getCloudID()));

        Logger.debug("Successfully updated router - Tenant: {} Environment: {} Router: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(routerToBeUpdated, ToStringStyle.MULTI_LINE_STYLE));
        return NetworkProviderResponseType.SUCCESS;
    }

    @Override
    public NetworkProviderResponseType deleteRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter routerToBeDeleted) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builderV3()
                .endpoint(tenant.getEndPoint())
                .credentials(tenant.getAdminUserName(), tenant.getAdminPassword())
                .domainName(tenant.getDomainName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //see if this already exists
        Router existingRouter = os.networking().router().get(routerToBeDeleted.getCloudID());
        if (existingRouter == null) {
            Logger.error("Failed to delete Router, it does not exist: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeDeleted, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.OBJECT_NOT_FOUND;
        }

        //now delete the router
        if (!os.networking().router().delete(existingRouter.getId()).isSuccess()) {
            Logger.error("Failed to delete Router, failure in Cloud Provider: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeDeleted, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.CLOUD_FAILURE;
        } else {
            Logger.debug("Successfully deleted Router: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeDeleted, ToStringStyle.MULTI_LINE_STYLE));
            return NetworkProviderResponseType.SUCCESS;
        }
    }
}