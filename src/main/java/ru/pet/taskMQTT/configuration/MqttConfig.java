package ru.pet.taskMQTT.configuration;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@IntegrationComponentScan
public class MqttConfig {

    @Value("${mosquitto.admin}")
    private String mosquittoAdmin;

    @Value("${mosquitto.pswd}")
    private String mosquittoPswd;

    @Value("${mosquitto.url}")
    private String url;

    @Value("${topic.sensors}")
    private String topicSensors;

    @Value("${topic.signalization}")
    private String topicSignalization;

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setHttpsHostnameVerificationEnabled(true);
        options.setServerURIs(new String[] {url});
        options.setUserName(mosquittoAdmin);
        options.setPassword(mosquittoPswd.toCharArray());
        options.setCleanSession(true);

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(url, "Client", topicSensors);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public MessageChannel mqttOutboundChannel(){
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outBound(){
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("Server",
                mqttPahoClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(topicSignalization);
        messageHandler.setDefaultQos(1);
        messageHandler.setDefaultRetained(true);
        return messageHandler;
    }
}