package lavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class YoutubeSearch extends ListenerAdapter {

    private final DefaultAudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers;

    public YoutubeSearch() {
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        musicManagers = new HashMap<>();

    }

    private GuildMusicManager getGuildAudioPlayer(Guild guild)
    {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null)
        {
            synchronized (musicManagers)
            {
                mng = musicManagers.get(guildId);
                if (mng == null)
                {
                    mng = new GuildMusicManager(playerManager);
                    musicManagers.put(guildId, mng);
                }
            }
        }
        return mng;
    }



    

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ", 2);

        Guild guild = event.getGuild();
        long channelId = guild.getTextChannelsByName("music-room", true).get(0).getIdLong();
        TextChannel supposedChannel = event.getMessage().getTextChannel();
        if ("~playyt".equals(command[0]) && command.length == 2 && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              loadAndPlay(event.getChannel(), "ytsearch:" + command[1]);
            //player.setVolume(//i);
        } else if ("~playsc".equals(command[0]) && command.length == 2 && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              loadAndPlay(event.getChannel(), "scsearch:" + command[1]);
        } else if ("~skip".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              skipTrack(event.getChannel());
        } else if ("~pause".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              pauseTrack(event.getChannel());
        } else if ("~resume".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              resumeTrack(event.getChannel());
        } else if ("~stop".equals(command[0]) && "music".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              stopMusic(event.getChannel(), event.getGuild().getAudioManager());
        } else if ("~stop".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              stopTrack(event.getChannel());
        } else if ("~capacity".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              sendCapacity(event.getChannel());
        } else if ("~set".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              setVolume(event.getChannel(), Integer.parseInt(command[1]));
        } else if ("~volume".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              getVolume(event.getChannel());
        } else if ("~playing".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              getPlayingTrack(event.getChannel());
        } else if ("~repeat".equals(command[0]) && "off".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
              setRepeat(event.getChannel(), false);
        } else if ("~repeat".equals(command[0]) && "on".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
            setRepeat(event.getChannel(), true);
        } else if ("!list".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
            returnQueue(event.getChannel());
        }
        super.onGuildMessageReceived(event);
              }

    private void loadAndPlay(final TextChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());


        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                    play(channel.getGuild(), musicManager, track);

            }



            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();
                        play(channel.getGuild(), musicManager, firstTrack);
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

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track){
        connectToFirstVoiceChannel(guild.getAudioManager());

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

    private void setRepeat (TextChannel channel, boolean repeat) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.setRepeat(repeat);
        if (!musicManager.scheduler.isRepeat()) {
            channel.sendMessage("On Repeat OFF").queue();
        } else {
            channel.sendMessage("On Repeat ON").queue();
        }
    }

    private void returnQueue(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        TrackScheduler schedulers = musicManager.scheduler;
        BlockingQueue<AudioTrack> queue = schedulers.getQueue();
        synchronized (queue) {

            if (queue.isEmpty()) {
                channel.sendMessage("No tracks in queue").queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.BLUE);
                int trackCount = 0;
                for (AudioTrack track : queue) {
                   if (trackCount > 10) {
                        builder.addField(String.valueOf(track.getPosition())
                                ,   "Title: " + track.getInfo().title + "Singer: " + track.getInfo().author +
                                        "Playing? " + track.getState() + "Length: " + track.getInfo().length
                                , true);

                        trackCount++;
                   }
                }
                channel.sendMessage(builder.build()).queue();
            }
        }
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            long voiceId = audioManager.getGuild().getVoiceChannelsByName("Music Room",true).get(0).getIdLong();
            VoiceChannel voiceChannel = audioManager.getGuild().getVoiceChannelById(voiceId);
            audioManager.openAudioConnection(voiceChannel);
        }
    }





}




