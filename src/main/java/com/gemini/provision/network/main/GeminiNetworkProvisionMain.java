package com.gemini.provision.network.main;

import com.gemini.common.repository.BaseRepository;
import com.gemini.common.repository.BaseRepositoryFactory;
import com.gemini.common.repository.GeminiDatabaseModule;
import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiTenantDTO;
import com.gemini.domain.dto.deserialize.GeminiNetworkDeserializer;
import com.gemini.domain.dto.deserialize.GeminiTenantDeserializer;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiTenant;
import com.gemini.mapper.GeminiMapper;
import com.gemini.mapper.GeminiMapperModule;
import com.gemini.properties.GeminiProperties;
import com.gemini.properties.GeminiPropertiesModule;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.network.base.NetworkProvisioningService;
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
import java.util.List;
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

    static NetworkProvisioningService provisioningService;
    static GeminiMapper mapper;

    public static void main(String[] args) {

        //activate logging level if it is DEBUG, default is INFO so no need for any
        //action if it is otherwise
        Injector propInjector = Guice.createInjector(new GeminiPropertiesModule());
        GeminiProperties properties = propInjector.getInstance(GeminiProperties.class);
        String loggingLevel = properties.getProperties().getProperty("LOGGING_LEVEL");
        if (loggingLevel.equals("DEBUG")) {
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

                if (routingKey.contains(properties.getProperties().getProperty("NETWORK_TASK_CREATE_SINGLE"))) {
                    createSingleNetwork(jsonBody);
                } else if (routingKey.contains(properties.getProperties().getProperty("NETWORK_TASK_CREATE_BULK"))) {
                    createBulkNetwork(jsonBody);
                } else if (routingKey.contains(properties.getProperties().getProperty("NETWORK_TASK_UPDATE_SINGLE"))) {
                    updateNetwork(jsonBody);
                } else if (routingKey.contains(properties.getProperties().getProperty("NETWORK_TASK_UPDATE_BULK"))) {
                    updateBulkNetwork(jsonBody);
                }

                try {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (IOException ex) {
                    //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                    Logger.error("Could not ack message. Exception: {}", ex);
                }
            }
        }
        );
        networkingThread.start();

        //the load balancer 
        Thread lbThread = new Thread(() -> {
        });

        lbThread.start();

        //the security thread
        Thread securityThread = new Thread(() -> {
        });

        securityThread.start();
    }

    private static void initializeTenant(GeminiTenant tenant) {
        BaseRepositoryFactory baseRepoFactory;

        Injector dbInjector = Guice.createInjector(new GeminiDatabaseModule());
        baseRepoFactory
                = dbInjector.getInstance(BaseRepositoryFactory.class
                );
        BaseRepository baseRepo = baseRepoFactory.create(GeminiNetwork.class);
        List<GeminiNetwork> listNetworks = baseRepo.list();
    }

    private static void createSingleNetwork(String jsonBody) {
        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        GeminiTenantDTO tenantDTO = gson.fromJson(jsonBody, GeminiTenantDTO.class);
        GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);

        //create the provisioning service 
        Injector provisioningInjector = Guice.createInjector(
                new NetworkProviderModule(tenant.getEnvironments().get(0).getType()));
        provisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);
    }

    private static void createBulkNetwork(String jsonBody) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void updateNetwork(String jsonBody) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void updateBulkNetwork(String jsonBody) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
