package io.github.dsheirer.module.decode.nbfm.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class AudioBufferManager {
    private static final int MAX_EVENTS = 5;
    private final LinkedList<List<float[]>> mAudioEvents = new LinkedList<>();
    private List<float[]> mCurrentEvent = null;

    public void startEvent() {
        synchronized (mAudioEvents) {
            mCurrentEvent = new ArrayList<>();
        }
    }

    public void addAudioSamples(float[] audioSamples) {
        synchronized (mAudioEvents) {
            if (mCurrentEvent != null) {
                // To avoid excessive memory usage, we limit the size of a single event's buffer
                // e.g., to ~10 seconds of audio. At 8kHz, float is 4 bytes.
                // Let's limit the list size to 1000 chunks for safety.
                if(mCurrentEvent.size() < 1000) {
                    mCurrentEvent.add(audioSamples);
                }
            }
        }
    }

    public void endEvent() {
        synchronized (mAudioEvents) {
            if (mCurrentEvent != null && !mCurrentEvent.isEmpty()) {
                mAudioEvents.addLast(mCurrentEvent);
                if (mAudioEvents.size() > MAX_EVENTS) {
                    mAudioEvents.removeFirst();
                }
            }
            mCurrentEvent = null;
        }
    }

    public List<List<float[]>> getBufferedEvents() {
        synchronized (mAudioEvents) {
            return new ArrayList<>(mAudioEvents);
        }
    }
}
