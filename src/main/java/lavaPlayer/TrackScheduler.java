package lavaPlayer;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
    private final DefaultAudioPlayer player;
    private final Queue<AudioTrack> queue;
    AudioTrack lastTrack;

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    private boolean repeat;
    private boolean repeatQueue;

    public boolean isRepeat() {
		return repeat;
	}
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	public boolean isRepeatQueue() {
		return repeatQueue;
	}
	public void setRepeatQueue(boolean repeatQueue) {
		this.repeatQueue = repeatQueue;
	}
	/**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(DefaultAudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();

    }
    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track){
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true) && !(queue.size() == 10)) {
            queue.offer(track);
        }
    }




    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    public void pauseTrack(){
        //pause the played track
        player.setPaused(true);
    }

    public void resumeTrack() {
        //resume the paused track
        player.setPaused(false);
    }


    public void stopTrack() {
        //stop track
        player.stopTrack();
        player.playTrack(queue.poll());
    }



    public void setVolume (int volume) {
        player.setVolume(volume);
    }

    public int getVolume () { return player.getVolume(); }

    public AudioTrack getPlayingTrack() { return player.getPlayingTrack(); }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        this.lastTrack = track;
        if (endReason.mayStartNext) {
            if (isRepeat()) {
                player.startTrack(lastTrack.makeClone(),false);
            } else if (isRepeatQueue()) {
            	queue.offer(lastTrack.makeClone());
            	if (endReason.equals(AudioTrackEndReason.STOPPED)) {
            		queue.offer(lastTrack.makeClone());
            		nextTrack();
            	}
            	nextTrack();
            } else {
            	nextTrack();
            }
        }
    }
}
