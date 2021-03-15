package com.mqtt.client;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;


@Component
public class Mqtt {
    //"tcp://192.168.2.60:1883"
    private static final String MQTT_PUBLISHER_ID = "spring-server";
    private static final String MQTT_SERVER_ADDRES= "ssl://aztrack.asiczen.com:8883"; //url=aztrack.asiczen.com,topic="test/devices/#"
    private static IMqttClient instance;
    @Autowired
    ResourceLoader resourceLoader;

    public IMqttClient getInstance() {
        try {
            if (instance == null) {
                instance = new MqttClient(MQTT_SERVER_ADDRES, MQTT_PUBLISHER_ID);
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(createSSLSocketFactory());
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(60); // This value, measured in seconds, defines the maximum time interval the client will wait for the network connection to the MQTT server to be established
            options.setKeepAliveInterval(30);// This value, measured in seconds, defines the maximum time interval between messages sent or received

            if (!instance.isConnected()) {
                instance.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private Mqtt() {

    }

    public void subscribe(final String topic,IMqttClient mqtt) throws MqttException, InterruptedException {
        System.out.println("Messages received:");

        mqtt.subscribeWithResponse(topic, (tpic, msg) -> {
            Data d = new Gson().fromJson(new String(msg.getPayload()),Data.class);
            d.setDate(new Date());
            //System.out.println(msg.getId() + " -> " + new String(msg.getPayload()));
            System.out.println(d);
        });
    }

    private  SSLSocketFactory createSSLSocketFactory()
    {
        try
        {
            InputStream client_ca= resourceLoader.getResource("classpath:client_ca.pem").getInputStream();
            InputStream client_crt=resourceLoader.getResource("classpath:client_cert.pem").getInputStream();
            InputStream client_key=resourceLoader.getResource("classpath:client_key.pem").getInputStream();
            TrustManagerFactory tmf;
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca;
            try {
                ca = cf.generateCertificate(client_ca);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                client_ca.close();
            }
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            Certificate ca1;
            try {
                ca1 = cf.generateCertificate(client_crt);
                System.out.println("ca=" + ((X509Certificate) ca1).getSubjectDN());
            } finally {
                client_crt.close();
            }
            PrivateKey privateKey = loadPrivateKey(client_key);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry("certificate", ca1);
            ks.setKeyEntry("private-key", privateKey, null, new java.security.cert.Certificate[]{ca1});
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, null);

            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        }
        catch (IOException | KeyStoreException | KeyManagementException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e)
        {
            //LOG.error("Creating ssl socket factory failed", e);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey loadPrivateKey(InputStream is)
            throws IOException, GeneralSecurityException {
        PrivateKey key = null;
        //InputStream is = null;
        try {
            //is = fileName.getClass().getResourceAsStream("/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            boolean inKey = false;
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (!inKey) {
                    if (line.startsWith("-----BEGIN ") &&
                            line.endsWith("-----")) {
                        /*&&
                            line.endsWith(" PRIVATE KEY-----")*/
                        inKey = true;
                    }
                }
                else {
                    if (line.startsWith("-----END ") &&
                            line.endsWith("-----")) {
                        inKey = false;
                        break;
                    }
                    builder.append(line);
                }
            }
            //
            byte[] encoded = Base64.getDecoder().decode(builder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            key = kf.generatePrivate(keySpec);
        } finally {
            closeSilent(is);
        }
        return key;
    }

    public static void closeSilent(final InputStream is) {
        if (is == null) return;
        try { is.close(); } catch (Exception ign) {ign.printStackTrace();}
    }
}
