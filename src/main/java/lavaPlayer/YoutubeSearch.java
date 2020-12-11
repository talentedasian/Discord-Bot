package lavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.w3c.dom.Text;

import javax.sound.midi.Track;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;

public class YoutubeSearch extends ListenerAdapter {

    private final DefaultAudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final DefaultAudioPlayer player;


    public YoutubeSearch() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        this.player = new DefaultAudioPlayer(playerManager);

    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(player);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ", 2);

        Guild guild = event.getGuild();
        long channelId = guild.getTextChannelsByName("music-room", true).get(0).getIdLong();
        TextChannel channel = guild.getTextChannelById(channelId);
        TextChannel supposedChannel = event.getMessage().getTextChannel();
        if ("~playyt".equals(command[0]) && command.length == 2 && supposedChannel.equals(channel)) {
              loadAndPlay(event.getChannel(), "ytsearch:" + command[1], false);
            //player.setVolume(//i);
        } else if ("~playsc".equals(command[0]) && command.length == 2 && supposedChannel.equals(channel)) {
              loadAndPlay(event.getChannel(), "scsearch:" + command[1], false);
        } else if ("~skip".equals(command[0]) && supposedChannel.equals(channel)) {
              skipTrack(event.getChannel());
        } else if ("~pause".equals(command[0]) && supposedChannel.equals(channel)) {
              pauseTrack(event.getChannel());
        } else if ("~resume".equals(command[0]) && supposedChannel.equals(channel)) {
              resumeTrack(event.getChannel());
        } else if ("~stop".equals(command[0]) && "music".equals(command[1]) && supposedChannel.equals(channel)) {
              stopMusic(event.getChannel(), event.getGuild().getAudioManager());
        } else if ("~stop".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(channel)) {
              stopTrack(event.getChannel());
        } else if ("~capacity".equals(command[0]) && supposedChannel.equals(channel)) {
              sendCapacity(event.getChannel());
        } else if ("~set".equals(command[0]) && supposedChannel.equals(channel)) {
              setVolume(event.getChannel(), Integer.parseInt(command[1]));
        } else if ("~volume".equals(command[0]) && supposedChannel.equals(channel)) {
              getVolume(event.getChannel());
        } else if ("~playing".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(channel)) {
              getPlayingTrack(event.getChannel());
        } else if ("~playytrepeat".equals(command[0])  && command.length == 2 && supposedChannel.equals(channel)) {
              loadAndPlay(event.getChannel(), "ytsearch:" + command[1], true);
        }

        super.onGuildMessageReceived(event);
              }

    private void loadAndPlay(final TextChannel channel, final String trackUrl, boolean repeat) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());


        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                try {
                    if (repeat == false) {
                    playNoRepeat(channel.getGuild(), musicManager, track);
                    } else {
                        playWithRepeat(channel.getGuild(), musicManager, track);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }



            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                try {
                    if (repeat == false) {
                        playNoRepeat(channel.getGuild(), musicManager, firstTrack);
                    } else if (repeat){
                        playWithRepeat(channel.getGuild(), musicManager, firstTrack);
                        channel.sendMessage("You Chose To Repeat The Song").queue();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void playWithRepeat(Guild guild, GuildMusicManager musicManager, AudioTrack track) throws InterruptedException {
        connectToFirstVoiceChannel(guild.getAudioManager());

            musicManager.scheduler.setRepeat(true);
            musicManager.scheduler.queue(track);
            musicManager.scheduler.resumeTrack();

    }
    private void playNoRepeat(Guild guild, GuildMusicManager musicManager, AudioTrack track) throws InterruptedException {
        connectToFirstVoiceChannel(guild.getAudioManager());

        musicManager.scheduler.setRepeat(false);
        musicManager.scheduler.queue(track);
        musicManager.scheduler.resumeTrack();

    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();


        channel.sendMessage("Skipped to next track.").queue();
    }

    private void pauseTrack (TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.pauseTrack();

        channel.sendMessage("Paused current track").queue();
    }
    private void resumeTrack (TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.resumeTrack();

        channel.sendMessage("Resumed paused track").queue();

    }
    private void stopTrack (TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.stopTrack();

        channel.sendMessage("Stopped current track").queue();

    }


    private void stopMusic (TextChannel channel, AudioManager manager) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.stopTrack();
        manager.closeAudioConnection();

        channel.sendMessage("Music was stopped").queue();

    }

    private void sendCapacity(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        channel.sendMessage("Remaining Songs to Put: " + musicManager.scheduler.sendQueueRemainingCapacity()).queue();
    }

    private void setVolume(TextChannel channel, int volume) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.setVolume(volume);
        channel.sendMessage("Volume Set to " + musicManager.scheduler.getVolume()).queue();
    }

    private void getVolume (TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        channel.sendMessage("Current Volume is " + musicManager.scheduler.getVolume()).queue();
    }

    private void getPlayingTrack (TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        channel.sendMessage("Current Playing Track is " + musicManager.scheduler.getPlayingTrack()
        .getInfo().title).queue();
    }











    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            long voiceId = audioManager.getGuild().getVoiceChannelsByName("Music Room",true).get(0).getIdLong();
            VoiceChannel voiceChannel = audioManager.getGuild().getVoiceChannelById(voiceId);
            audioManager.openAudioConnection(voiceChannel);
        }
    }
}




