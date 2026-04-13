package io.github.dsheirer.audio.broadcast.mqtt;

import io.github.dsheirer.preference.UserPreferences;
import io.github.dsheirer.preference.mqtt.MqttPreference;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.dsheirer.eventbus.MyEventBus;
import com.google.common.eventbus.Subscribe;
import io.github.dsheirer.playlist.PlaylistLoadedEvent;
import io.github.dsheirer.playlist.TwoToneConfiguration;

public class MqttService
{
    private static final Logger mLog = LoggerFactory.getLogger(MqttService.class);
    private static MqttService mInstance;
    private MqttClient mClient;
    private MqttPreference mPreference;

    private MqttService(UserPreferences userPreferences)
    {
        mPreference = userPreferences.getMqttPreference();
        connect();
        MyEventBus.getGlobalEventBus().register(this);
    }

    public static synchronized MqttService getInstance(UserPreferences userPreferences)
    {
        if (mInstance == null) {
            mInstance = new MqttService(userPreferences);
        }
        return mInstance;
    }

    public static MqttService getInstance()
    {
        return mInstance;
    }

    public synchronized void connect()
    {
        if (!mPreference.isEnabled()) {
            disconnect();
            return;
        }

        try {
            if (mClient != null && mClient.isConnected()) {
                mClient.disconnect();
            }

            mClient = new MqttClient(mPreference.getServer(), mPreference.getClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            if (mPreference.getUsername() != null && !mPreference.getUsername().isEmpty()) {
                options.setUserName(mPreference.getUsername());
            }
            if (mPreference.getPassword() != null && !mPreference.getPassword().isEmpty()) {
                options.setPassword(mPreference.getPassword().toCharArray());
            }

            mLog.info("Connecting to MQTT broker: " + mPreference.getServer());
            mClient.connect(options);
            mLog.info("MQTT connected");
        } catch (MqttException e) {
            mLog.error("Error connecting to MQTT broker", e);
        }
    }

    public synchronized void disconnect()
    {
        if (mClient != null && mClient.isConnected()) {
            try {
                mClient.disconnect();
                mLog.info("MQTT disconnected");
            } catch (MqttException e) {
                mLog.error("Error disconnecting from MQTT broker", e);
            }
        }
    }

    public void publish(String topic, String payload)
    {
        if (mClient == null || !mClient.isConnected()) {
            mLog.warn("MQTT client is not connected, dropping message for topic: " + topic);
            return;
        }

        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(0);
            mClient.publish(topic, message);
            mLog.info("MQTT published to " + topic + ": " + payload);
        } catch (MqttException e) {
            mLog.error("Error publishing MQTT message", e);
        }
    }

    @Subscribe
    public void onPlaylistLoaded(PlaylistLoadedEvent event) {
        publishAutoDiscovery(event.getPlaylist());
    }

    private void publishAutoDiscovery(io.github.dsheirer.playlist.PlaylistV2 playlist) {
        if (!mPreference.isEnabled() || playlist == null) return;

        for (TwoToneConfiguration config : playlist.getTwoToneConfigurations()) {
            if (config.isEnableMqttPublish()) {
                String alias = config.getAlias() != null ? config.getAlias().replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase() : "unknown";
                String topic = "homeassistant/sensor/sdrtrunk_" + alias + "/config";
                String payload = String.format("{\"name\": \"%s\", \"state_topic\": \"%s\", \"value_template\": \"{{ value_json.state }}\"}",
                                               config.getAlias() != null ? config.getAlias() : "Unknown Detector",
                                               config.getMqttTopic());
                publish(topic, payload);
            }
        }
    }
}
