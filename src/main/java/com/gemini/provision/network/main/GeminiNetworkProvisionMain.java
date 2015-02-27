package com.gemini.provision.network.main;

import com.gemini.domain.dto.GeminiTenantDTO;
import com.gemini.domain.dto.deserialize.GeminiTenantDeserializer;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiSecurityGroup;
import com.gemini.domain.model.GeminiTenant;
import com.gemini.mapper.GeminiMapper;
import com.gemini.mapper.GeminiMapperModule;
import com.gemini.properties.GeminiProperties;
import com.gemini.properties.GeminiPropertiesModule;
import com.gemini.provision.base.ProvisioningProviderResponseType;
import com.gemini.provision.loadbalancer.base.LoadBalancerProvisioningService;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.network.base.NetworkProvisioningService;
import com.gemini.provision.security.base.SecurityProviderModule;
import com.gemini.provision.security.base.SecurityProvisioningService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author schari
 */
public class GeminiNetworkProvisionMain {

    static NetworkProvisioningService netProvisioningService;
    static SecurityProvisioningService secProvisioningService;
    static LoadBalancerProvisioningService lbProvisioningService;

    static GeminiMapper mapper;

    public static void main(String[] args) {

        //activate logging level if it is DEBUG, default is INFO so no need for any
        //action if it is otherwise
        Injector propInjector = Guice.createInjector(new GeminiPropertiesModule());
        GeminiProperties properties = propInjector.getInstance(GeminiProperties.class);
        if (properties.getProperties().getProperty("LOGGING_LEVEL").equals("DEBUG")) {
            Configurator.defaultConfig().level(Level.DEBUG).activate();
        }

        //inject the mapper as it will be used in all the threads
        Injector mapperInjector = Guice.createInjector(new GeminiMapperModule());
        mapper = mapperInjector.getInstance(GeminiMapper.class);

        //start the topic 
        //the networking message recevier
        Thread networkingThread = new Thread(() -> {
            //setup the message receiver
            final Connection connection;
            final Channel channel;
            final QueueingConsumer consumer;

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getProperties().getProperty("MESSAGING_HOST"));
            String queueName = null;
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.exchangeDeclare(properties.getProperties().getProperty("EXCHANGE_NAME"), "topic");
                queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, properties.getProperties().getProperty("EXCHANGE_NAME"),
                        properties.getProperties().getProperty("NETWORK_TOPIC"));
//                channel.basicQos(Integer.parseUnsignedInt(properties.getProperties().getProperty("PREFETCH_COUNT")));
                consumer = new QueueingConsumer(channel);
                channel.basicConsume(queueName, true, consumer);
            } catch (IOException | NullPointerException | NumberFormatException ex) {
                Logger.error("Fatal Error: could not connect to messaging system. Exception: {}", ex);
                return;
            }

            QueueingConsumer.Delivery delivery = null;
            while (true) {
                try {
                    delivery = consumer.nextDelivery();
                } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                    Logger.error("Could not get message from queue. Exception: {}", ex);

                    //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                    continue;
                }

                String routingKey = delivery.getEnvelope().getRoutingKey();
                String jsonBody = new String(delivery.getBody());

                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE IF THERE IS A FAILURE
                if (routingKey.equals(properties.getProperties().getProperty("NETWORK_TASK_CREATE"))) {
                    createNetwork(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("NETWORK_TASK_UPDATE"))) {
                    updateNetwork(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("NETWORK_TASK_DELETE"))) {
                    deleteNetwork(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_CREATE"))) {
                    createSubnet(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_UPDATE"))) {
                    updateSubnet(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_DELETE"))) {
                    deleteSubnet(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("ROUTER_TASK_CREATE"))) {
                    createRouter(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("ROUTER_TASK_UPDATE"))) {
                    updateRouter(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("ROUTER_TASK_DELETE"))) {
                    deleteRouter(jsonBody);
                }

                try {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (IOException ex) {
                    Logger.error("Could not ack message. Exception: {}", ex);
                }
            }
        });
        networkingThread.start();

        //the load balancer 
        Thread lbThread = new Thread(() -> {
        });

        lbThread.start();

        //the security thread
        Thread securityThread = new Thread(() -> {
            //setup the message receiver
            final Connection connection;
            final Channel channel;
            final QueueingConsumer consumer;

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getProperties().getProperty("MESSAGING_HOST"));
            String queueName = null;
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.exchangeDeclare(properties.getProperties().getProperty("EXCHANGE_NAME"), "topic");
                queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, properties.getProperties().getProperty("EXCHANGE_NAME"),
                        properties.getProperties().getProperty("SECURITY_TOPIC"));
//                channel.basicQos(Integer.parseUnsignedInt(properties.getProperties().getProperty("PREFETCH_COUNT")));
                consumer = new QueueingConsumer(channel);
                channel.basicConsume(queueName, true, consumer);
            } catch (IOException | NullPointerException | NumberFormatException ex) {
                Logger.error("Fatal Error: could not connect to messaging system. Exception: {}", ex);
                return;
            }

            QueueingConsumer.Delivery delivery = null;
            while (true) {
                try {
                    delivery = consumer.nextDelivery();
                } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                    Logger.error("Could not get message from queue. Exception: {}", ex);
                    continue;
                }

