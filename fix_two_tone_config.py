import re

with open('src/main/java/io/github/dsheirer/playlist/TwoToneConfiguration.java', 'r') as f:
    content = f.read()

# Make sure all required properties exist (after merge with master we lost some bindings)

if "mZelloAlertToneEnabledProperty" not in content:
    # Add it
    props = """    private StringProperty mMqttPayloadProperty = new SimpleStringProperty("{\\"detector\\": \\"[DetectorName]\\", \\"state\\": \\"ON\\", \\"time\\": \\"[Timestamp]\\"}");
    private javafx.beans.property.BooleanProperty mZelloAlertToneEnabledProperty = new javafx.beans.property.SimpleBooleanProperty(false);
    private StringProperty mAlertToneFileProperty = new SimpleStringProperty();"""
    content = content.replace("    private StringProperty mMqttPayloadProperty = new SimpleStringProperty(\"{\\\"detector\\\": \\\"[DetectorName]\\\", \\\"state\\\": \\\"ON\\\", \\\"time\\\": \\\"[Timestamp]\\\"}\");", props)

if "isZelloAlertToneEnabled" not in content:
    getters = """    @JsonIgnore
    public StringProperty mqttPayloadProperty()
    {
        return mMqttPayloadProperty;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "zelloAlertToneEnabled")
    public boolean isZelloAlertToneEnabled()
    {
        return mZelloAlertToneEnabledProperty.get();
    }

    public void setZelloAlertToneEnabled(boolean enabled)
    {
        mZelloAlertToneEnabledProperty.set(enabled);
    }

    @JsonIgnore
    public javafx.beans.property.BooleanProperty zelloAlertToneEnabledProperty()
    {
        return mZelloAlertToneEnabledProperty;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "alertToneFile")
    public String getAlertToneFile()
    {
        return mAlertToneFileProperty.get();
    }

    public void setAlertToneFile(String file)
    {
        mAlertToneFileProperty.set(file);
    }

    @JsonIgnore
    public StringProperty alertToneFileProperty()
    {
        return mAlertToneFileProperty;
    }"""
    content = content.replace("""    @JsonIgnore
    public StringProperty mqttPayloadProperty()
    {
        return mMqttPayloadProperty;
    }""", getters)

if "copy.setZelloAlertToneEnabled" not in content:
    copy_of = """        copy.setMqttPayload(getMqttPayload());
        copy.setZelloAlertToneEnabled(isZelloAlertToneEnabled());
        copy.setAlertToneFile(getAlertToneFile());"""
    content = content.replace("        copy.setMqttPayload(getMqttPayload());", copy_of)

if "config.zelloAlertToneEnabledProperty" not in content:
    ext = """            config.aliasProperty(), config.templateProperty(), config.zelloChannelProperty(), config.enableMqttPublishProperty(), config.mqttTopicProperty(), config.mqttPayloadProperty(), config.zelloAlertToneEnabledProperty(), config.alertToneFileProperty()
        };"""
    content = re.sub(r'            config\.aliasProperty\(\), config\.templateProperty\(\), config\.zelloChannelProperty\(\), config\.enableMqttPublishProperty\(\), config\.mqttTopicProperty\(\), config\.mqttPayloadProperty\(\)\s*\};\s*', ext, content)


with open('src/main/java/io/github/dsheirer/playlist/TwoToneConfiguration.java', 'w') as f:
    f.write(content)
