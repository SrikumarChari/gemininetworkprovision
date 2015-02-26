/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.openstack;

import com.gemini.domain.common.IPAddressType;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiNetworkRouter;
import com.gemini.domain.model.GeminiSubnet;
import com.gemini.domain.model.GeminiSubnetAllocationPool;
import com.gemini.domain.model.GeminiTenant;
import com.gemini.provision.network.base.NetworkProvider;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.google.common.net.InetAddresses;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.api.OSClient;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openstack4j.api.Builders;
import org.openstack4j.api.exceptions.ClientResponseException;
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
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        List<? extends Network> networks = os.networking().network().list();
        List<GeminiNetwork> gateways = new ArrayList();

        //map the list of network gateways and their subnets to gemini equivalents
        networks.stream().filter(osn -> osn.isRouterExternal()).forEach(osn -> {
            GeminiNetwork gn = null;
            try {
                gn = env.getOrphanNetworks().stream()
                        .filter(n -> n.getCloudID().equals(osn.getId())) //filter on the OpenStack network object cloud id
                        .findFirst().get();
            } catch (NoSuchElementException ex) {
                //not an error, just log the event. the network object will be created below
                Logger.debug("Gateway {} not mapped in Gemini models, creating one...", osn.getName());
            }

            if (gn == null) {
                //the network has not created in gemini data model - create and it's subnets, etc
                GeminiNetwork newGn = new GeminiNetwork();
                newGn.setName(osn.getName());
                newGn.setCloudID(osn.getId());
                if (osn.getNetworkType() != null) {
                    newGn.setNetworkType(osn.getNetworkType().name());
                }
                //add the subnets to the new network. List all subnets and filter by the parent network id
                List<? extends Subnet> osSubnets = os.networking().subnet().list();
                osSubnets.stream().filter(osSubnet -> osSubnet != null)
                        .filter(osSubnet -> osSubnet.getNetworkId().equals(osn.getId()))
                        .forEach(osSubnet -> {
                            GeminiSubnet gs = new GeminiSubnet();
                            gs.setCloudID(osSubnet.getId());
                            gs.setParent(newGn);
                            gs.setCidr(osSubnet.getCidr());
                            osSubnet.getAllocationPools().stream().forEach(ap -> {
                                GeminiSubnetAllocationPool geminiAp = new GeminiSubnetAllocationPool(InetAddresses.forString(ap.getStart()),
                                        InetAddresses.forString(ap.getEnd()));
                                geminiAp.setParent(gs);
                                gs.addAllocationPool(geminiAp);
                            });
                            gs.setParent(newGn);
                            newGn.addSubnet(gs);
                        });
                gn = newGn;
                env.addGateway(newGn);
            }
            //TODO: When gn != null, do we need to check if subnet objects are correctly captured
            //      in the Gemini data model
            gateways.add(gn);
        });
        return gateways;
    }

    @Override
    public List<GeminiNetwork> getNetworks(GeminiTenant tenant, GeminiEnvironment env) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get all the subnets
        List<? extends Network> networks = os.networking().network().list();
        List<GeminiNetwork> gemNetworks = new ArrayList();

        //map the list of network gateways and their subnets to gemini equivalents
        networks.stream().forEach(osn -> {
            GeminiNetwork gn = null;
            try {
                //first see if this network belongs to an application
                gn = env.getApplications().stream()
                        .map(GeminiApplication::getNetworks).flatMap(List::stream) //invoke the getNetworks on each application and convert the result into one large stream
                        .filter(n -> n.getCloudID().equals(osn.getId())) //filter on the OpenStack network object cloud id
                        .findFirst().get();
                gn.setCloudID(osn.getId()); //in the event the ID has not been updated
            } catch (NoSuchElementException | NullPointerException e) {
                //not part of an application, see if it is in the orphan list
                try {
                    gn = env.getOrphanNetworks().stream()
                            .filter(n -> n.getCloudID().equals(osn.getId())) //filter on the OpenStack network object cloud id
                            .findFirst().get();
                } catch (NoSuchElementException | NullPointerException ex) {
                    //not an error, just log the event. the network object will be created below
                    Logger.debug("Network {} not mapped in Gemini models, creating one...", osn.getName());
                }
            }

            GeminiNetwork newGn = null;
            if (gn == null) {
                newGn = new GeminiNetwork();
                newGn.setName(osn.getName());
                newGn.setCloudID(osn.getId());
                if (osn.getNetworkType() != null) {
                    newGn.setNetworkType(osn.getNetworkType().name());
                }
                //we don't which application this network belongs to... so add it to orphan networks list
                env.addOrphanNetwork(newGn);
                gn = newGn;
            }

            //add the subnets to the new network. For some reason Network::getNeutronSubnets
            //always returned null. List all subnets and filter by the parent network id
            List<? extends Subnet> osSubnets = os.networking().subnet().list();
            if (osSubnets != null && !osSubnets.isEmpty() && osSubnets.stream().anyMatch(osSubnet -> osSubnet.getNetworkId().equals(osn.getId()))) {
                GeminiNetwork tmpParent = newGn == null ? gn : newGn;
                osSubnets.stream().filter(osSubnet -> osSubnet.getNetworkId().equals(osn.getId()))
                        .forEach(osSubnet -> {
                            if (tmpParent.getSubnets().stream().noneMatch(s -> s.getName().equals(osSubnet.getName()))) {
                                //this subnet is not mapped on the Gemini side
                                GeminiSubnet geminiSubnet = new GeminiSubnet();
                                geminiSubnet.setCloudID(osSubnet.getId());
                                geminiSubnet.setParent(tmpParent);
                                geminiSubnet.setCidr(osSubnet.getCidr());
                                geminiSubnet.setName(osSubnet.getName());
                                geminiSubnet.setEnableDHCP(osSubnet.isDHCPEnabled());
                                geminiSubnet.setParent(tmpParent);
                                geminiSubnet.setNetworkType(osSubnet.getIpVersion() == IPVersionType.V4 ? IPAddressType.IPv4 : IPAddressType.IPv6);
//                                try {
//                                    geminiSubnet.setGateway(env.getGateways().stream().filter(g -> g.getName().equals(osSubnet.getGateway())).findAny().get());
//                                } catch (NoSuchElementException | NullPointerException ex) {
//                                    Logger.error("Subnet {} has a gateway that isn't mappeed to a an object in Gemini. Gateway {}",
//                                            geminiSubnet.getName(), osSubnet.getGateway());
//                                    geminiSubnet.setGateway(null);
//                                }
                                osSubnet.getAllocationPools().stream().forEach(ap -> {
                                    GeminiSubnetAllocationPool geminiAp = new GeminiSubnetAllocationPool(InetAddresses.forString(ap.getStart()),
                                            InetAddresses.forString(ap.getEnd()));
                                    geminiAp.setParent(geminiSubnet);
                                    geminiSubnet.addAllocationPool(geminiAp);
                                });
                                tmpParent.addSubnet(geminiSubnet);
                            }
                        });
            }
            gemNetworks.add(gn);
        });
        return gemNetworks;
    }

    @Override
    public ProvisioningProviderResponseType createNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork newNetwork) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //check to see if this network exists
        List<? extends Network> networks = os.networking().network().list();
        if (networks.stream().anyMatch(n -> n.getName().equals(newNetwork.getName()))) {
            Logger.error("Failed to create network - already exists. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.OBJECT_EXISTS;
        }

        //create the network
        Network network;
        try {
            network = os.networking().network().create(Builders.network()
                    .tenantId(tenant.getTenantID())
                    .name(newNetwork.getName())
                    .build());
        } catch (ClientResponseException ex) {
            Logger.error("Cloud exception, failed to create network: status code {} tenant: {}, env: {} network {}",
                    ex.getStatusCode(), tenant.getName(), env.getName(), newNetwork.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (network == null) {
            Logger.error("Failed to create network, failure in Cloud provider. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        //copy the cloud ID to the Gemini object as it is required later
        newNetwork.setCloudID(network.getId());

        //now create subnets in network
        newNetwork.getSubnets().stream().forEach(gs -> {
            //create the subnet
            Subnet subnet = null;
            try {
                subnet = os.networking().subnet().create(Builders.subnet()
                        .tenantId(tenant.getTenantID())
                        .gateway(InetAddresses.toAddrString(gs.getGateway()))
                        .enableDHCP(gs.isEnableDHCP())
                        .ipVersion(gs.getNetworkType() == IPAddressType.IPv6 ? IPVersionType.V6 : IPVersionType.V4)
                        .name(gs.getName())
                        .networkId(newNetwork.getCloudID())
                        .cidr(gs.getCidr())
                        .build());
                gs.setGateway(InetAddresses.forString(subnet.getGateway()));
            } catch (ClientResponseException ex) {
                Logger.error("Cloud exception, failed to create subnet. status code {} tenant: {} env: {} network: {} subnet: {}",
                        ex.getStatusCode(), tenant.getName(), env.getName(), newNetwork.getName(), gs.getName());
            }

            if (subnet == null) {
                Logger.error("Cloud failure, failed to create subnet. tenant: {} env: {} network: {} subnet: {}",
                        tenant.getName(), env.getName(), newNetwork.getName(), gs.getName());
            }
        });

        Logger.debug("Successfully created network and it's subnets - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(newNetwork, ToStringStyle.MULTI_LINE_STYLE));
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public List<ProvisioningProviderResponseType> bulkCreateNetwork(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetwork> networks) {
        List<ProvisioningProviderResponseType> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        networks.stream().forEach(n -> createNetwork(tenant, env, n));
        return retValues;
    }

    @Override
    public ProvisioningProviderResponseType deleteNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork delNetwork) {
        //authenticate the session with the OpenStack installation
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //check to see if this network exists
        Network n;
        try {
            n = os.networking().network().get(delNetwork.getCloudID());
        } catch (NullPointerException | ClientResponseException ex) {
            Logger.error("Failed to delete network - does not exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(), delNetwork.getName());
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        try {
            os.networking().network().delete(delNetwork.getCloudID());
        } catch (ClientResponseException ex) {
            Logger.error("Cloud exception, could not delete network. status code {} Tenant: {} Environment: {} Network: {}",
                    ex.getStatusCode(), tenant.getName(), env.getName(), delNetwork.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        Logger.debug("Successfully deleted network - Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(), delNetwork.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public ProvisioningProviderResponseType updateNetwork(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork n) {
        //authenticate the session with the OpenStack installation
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        // Get a network by ID
        Network network;
        try {
            network = os.networking().network().get(n.getCloudID());
        } catch (NullPointerException ex) {
            Logger.error("Failed to update network - does not exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(), n.getName());
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //update the network
        Network updatedNetwork;
        try {
            updatedNetwork = os.networking().network().update(n.getCloudID(), Builders.networkUpdate().name(n.getName()).build());
        } catch (ClientResponseException ex) {
            Logger.error("Cloud exception, could not update network. status code {} Tenant: {} Environment: {} Network: {}",
                    ex.getStatusCode(), tenant.getName(), env.getName(), n.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (updatedNetwork == null) {
            Logger.error("Failed to update network, Cloud provider failure Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(), n.getName());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        Logger.debug("Successfully updated the network. Tenant: {} Environment: {} Network: {}",
                tenant.getName(), env.getName(), n.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    /**
     * getAllSubnets. Lists all the subnets in the cloud - function may not have
     * practical use in OpenStack. Use getSubnets and provide a network to get
     * subnets specific to a network.
     *
     * @param tenant - the tenant
     * @param env - the environment with the subnets
     * @return
     */
    @Override
    public List<GeminiSubnet> getAllSubnets(GeminiTenant tenant, GeminiEnvironment env) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get all the subnets
        List<? extends Subnet> subnets = os.networking().subnet().list();
        if (subnets.isEmpty()) {
            return null;
        }
        List<GeminiSubnet> gemSubnets = new ArrayList();

        //map the list of network gateways and their subnets to gemini equivalents
        subnets.stream().forEach(s -> {
            GeminiSubnet gn = new GeminiSubnet();
            //the basic elements
            gn.setName(s.getName());
            gn.setCloudID(s.getId());
            gn.setCidr(s.getCidr());
//            try {
//                gn.setGateway(env.getGateways().stream().filter(g -> g.getName().equals(s.getGateway())).findAny().get());
//            } catch (NoSuchElementException ex) {
//                Logger.error("Subnet {} has a gateway that isn't mappeed to a an object in Gemini. Gateway {}",
//                        gn.getName(), s.getGateway());
//            }
            try {
                //match the parent network to one in the application
                gn.setParent(env.getApplications().stream()
                        .map(GeminiApplication::getNetworks)
                        .flatMap(List::stream)
                        .filter(g -> g.getCloudID().equals(s.getNetworkId()))
                        .findAny().get());
            } catch (NoSuchElementException | NullPointerException  ex) {
                try {
                    //could not find a parent network in the applications, now look in the orphaned networks
                    gn.setParent(env.getOrphanNetworks().stream()
                            .filter(g -> g.getCloudID().equals(s.getNetworkId()))
                            .findAny().get());
                } catch (NoSuchElementException | NullPointerException  e) {
                    gn.setParent(null); //VERY BAD SITUATION - GEMINI MODEL IS COMPLETELY OUT OF SYNC
                    Logger.error("Subnet {} has a network that isn't in Applications or orphaned networks. subnet {} parent network {}",
                            gn.getName(), s.getNetworkId());
                }
            }
            gn.setEnableDHCP(s.isDHCPEnabled());
            gn.setNetworkType(s.getIpVersion() == IPVersionType.V6 ? IPAddressType.IPv6 : IPAddressType.IPv4);
            //s.getHostRoutes();
            s.getAllocationPools().stream().forEach(ap -> {
                GeminiSubnetAllocationPool gsap = new GeminiSubnetAllocationPool(InetAddresses.forString(ap.getStart()), InetAddresses.forString(ap.getEnd()));
                gn.addAllocationPool(gsap);
            });
            gemSubnets.add(gn);
        });
        return gemSubnets;
    }

    /**
     * getSubnets. Find all subnets related to a parent network. For each subnet
     * found it create an equivalent gemini object and adds it to the parent
     * network
     *
     * @param tenant - contains the environment, provides the auth parameters
     * @param env - contains the parent network whose subnets needs to be
     * retrieved
     * @param parent - the parent network whose subnets need to be retrieved
     * @return List<GeminiSubnet> list of the subnets found or null if there was
     * an error
     */
    @Override
    public List<GeminiSubnet> getSubnets(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //get all the subnets
        List<? extends Subnet> subnets = os.networking().subnet().list();
        if (subnets == null || subnets.isEmpty()) {
            Logger.info("No subnets for tenant: {} env: {} network {}", tenant.getName(), env.getName(), parent.getName());
            return null;
        }
        List<GeminiSubnet> gemSubnets = new ArrayList();

        //map the list of network gateways and their subnets to gemini equivalents
        subnets.stream().filter(s -> s != null).filter(s -> s.getNetworkId().equals(parent.getCloudID())).forEach(s -> {
            GeminiSubnet gn = new GeminiSubnet();
            gn.setName(s.getName());
            gn.setCloudID(s.getId());
            gn.setCidr(s.getCidr());
//            try {
//                gn.setGateway(env.getGateways().stream().filter(g -> g.getName().equals(s.getGateway())).findAny().get());
//            } catch (NoSuchElementException ex) {
//                Logger.error("Subnet {} has a gateway that isn't mappeed to a an object in Gemini. Gateway {}",
//                        gn.getName(), s.getGateway());
//            }
            gn.setParent(parent);
            gn.setEnableDHCP(s.isDHCPEnabled());
            gn.setNetworkType(s.getIpVersion() == IPVersionType.V4 ? IPAddressType.IPv4 : IPAddressType.IPv6);
            s.getAllocationPools().stream().forEach(ap -> {
                GeminiSubnetAllocationPool gsap = new GeminiSubnetAllocationPool(InetAddresses.forString(ap.getStart()), InetAddresses.forString(ap.getEnd()));
                gn.addAllocationPool(gsap);
            });
            gemSubnets.add(gn);
            gn.setParent(parent);
            parent.addSubnet(gn);
        });
        return gemSubnets;
    }

    @Override
    public ProvisioningProviderResponseType createSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, GeminiSubnet newSubnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        //check to see if the parenet network exists
        Network osParent;
        try {
            osParent = os.networking().network().list().stream().filter(osn -> osn.getName().equals(parent.getName())).findFirst().get();
            parent.setCloudID(osParent.getId()); //just in case...
        } catch (NoSuchElementException ex) {
            Logger.error("Failed to create subnet - parent network does not exist. Tenant: {} Environment: {} network: {} subnet: {}",
                    tenant.getName(), env.getName(), parent.getName(), newSubnet.getName());
            return ProvisioningProviderResponseType.CLOUD_NO_PARENT;
        }

        //check to see if this subnet exists
        if (os.networking().subnet().list().stream().anyMatch(n -> n.getName().equals(newSubnet.getName()))) {
            Logger.error("Failed to create subnet - already exists. Tenant: {} Environment: {} network: {} subnet: {}",
                    tenant.getName(), env.getName(), parent.getName(), newSubnet.getName());
            return ProvisioningProviderResponseType.OBJECT_EXISTS;
        }

        //create the subnet
        Subnet subnet;
        try {
            subnet = os.networking().subnet().create(Builders.subnet()
                    .tenantId(tenant.getTenantID())
                    .gateway(InetAddresses.toAddrString(newSubnet.getGateway()))
                    .enableDHCP(newSubnet.isEnableDHCP())
                    .ipVersion(newSubnet.getNetworkType() == IPAddressType.IPv6 ? IPVersionType.V6 : IPVersionType.V4)
                    .name(newSubnet.getName())
                    .networkId(parent.getCloudID())
                    .cidr(newSubnet.getCidr())
                    .build());
            newSubnet.setGateway(InetAddresses.forString(subnet.getGateway()));
        } catch (ClientResponseException ex) {
            Logger.error("Cloud exception: failed to create subnet. status code {} tenant: {} env: {} parent network: {} subnet {} ",
                    ex.getStatusCode(), tenant.getName(), env.getName(), parent.getName(), newSubnet.getName());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        //add the list of subnets (this will be an update the subnet just created)
        List<GeminiSubnetAllocationPool> pools = newSubnet.getAllocationPools();
        pools.stream().forEach(p -> {
            if (os.networking().subnet().update(subnet.toBuilder()
                    .addPool(p.getStart().getHostAddress(), p.getEnd().getHostAddress())
                    .build()) == null) {
                Logger.error("Failed to create subnet allocation pool, Tenant: {} Environment: {} Parent Network: {} Subnet: {} Allocation Pool {} {}",
                        tenant.getName(), env.getName(), parent.getName(), newSubnet.getName(), p.getStart().getHostAddress(), p.getEnd().getHostAddress());
            }
        });

        //copy the id to the domain object
        newSubnet.setCloudID(subnet.getId());
        Logger.debug("Successfully added network - tenant: {} env: {} parent network: {} subnet {} ",
                tenant.getName(), env.getName(), parent.getName(), newSubnet.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public List<ProvisioningProviderResponseType> bulkCreateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiNetwork parent, List<GeminiSubnet> subnets) {
        List<ProvisioningProviderResponseType> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        subnets.stream().forEach(n -> retValues.set(0, createSubnet(tenant, env, parent, n)));
        return retValues;
    }

    @Override
    public ProvisioningProviderResponseType updateSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet subnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        // Get a subnet by ID
        Subnet s = os.networking().subnet().get(subnet.getCloudID());
        if (s == null) {
            Logger.error("Failed to update subnet - doesn't exist. Tenant: {} Environment: {} Sbunet: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //update the subnet
        Subnet updatedSubnet;

        try {
            updatedSubnet = os.networking().subnet().update(s.toBuilder()
                    .tenantId(tenant.getTenantID())
                    .cidr(subnet.getCidr())
                    .name(subnet.getName())
                    .gateway(InetAddresses.toAddrString(subnet.getGateway()))
                    .networkId(subnet.getParent().getCloudID())
                    .enableDHCP(subnet.isEnableDHCP())
                    .ipVersion(subnet.getNetworkType() == IPAddressType.IPv6? IPVersionType.V6 : IPVersionType.V4)
                    .build());
            subnet.setGateway(InetAddresses.forString(updatedSubnet.getGateway()));
        } catch (ClientResponseException ex) {
            Logger.error("Cloud exception: status code {}", ex.getStatusCode());
            return ProvisioningProviderResponseType.CLOUD_EXCEPTION;
        }

        //TODO: Need to get detailed error codes for the call above. Research the StatusCode class
        if (updatedSubnet == null) {
            Logger.error("Failed to update subnet, Cloud provider failure. Tenant: {} Environment: {} Parent Network: {} Subnet: {} ",
                    tenant.getName(), env.getName(), subnet.getParent().getName(), subnet.getName());
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        //add the list of subnets (this will be an update the subnet just created)
        List<GeminiSubnetAllocationPool> pools = subnet.getAllocationPools();
        pools.stream().forEach(p -> {
            if (os.networking().subnet()
                    .update(updatedSubnet.toBuilder()
                            .addPool(p.getStart().getHostAddress(), p.getEnd().getHostAddress())
                            .build()) == null) {
                Logger.error("Failed to create subnet allocation pool, Tenant: {} Environment: {} Parent Network: {} Subnet: {} Allocation Pool {} {}",
                        tenant.getName(), env.getName(), subnet.getParent().getName(), subnet.getName(), p.getStart().getHostAddress(), p.getEnd().getHostAddress());

            }
        });

        Logger.debug("Successfully updated the subnet. Tenant: {} Environment: {} Parent Network: {} Subnet: {}",
                tenant.getName(), env.getName(), subnet.getParent().getName(), subnet.getName());
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public ProvisioningProviderResponseType deleteSubnet(GeminiTenant tenant, GeminiEnvironment env, GeminiSubnet subnet) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        // Get a subnet by ID
        Subnet s = os.networking().subnet().get(subnet.getCloudID());
        if (s == null) {
            Logger.error("Failed to delete network - does not exist. Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        if (os.networking().subnet().delete(subnet.getCloudID()).isSuccess()) {
            Logger.debug("Successfully deleted network - Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.SUCCESS;
        } else {
            Logger.error("Failed to delete network, cloud provider failure - Tenant: {} Environment: {} Network: {}",
                    tenant.getName(), env.getName(),
                    ToStringBuilder.reflectionToString(subnet, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
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
    public List<GeminiNetworkRouter> getEnvRouters(GeminiTenant tenant, GeminiEnvironment env) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}", ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
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
                        .map(GeminiEnvironment::getApplications)
                        .flatMap(List::stream)
                        .map(GeminiApplication::getNetworks)
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

    @Override
    public ProvisioningProviderResponseType createRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter newRouter) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //see if this already exists
        List<? extends Router> existingRoutes = os.networking().router().list();
        if (existingRoutes.stream().anyMatch(r -> r.getName().equals(newRouter.getName()))) {
            Logger.error("Failed to add Router, already exists: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(newRouter, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.OBJECT_EXISTS;
        }

        //create the router
        Router createdRouter = os.networking().router().create(Builders.router()
                .tenantId(tenant.getTenantID())
                .name(newRouter.getName())
                .adminStateUp(true)
                .externalGateway(newRouter.getGateway().getCloudID())
                .build());
        if (createdRouter == null) {
            Logger.error("Failed to add Router, cloud provider failure: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(newRouter, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        }

        //now add the host routes
        newRouter.getRoutes().forEach((k, v) -> os.networking().router().update(createdRouter.toBuilder().route(k, v).build()));

        //now attach the interfaces
        newRouter.getInterfaces().forEach(s -> os.networking().router().attachInterface(createdRouter.getId(), AttachInterfaceType.SUBNET, s.getCloudID()));

        Logger.debug("Successfully added router - Tenant: {} Environment: {} Router: {}",
                tenant.getName(), env.getName(),
                ToStringBuilder.reflectionToString(newRouter, ToStringStyle.MULTI_LINE_STYLE));
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public List<ProvisioningProviderResponseType> bulkCreateRouter(GeminiTenant tenant, GeminiEnvironment env, List<GeminiNetworkRouter> routes) {
        List<ProvisioningProviderResponseType> retValues = Collections.synchronizedList(new ArrayList());
        //TODO: Only the first element is set ... NEED to research whether it is possible to get the current position from the stream
        routes.stream().forEach(r -> retValues.set(0, createRouter(tenant, env, r)));
        return retValues;
    }

    @Override
    public ProvisioningProviderResponseType updateRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter routerToBeUpdated) {
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //see if this already exists
        Router existingRouter = os.networking().router().get(routerToBeUpdated.getCloudID());
        if (existingRouter == null) {
            Logger.error("Failed to update Router, it does not exist: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeUpdated, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //first update the non-array/list items
        os.networking().router().update(existingRouter
                .toBuilder()
                .name(routerToBeUpdated.getName())
                //.adminStateUp(routerToBeUpdated.)
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
        return ProvisioningProviderResponseType.SUCCESS;
    }

    @Override
    public ProvisioningProviderResponseType deleteRouter(GeminiTenant tenant, GeminiEnvironment env, GeminiNetworkRouter routerToBeDeleted) {
        //authenticate the session with the OpenStack installation
        //authenticate the session with the OpenStack installation
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        if (os == null) {
            Logger.error("Failed to authenticate Tenant: {}",
                    ToStringBuilder.reflectionToString(tenant, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_AUTH_FAILURE;
        }

        //see if this already exists
        Router existingRouter = os.networking().router().get(routerToBeDeleted.getCloudID());
        if (existingRouter == null) {
            Logger.error("Failed to delete Router, it does not exist: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeDeleted, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.OBJECT_NOT_FOUND;
        }

        //now delete the router
        if (!os.networking().router().delete(existingRouter.getId()).isSuccess()) {
            Logger.error("Failed to delete Router, failure in Cloud Provider: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeDeleted, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.CLOUD_FAILURE;
        } else {
            Logger.debug("Successfully deleted Router: Tenant: {} Environment: {}, Router: {}",
                    tenant.getName(), env.getName(), ToStringBuilder.reflectionToString(routerToBeDeleted, ToStringStyle.MULTI_LINE_STYLE));
            return ProvisioningProviderResponseType.SUCCESS;
        }
    }

    @Override
    public GeminiSubnet getSubnet(GeminiTenant tenant, GeminiEnvironment env,String subnetId){
        OSClient os = getOSClient(tenant, env);
        Subnet subnet = os.networking().subnet().get(subnetId);
        GeminiSubnet gn = new GeminiSubnet();
        gn.setName(subnet.getName());
        gn.setCloudID(subnet.getId());
        gn.setCidr(subnet.getCidr());
        gn.setEnableDHCP(subnet.isDHCPEnabled());
        return gn;
    }

    public OSClient getOSClient(GeminiTenant tenant, GeminiEnvironment env){
        OSClient os = OSFactory.builder()
                .endpoint(env.getEndPoint())
                .credentials(env.getAdminUserName(), env.getAdminPassword())
                .tenantName(tenant.getName())
                .authenticate();
        return os;
    }
}
