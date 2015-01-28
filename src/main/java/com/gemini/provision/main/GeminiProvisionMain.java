package com.gemini.provision.main;

import com.gemini.domain.dto.GeminiTenantDTO;
import com.gemini.domain.dto.deserialize.GeminiTenantDeserializer;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.mapper.GeminiMapper;
import com.gemini.mapper.GeminiMapperModule;
import com.gemini.provision.network.base.NetworkProviderModule;
import com.gemini.provision.network.base.NetworkProvisioningService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
    static GeminiMapper mapper;
    final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, InterruptedException {
        Injector mapperInjector = Guice.createInjector(new GeminiMapperModule());
        mapper = mapperInjector.getInstance(GeminiMapper.class);

        //create the provisioning service 
        Injector provisioningInjector = Guice.createInjector(
                new NetworkProviderModule(env.getType()));
        provisioningService = provisioningInjector.getInstance(NetworkProvisioningService.class);

//        List<GeminiNetwork> gateways = provisioningService.getProvisioningService().getExternalGateways(tenant, env);
//        //we should only one have one gateway
//        gateways.stream().forEach(System.out::println);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);

        //create a gson object and pass the customer deserialization module for the tenant. Other custom
        //deserializers will be passed in the respective deserialization functions
        Gson gson = new GsonBuilder().registerTypeAdapter(GeminiTenantDTO.class, new GeminiTenantDeserializer()).create();
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
            GeminiTenantDTO tenantDTO = gson.fromJson(message, GeminiTenantDTO.class);
            System.out.print(ToStringBuilder.reflectionToString(tenantDTO, ToStringStyle.MULTI_LINE_STYLE));
            //System.out.println(" [x] Received '" + message + "'");
        }
    }
}
