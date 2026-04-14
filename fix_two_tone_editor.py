import sys

with open('src/main/java/io/github/dsheirer/gui/playlist/twotone/TwoToneEditor.java', 'r') as f:
    content = f.read()

# It's mqttPayloadProperty in TwoToneConfiguration, not mqttMessageProperty!
content = content.replace("mqttMessageProperty", "mqttPayloadProperty")

with open('src/main/java/io/github/dsheirer/gui/playlist/twotone/TwoToneEditor.java', 'w') as f:
    f.write(content)
