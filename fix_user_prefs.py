import re

with open('src/main/java/io/github/dsheirer/preference/UserPreferences.java', 'r') as f:
    content = f.read()

props = """    private PlaybackPreference mPlaybackPreference;
    private PlaylistPreference mPlaylistPreference;
    private RadioReferencePreference mRadioReferencePreference;
    private RecordPreference mRecordPreference;
    private TalkgroupFormatPreference mTalkgroupFormatPreference;
    private TunerPreference mTunerPreference;
    private VectorCalibrationPreference mVectorCalibrationPreference;
    private io.github.dsheirer.preference.mqtt.MqttPreference mMqttPreference;"""

content = re.sub(r'    private PlaybackPreference mPlaybackPreference;[\s\S]+?private VectorCalibrationPreference mVectorCalibrationPreference;', props, content)

load = """        mPlaylistPreference = new PlaylistPreference(this::receive, mDirectoryPreference);
        mRadioReferencePreference = new RadioReferencePreference(this::receive);
        mRecordPreference = new RecordPreference(this::receive);
        mTalkgroupFormatPreference = new TalkgroupFormatPreference(this::receive);
        mTunerPreference = new TunerPreference(this::receive);
        mVectorCalibrationPreference = new VectorCalibrationPreference(this::receive);
        mMqttPreference = new io.github.dsheirer.preference.mqtt.MqttPreference();"""

content = re.sub(r'        mPlaylistPreference = new PlaylistPreference\(this::receive, mDirectoryPreference\);[\s\S]+?mVectorCalibrationPreference = new VectorCalibrationPreference\(this::receive\);', load, content)


with open('src/main/java/io/github/dsheirer/preference/UserPreferences.java', 'w') as f:
    f.write(content)
