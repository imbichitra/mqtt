package com.mqtt;

import com.mqtt.client.Mqtt;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MqttApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(MqttApplication.class, args);
		Mqtt mqtt = context.getBean(Mqtt.class);
		try {
			mqtt.subscribe("test/devices/#", mqtt.getInstance());
		} catch (MqttException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