                String routingKey = delivery.getEnvelope().getRoutingKey();
                String jsonBody = new String(delivery.getBody());

                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE IF THERE IS A FAILURE
                if (routingKey.equals(properties.getProperties().getProperty("SECURITY_TASK_SG_CREATE"))) {
                    createSecurityGroup(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SECURITY_TASK_SG_UPDATE"))) {
                    updateSecurityGroup(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SECURITY_TASK_SG_DELETE"))) {
                    deleteSecurityGroup(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SECURITY_TASK_SG_RULE_CREATE"))) {
                    createSecurityGroupRule(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SECURITY_TASK_SG_RULE_UPDATE"))) {
                    updateSecurityGroupRule(jsonBody);
                } else if (routingKey.equals(properties.getProperties().getProperty("SECURITY_TASK_SG_RULE_DELETE"))) {
                    deleteSecurityGroupRule(jsonBody);
                }

                try {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (IOException ex) {
                    Logger.error("Could not ack message. Exception: {}", ex);
                }
            }
        });
        securityThread.start();
    }

    public static String createNetwork(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

            List<GeminiNetwork> listNetworks = e.getApplications().stream()
                    .map(GeminiApplication::getNetworks)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            netProvisioningService.getProvider().bulkCreateNetwork(tenant, e, listNetworks);
        });
        return gson.toJson(tenant);
    }

    public static void updateNetwork(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //update the network(s) 
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

            e.getApplications().stream()
                    .map(GeminiApplication::getNetworks)
                    .flatMap(List::stream)
                    .forEach(n -> netProvisioningService.getProvider().updateNetwork(tenant, e, n));
        });
    }

    public static void deleteNetwork(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        List<ProvisioningProviderResponseType> retVals = new ArrayList();

        //update the network(s) 
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

            e.getApplications().stream()
                    .map(GeminiApplication::getNetworks)
                    .flatMap(List::stream)
                    .forEach(n -> netProvisioningService.getProvider().deleteNetwork(tenant, e, n));
        });
    }

    public static void createSubnet(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

            e.getApplications().stream()
                    .map(GeminiApplication::getNetworks)
                    .flatMap(List::stream)
                    .forEach(n -> netProvisioningService.getProvider().bulkCreateSubnet(tenant, e, n, n.getSubnets()));
        });
    }

    public static void updateSubnet(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

            e.getApplications().stream()
                    .map(GeminiApplication::getNetworks)
                    .flatMap(List::stream)
                    .map(GeminiNetwork::getSubnets)
                    .flatMap(List::stream)
                    .forEach(s -> netProvisioningService.getProvider().updateSubnet(tenant, e, s));
        });
    }

    public static void deleteSubnet(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

            e.getApplications().stream()
                    .map(GeminiApplication::getNetworks)
                    .flatMap(List::stream)
                    .map(GeminiNetwork::getSubnets)
                    .flatMap(List::stream)
                    .forEach(s -> netProvisioningService.getProvider().deleteSubnet(tenant, e, s));
        });
    }

    public static void createRouter(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);
            netProvisioningService.getProvider().bulkCreateRouter(tenant, e, e.getRouters());
        });
    }

    public static void updateRouter(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);
            e.getRouters().stream().forEach(r -> netProvisioningService.getProvider().updateRouter(tenant, e, r));
        });
    }

    public static void deleteRouter(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service - could be different environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(new NetworkProviderModule(e.getType()));
            netProvisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);
            e.getRouters().stream().forEach(r -> netProvisioningService.getProvider().deleteRouter(tenant, e, r));
        });
    }

    public static void createSecurityGroup(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service, it could be different types of environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new SecurityProviderModule(e.getType()));
            secProvisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);

            e.getSecurityGroups().stream()
                    .forEach(sg -> secProvisioningService.getProvider().createSecurityGroup(tenant, e, sg));
        });
    }

    public static void updateSecurityGroup(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service, it could be different types of environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new SecurityProviderModule(e.getType()));
            secProvisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);

            e.getSecurityGroups().stream()
                    .forEach(sg -> secProvisioningService.getProvider().updateSecurityGroup(tenant, e, sg));
        });
    }

    public static void deleteSecurityGroup(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service, it could be different types of environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new SecurityProviderModule(e.getType()));
            secProvisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);

            e.getSecurityGroups().stream()
                    .forEach(sg -> secProvisioningService.getProvider().deleteSecurityGroup(tenant, e, sg));
        });
    }

    public static void createSecurityGroupRule(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service, it could be different types of environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new SecurityProviderModule(e.getType()));
            secProvisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);

            e.getSecurityGroups()
                    .stream()
                    .map(GeminiSecurityGroup::getSecurityRules)
                    .flatMap(List::stream)
                    .forEach(sgr -> secProvisioningService.getProvider().createSecurityGroupRule(tenant, e, sgr.getParent(), sgr));
        });
    }

    public static void updateSecurityGroupRule(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service, it could be different types of environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new SecurityProviderModule(e.getType()));
            secProvisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);

            e.getSecurityGroups()
                    .stream()
                    .map(GeminiSecurityGroup::getSecurityRules)
                    .flatMap(List::stream)
                    .forEach(sgr -> secProvisioningService.getProvider().updateSecurityGroupRule(tenant, e, sgr.getParent(), sgr));
        });
    }

    public static void deleteSecurityGroupRule(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //get the network(s) to be created
        tenant.getEnvironments().stream().forEach(e -> {
            //create the provisioning service, it could be different types of environments
            //it shouldn't matter if it is the same because the provisioning service is a singleton
            Injector provisioningInjector = Guice.createInjector(
                    new SecurityProviderModule(e.getType()));
            secProvisioningService = provisioningInjector.getInstance(SecurityProvisioningService.class);

            e.getSecurityGroups()
                    .stream()
                    .map(GeminiSecurityGroup::getSecurityRules)
                    .flatMap(List::stream)
                    .forEach(sgr -> secProvisioningService.getProvider().deleteSecurityGroupRule(tenant, e, sgr.getParent(), sgr));
        });
    }
}
