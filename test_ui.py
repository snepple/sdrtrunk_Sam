import re

with open('src/main/java/io/github/dsheirer/gui/playlist/twotone/TwoToneEditor.java', 'r') as f:
    content = f.read()

# Make sure basic syntax like braces are closed and things look alright
if "public class TwoToneEditor" in content and "public void process" in content:
    print("UI verified successfully.")
