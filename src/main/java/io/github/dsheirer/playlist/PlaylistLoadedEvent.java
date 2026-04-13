package io.github.dsheirer.playlist;

public class PlaylistLoadedEvent {
    private final PlaylistV2 mPlaylist;

    public PlaylistLoadedEvent(PlaylistV2 playlist) {
        mPlaylist = playlist;
    }

    public PlaylistV2 getPlaylist() {
        return mPlaylist;
    }
}
