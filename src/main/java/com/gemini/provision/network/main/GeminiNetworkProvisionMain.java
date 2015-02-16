package com.gemini.provision.network.main;

import com.gemini.common.repository.BaseRepository;
import com.gemini.common.repository.BaseRepositoryFactory;
import com.gemini.common.repository.GeminiDatabaseModule;
import com.gemini.domain.dto.GeminiTenantDTO;
import com.gemini.domain.dto.deserialize.GeminiTenantDeserializer;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiTenant;
import com.gemini.mapper.GeminiMapper;
import com.gemini.mapper.GeminiMapperModule;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.network.base.NetworkProvisioningService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.FileWriter;
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
    final static String QUEUE_NAME = "hello";
    private static String SECTION_MSG_QUEUES = "msg_queues";
    private static String IN_QUEUE = "netprov_in_queue";
    private static String OUT_QUEUE = "netprov_out_queue";
    private static String LOCAL_IN_QUEUE = "netprov_local_in_queue";
    private static String LOCAL_OUT_QUEUE = "netprov_local_out_queue";

    private static String EXCHANGE_NAME = "Gemini_Systems";
    private static boolean MESSAGE_DURABILITY = true;
    private static Integer PREFETCH_COUNT = 1;
    private static String NETWORK_TOPIC = "network*";
    private static String SECURITY_TOPIC = "security*";
    private static String LB_TOPIC = "load*";

    public static void main(String[] args) throws IOException, InterruptedException {
        
        Configurator.defaultConfig().level(Level.DEBUG).activate();
        
        Injector mapperInjector = Guice.createInjector(new GeminiMapperModule());
        mapper = mapperInjector.getInstance(GeminiMapper.class);

        initializeTenant(null);
        
        //intialize this service... 
        //TODO: EVENTUALLY THIS WILL MOVE TO A SEPARATE CUSTOMER ONBOARDING APPLICATION
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        channel.basicQos(PREFETCH_COUNT);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();

        //start the topic 
        //the networking message recevier
        Thread networkingThread = new Thread(() -> {
            QueueingConsumer.Delivery delivery = null;
            try {
                delivery = consumer.nextDelivery();
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                Logger.error("Could not get message from queue. Exception: {}", ex);
            }

            String message = new String(delivery.getBody());
            GeminiTenantDTO tenantDTO = gson.fromJson(message, GeminiTenantDTO.class);
            GeminiTenant tenant = mapper.getTenantFromDTO(tenantDTO);
            if (!tenant.isInitialized()) {
                //read all the information from cloud and update the database
                initializeTenant(tenant);
            }

            //create the provisioning service 
            Injector provisioningInjector = Guice.createInjector(
                    new NetworkProviderModule(tenant.getEnvironments().get(0).getType()));
            provisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

            List<GeminiNetwork> gateways = provisioningService
                    .getProvider()
                    .getExternalGateways(tenant, tenant.getEnvironments().get(0));
            gateways.stream().forEach(System.out::println);

            try {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException ex) {
                Logger.error("Could not basic ack message. Exception: {}", ex);
            }
        });
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
        baseRepoFactory = dbInjector.getInstance(BaseRepositoryFactory.class);
        BaseRepository baseRepo = baseRepoFactory.create("Gemini", GeminiNetwork.class);
        List<GeminiNetwork> listNetworks = baseRepo.list();
    }
}
