/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.provision.network.main;

import com.gemini.mapper.GeminiMapper;
import com.gemini.mapper.GeminiMapperModule;
import com.gemini.properties.GeminiProperties;
import com.gemini.properties.GeminiPropertiesModule;
import static com.gemini.provision.network.main.GeminiNetworkProvisionMain.mapper;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.pmw.tinylog.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author schari
 */
public class GeminiNetworkProvisionMainIT {

    //message sender related
    private static GeminiProperties properties;
    private static Connection sendMsgConnection = null;
    private static Channel sendMsgChannel = null;

    //message receiver related
    private static Connection recvNetMsgConnection;
    private static Channel recvNetMsgChannel;
    private static QueueingConsumer recvNetMsgconsumer;
    private static Connection recvSecMsgConnection;
    private static Channel recvSecMsgChannel;
    private static QueueingConsumer recvSecMsgconsumer;
    

    public GeminiNetworkProvisionMainIT() {
    }

    @BeforeClass
    public static void setUpClass() {
        //load the properties singleton, will be used throughout the tests.
        properties = Guice.createInjector(new GeminiPropertiesModule()).getInstance(GeminiProperties.class);

        //create the mapper in the main class
        //inject the mapper as it will be used in all the threads
        Injector mapperInjector = Guice.createInjector(new GeminiMapperModule());
        mapper = mapperInjector.getInstance(GeminiMapper.class);

        //create the sender
        ConnectionFactory sendMsgFactory = new ConnectionFactory();
        sendMsgFactory.setHost(properties.getProperties().getProperty("MESSAGING_HOST"));
        try {
            sendMsgConnection = sendMsgFactory.newConnection();
            sendMsgChannel = sendMsgConnection.createChannel();
            sendMsgChannel.exchangeDeclare(properties.getProperties().getProperty("EXCHANGE_NAME"), "topic");
        } catch (IOException ex) {
            Logger.error("Fatal Error: Sender unable to connect to messaging system. Exception: {}", ex);
            fail("Unable to connect to messaging system");
        }

        //create the network receiver
        ConnectionFactory recvNetMsgFactory = new ConnectionFactory();
        recvNetMsgFactory.setHost(properties.getProperties().getProperty("MESSAGING_HOST"));
        String netQueueName = null;
        try {
            recvNetMsgConnection = recvNetMsgFactory.newConnection();
            recvNetMsgChannel = recvNetMsgConnection.createChannel();
            recvNetMsgChannel.exchangeDeclare(properties.getProperties().getProperty("EXCHANGE_NAME"), "topic");
            netQueueName = recvNetMsgChannel.queueDeclare().getQueue();
            recvNetMsgChannel.queueBind(netQueueName, properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("NETWORK_TOPIC"));
            recvNetMsgconsumer = new QueueingConsumer(recvNetMsgChannel);
            recvNetMsgChannel.basicConsume(netQueueName, true, recvNetMsgconsumer);
        } catch (IOException | NullPointerException | NumberFormatException ex) {
            Logger.error("Fatal Error: Network receiver could not connect to messaging system. Exception: {}", ex);
            fail("Fatal Error: Receiver could not connect to messaging system.");
        }

        //create the network receiver
        ConnectionFactory recvSecMsgFactory = new ConnectionFactory();
        recvSecMsgFactory.setHost(properties.getProperties().getProperty("MESSAGING_HOST"));
        String secQueueName = null;
        try {
            recvSecMsgConnection = recvSecMsgFactory.newConnection();
            recvSecMsgChannel = recvSecMsgConnection.createChannel();
            recvSecMsgChannel.exchangeDeclare(properties.getProperties().getProperty("EXCHANGE_NAME"), "topic");
            secQueueName = recvSecMsgChannel.queueDeclare().getQueue();
            recvSecMsgChannel.queueBind(secQueueName, properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("SECURITY_TOPIC"));
            recvSecMsgconsumer = new QueueingConsumer(recvSecMsgChannel);
            recvSecMsgChannel.basicConsume(secQueueName, true, recvNetMsgconsumer);
        } catch (IOException | NullPointerException | NumberFormatException ex) {
            Logger.error("Fatal Error: Security receiver could not connect to messaging system. Exception: {}", ex);
            fail("Fatal Error: Receiver could not connect to messaging system.");
        }
    }

