package io.github.dsheirer.preference.mqtt;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.github.dsheirer.preference.Preference;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.preference.PreferenceType;

import java.util.UUID;

public class MqttPreference extends Preference
{
    private String mServer = "tcp://localhost:1883";
    private String mUsername = "";
    private String mPassword = "";
    private String mClientId = "";
    private boolean mEnabled = false;

    public MqttPreference(Listener<PreferenceType> listener)
    {
        super(listener);
    }

    @JacksonXmlProperty(isAttribute = true, localName = "server")
    public String getServer()
    {
        return mServer;
    }

    public void setServer(String server)
    {
        mServer = server;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "username")
    public String getUsername()
    {
        return mUsername;
    }

    public void setUsername(String username)
    {
        mUsername = username;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "password")
    public String getPassword()
    {
        return mPassword;
    }

    public void setPassword(String password)
    {
        mPassword = password;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "clientId")
    public String getClientId()
    {
        if (mClientId == null || mClientId.isEmpty()) {
            mClientId = "SDRTrunk-" + UUID.randomUUID().toString();
        }
        return mClientId;
    }

    public void setClientId(String clientId)
    {
        mClientId = clientId;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "enabled")
    public boolean isEnabled()
    {
        return mEnabled;
    }

    public void setEnabled(boolean enabled)
    {
        mEnabled = enabled;
    }

    @Override
    public PreferenceType getPreferenceType()
    {
        return PreferenceType.MQTT;
    }
}
