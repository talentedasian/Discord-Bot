package lavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;

public class GuildMusicManager {
/** N
     * Audio player for the guild.
     */
    public DefaultAudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayerManager manager) {
        this.player = (DefaultAudioPlayer) manager.createPlayer();
        this.scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }


    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

}