    @AfterClass
    public static void tearDownClass() {
        //close the sender
        try {
            if (sendMsgChannel.isOpen()) {
                sendMsgChannel.close();
            }
            if (sendMsgConnection.isOpen()) {
                sendMsgConnection.close();
            }
        } catch (IOException ex) {
            Logger.error("Sender unable to close the connection to messaging system. Exception: {}", ex);
            fail("Unable to close the connection to messaging system.");
        }

        //close the network receiver
        try {
            if (recvNetMsgChannel.isOpen()) {
                recvNetMsgChannel.close();
            }
            if (recvNetMsgConnection.isOpen()) {
                recvNetMsgConnection.close();
            }
        } catch (IOException ex) {
            Logger.error("Network receiver unable to close the connection to messaging system. Exception: {}", ex);
            fail("Unable to close the connection to messaging system.");
        }

        //close the security receiver
        try {
            if (recvSecMsgChannel.isOpen()) {
                recvSecMsgChannel.close();
            }
            if (recvSecMsgConnection.isOpen()) {
                recvSecMsgConnection.close();
            }
        } catch (IOException ex) {
            Logger.error("Security receiver unable to close the connection to messaging system. Exception: {}", ex);
            fail("Unable to close the connection to messaging system.");
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     *
     */
    @Test
    public void testCreateNetwork() {
        System.out.println("testCreateNetwork");

        //first send the message
        //load the YAML file
        Yaml yaml = new Yaml();
        InputStream input;
        Object json = null;
        try {
            input = new FileInputStream(new File(properties.getProperties().getProperty("TEST_NETWORK_CREATE_FILE")));
            json = yaml.load(input);
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.error("Unable to open test create network YAML file Exception: {}", ex);
            fail("Unable to open test YAML file.");
        } catch (IOException ex) {
            Logger.error("Unable to read test YAML file Exception: {}", ex);
            fail("Unable to read test YAML file.");
        }

        //convert the YAML to json
        Gson gson = new Gson();
        try {
            sendMsgChannel.basicPublish(properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("NETWORK_TASK_CREATE"),
                    null, gson.toJson(json).getBytes());
        } catch (IOException ex) {
            Logger.error("Unable to send a message to the messaging system. Exception: {}", ex);
            fail("Unable to send a message to the messaging system.");
        }

        //now receive the message and act on it
        QueueingConsumer.Delivery delivery = null;
        while (true) {
            try {
                delivery = recvNetMsgconsumer.nextDelivery();
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                Logger.error("Could not get message from queue. Exception: {}", ex);

                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                continue;
            }

            String routingKey = delivery.getEnvelope().getRoutingKey();
            if (!routingKey.equals(properties.getProperties().getProperty("NETWORK_TASK_CREATE"))) {
                try {
                    //return the message to queue, we want this unit test to only process the create test case
                    //the update and delete messages may already be in the queue
                    recvNetMsgChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                } catch (IOException ex) {
                    Logger.error("Could not return the message back to queue... routing key {} delivery tag {} Exception: {}",
                            routingKey, delivery.getEnvelope().getDeliveryTag(), ex);
                }
                continue;
            }
            if (routingKey.equals(properties.getProperties().getProperty("NETWORK_TASK_CREATE"))) {
                String jsonBody = new String(delivery.getBody());
                String returnJson = GeminiNetworkProvisionMain.createNetwork(jsonBody);

                /*
                 THIS SECTION IS ONLY FOR UNIT TESTS
                 */
                try {
                    FileWriter writer = new FileWriter(properties.getProperties().getProperty("TEST_NETWORK_UPDATE_FILE"));
                    yaml.dump(gson.fromJson(returnJson, Object.class), writer);
                    writer.flush();
                    writer.close();
                } catch (FileNotFoundException ex) {
                    Logger.error("Could not create file for writing network update yaml.");
                } catch (IOException ex) {
                    Logger.error("Could not write network update file");
                }
                /*
                 END OF UNIT TEST ONLY SECTION
                 */
            }

            try {
                recvNetMsgChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException ex) {
                Logger.error("Could not ack message. Delivery tag {} Exception: {}",
                        delivery.getEnvelope().getDeliveryTag(), ex);
            }

            //we need to just process one message for the test
            break;
        }
    }

    //TESTS HAVE BEEN DELIBERATELY COMMENTED OUT BECAUSE THE SERIALIZATION
    //FUNCTIONS NEED TO BE IMPLEMENTED
    @Test
    public void testUpdateNetwork() {
        System.out.println("testUpdateNetwork");

        //first send the message
        //load the YAML file
        Yaml yaml = new Yaml();
        InputStream input;
        Object json = null;
        try {
            input = new FileInputStream(new File(properties.getProperties().getProperty("TEST_NETWORK_UPDATE_FILE")));
            json = yaml.load(input);
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.error("Unable to open test update network YAML file Exception: {}", ex);
            fail("Unable to open test YAML file.");
        } catch (IOException ex) {
            Logger.error("Unable to read test update network YAML file Exception: {}", ex);
            fail("Unable to read test YAML file.");
        }

        //convert the YAML to json
        Gson gson = new Gson();
        try {
            sendMsgChannel.basicPublish(properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("NETWORK_TASK_UPDATE"),
                    null, gson.toJson(json).getBytes());
        } catch (IOException ex) {
            Logger.error("Unable to send a message to the messaging system. Exception: {}", ex);
            fail("Unable to send a message to the messaging system.");
        }
        System.out.println(" [x] Sent '" + gson.toJson(json) + "'");

        //now receive the message and act on it
        QueueingConsumer.Delivery delivery = null;
        while (true) {
            try {
                delivery = recvNetMsgconsumer.nextDelivery();
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                Logger.error("Could not get message from queue. Exception: {}", ex);

                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                continue;
            }

            String routingKey = delivery.getEnvelope().getRoutingKey();
            if (!routingKey.equals(properties.getProperties().getProperty("NETWORK_TASK_UPDATE"))) {
                try {
                    //return the message to queue, we want this unit test to only process the create test case
                    //the update and delete messages may already be in the queue
                    recvNetMsgChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                } catch (IOException ex) {
                    Logger.error("Could not return the message back to queue... routing key {} delivery tag {} Exception: {}",
                            routingKey, delivery.getEnvelope().getDeliveryTag(), ex);
                }
                continue;
            }
            String jsonBody = new String(delivery.getBody());
            GeminiNetworkProvisionMain.updateNetwork(jsonBody);

            try {
                recvNetMsgChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException ex) {
                Logger.error("Could not ack message. Exception: {}", ex);
                fail("Could not ack message.");
            }
            //we need to just process one message for the test
            break;
        }
    }

    @Test
    public void testDeleteNetwork() {
        System.out.println("testDeleteNetwork");

        //first send the message
        //load the YAML file
        Yaml yaml = new Yaml();
        InputStream input;
        Object json = null;
        try {
            input = new FileInputStream(new File(properties.getProperties().getProperty("TEST_NETWORK_DELETE_FILE")));
            json = yaml.load(input);
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.error("Unable to open test delete network YAML file Exception: {}", ex);
            fail("Unable to open test delete network YAML file.");
        } catch (IOException ex) {
            Logger.error("Unable to read test delete network YAML file Exception: {}", ex);
            fail("Unable to read test delete network YAML file.");
        }

        //convert the YAML to json
        Gson gson = new Gson();
        try {
            sendMsgChannel.basicPublish(properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("NETWORK_TASK_DELETE"),
                    null, gson.toJson(json).getBytes());
        } catch (IOException ex) {
            Logger.error("Unable to send a delete network message to the messaging system. Exception: {}", ex);
            fail("Unable to send a delete network message to the messaging system.");
        }
        System.out.println(" [x] Sent '" + gson.toJson(json) + "'");

        //now receive the message and act on it
        QueueingConsumer.Delivery delivery = null;
        while (true) {
            try {
                delivery = recvNetMsgconsumer.nextDelivery();
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                Logger.error("Could not get message from queue. Exception: {}", ex);

                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                continue;
            }

            String routingKey = delivery.getEnvelope().getRoutingKey();
            if (!routingKey.equals(properties.getProperties().getProperty("NETWORK_TASK_DELETE"))) {
                try {
                    //return the message to queue, we want this unit test to only process the create test case
                    //the update and delete messages may already be in the queue
                    recvNetMsgChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                } catch (IOException ex) {
                    Logger.error("Could not return the message back to queue... routing key {} delivery tag {} Exception: {}",
                            routingKey, delivery.getEnvelope().getDeliveryTag(), ex);
                }
                continue;
            }
            String jsonBody = new String(delivery.getBody());
            GeminiNetworkProvisionMain.deleteNetwork(jsonBody);

            try {
                recvNetMsgChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException ex) {
                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                Logger.error("Could not ack message. Exception: {}", ex);
            }
            //we need to just process one message for the test
            break;
        }
    }

    @Test
    public void testCreateSubnet() {
        System.out.println("testCreateSubnet");

        //first send the message
        //load the YAML file
        Yaml yaml = new Yaml();
        InputStream input;
        Object json = null;
        try {
            input = new FileInputStream(new File(properties.getProperties().getProperty("TEST_SUBNET_CREATE_FILE")));
            json = yaml.load(input);
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.error("Unable to open test create subnet YAML file Exception: {}", ex);
            fail("Unable to open test create subnet YAML file.");
        } catch (IOException ex) {
            Logger.error("Unable to read test YAML file Exception: {}", ex);
            fail("Unable to read test YAML file.");
        }

        //convert the YAML to json
        Gson gson = new Gson();
        try {
            sendMsgChannel.basicPublish(properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("SUBNET_TASK_CREATE"),
                    null, gson.toJson(json).getBytes());
        } catch (IOException ex) {
            Logger.error("Unable to send a message to the messaging system. Exception: {}", ex);
            fail("Unable to send a message to the messaging system.");
        }
        System.out.println(" [x] Sent '" + gson.toJson(json) + "'");

        //now receive the message and act on it
        QueueingConsumer.Delivery delivery = null;
        while (true) {
            try {
                delivery = recvNetMsgconsumer.nextDelivery();
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                Logger.error("Could not get message from queue. Exception: {}", ex);

                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                continue;
            }

            String routingKey = delivery.getEnvelope().getRoutingKey();
            if (!routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_CREATE"))) {
                try {
                    //return the message to queue, we want this unit test to only process the create test case
                    //the update and delete messages may already be in the queue
                    recvNetMsgChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                } catch (IOException ex) {
                    Logger.error("Could not return the message back to queue... routing key {} delivery tag {} Exception: {}",
                            routingKey, delivery.getEnvelope().getDeliveryTag(), ex);
                }
                continue;
            }
            String jsonBody = new String(delivery.getBody());

            if (routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_CREATE"))) {
                GeminiNetworkProvisionMain.createSubnet(jsonBody);
            }

            try {
                recvNetMsgChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException ex) {
                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                Logger.error("Could not ack message. Exception: {}", ex);
            }
            //we need to just process one message for the test
            break;
        }
    }

    @Test
    public void testUpdateSubnet() {
        System.out.println("testUpdateSubnet");

        //first send the message
        //load the YAML file
        Yaml yaml = new Yaml();
        InputStream input;
        Object json = null;
        try {
            input = new FileInputStream(new File(properties.getProperties().getProperty("TEST_SUBNET_UPDATE_FILE")));
            json = yaml.load(input);
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.error("Unable to open test update subnet YAML file Exception: {}", ex);
            fail("Unable to open test update subnet YAML file.");
        } catch (IOException ex) {
            Logger.error("Unable to read test YAML file Exception: {}", ex);
            fail("Unable to read test YAML file.");
        }

        //convert the YAML to json
        Gson gson = new Gson();
        try {
            sendMsgChannel.basicPublish(properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("SUBNET_TASK_UPDATE"),
                    null, gson.toJson(json).getBytes());
        } catch (IOException ex) {
            Logger.error("Unable to send a message to the messaging system. Exception: {}", ex);
            fail("Unable to send a message to the messaging system.");
        }
        System.out.println(" [x] Sent '" + gson.toJson(json) + "'");

        //now receive the message and act on it
        QueueingConsumer.Delivery delivery = null;
        while (true) {
            try {
                delivery = recvNetMsgconsumer.nextDelivery();
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                Logger.error("Could not get message from queue. Exception: {}", ex);

                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                continue;
            }

            String routingKey = delivery.getEnvelope().getRoutingKey();
            if (!routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_UPDATE"))) {
                try {
                    //return the message to queue, we want this unit test to only process the create test case
                    //the update and delete messages may already be in the queue
                    recvNetMsgChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                } catch (IOException ex) {
                    Logger.error("Could not return the message back to queue... routing key {} delivery tag {} Exception: {}",
                            routingKey, delivery.getEnvelope().getDeliveryTag(), ex);
                }
                continue;
            }
            String jsonBody = new String(delivery.getBody());

            if (routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_UPDATE"))) {
                GeminiNetworkProvisionMain.updateSubnet(jsonBody);
            }

            try {
                recvNetMsgChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException ex) {
                //TODO: NEED TO PUT THE MESSAGE BACK IN THE QUEUE
                Logger.error("Could not ack message. Exception: {}", ex);
            }
            //we need to just process one message for the test
            break;
        }
    }

    @Test
    public void testDeleteSubnet() {
        System.out.println("testDeleteSubnet");

        //first send the message
        //load the YAML file
        Yaml yaml = new Yaml();
        InputStream input;
        Object json = null;
        try {
            input = new FileInputStream(new File(properties.getProperties().getProperty("TEST_SUBNET_DELETE_FILE")));
            json = yaml.load(input);
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.error("Unable to open test delete subnet YAML file Exception: {}", ex);
            fail("Unable to open test update delete YAML file.");
        } catch (IOException ex) {
            Logger.error("Unable to read test YAML file Exception: {}", ex);
            fail("Unable to read test YAML file.");
        }

        //convert the YAML to json
        Gson gson = new Gson();
        try {
            sendMsgChannel.basicPublish(properties.getProperties().getProperty("EXCHANGE_NAME"),
                    properties.getProperties().getProperty("SUBNET_TASK_DELETE"),
                    null, gson.toJson(json).getBytes());
        } catch (IOException ex) {
            Logger.error("Unable to send a message to the messaging system. Exception: {}", ex);
            fail("Unable to send a message to the messaging system.");
        }
        System.out.println(" [x] Sent '" + gson.toJson(json) + "'");

        //now receive the message and act on it
        QueueingConsumer.Delivery delivery = null;
        while (true) {
            try {
                delivery = recvNetMsgconsumer.nextDelivery();
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                Logger.error("Could not get message from queue. Exception: {}", ex);
                continue;
            }

            String routingKey = delivery.getEnvelope().getRoutingKey();
            if (!routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_DELETE"))) {
                try {
                    //return the message to queue, we want this unit test to only process the create test case
                    //the update and delete messages may already be in the queue
                    recvNetMsgChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
                } catch (IOException ex) {
                    Logger.error("Could not return the message back to queue... routing key {} delivery tag {} Exception: {}",
                            routingKey, delivery.getEnvelope().getDeliveryTag(), ex);
                }
                continue;
            }
            String jsonBody = new String(delivery.getBody());

            if (routingKey.equals(properties.getProperties().getProperty("SUBNET_TASK_DELETE"))) {
                GeminiNetworkProvisionMain.updateSubnet(jsonBody);
            }

            try {
                recvNetMsgChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException ex) {
                //TODO: DO WE NEED TO PUT THE MESSAGE BACK IN THE QUEUE?
                Logger.error("Could not ack message. Exception: {}", ex);
            }
            //we need to just process one message for the test
            break;
        }
    }
}
