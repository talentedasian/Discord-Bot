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
    private final Map<Long, GuildMusicManager> musicManagers;

    public YoutubeSearch() {
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        musicManagers = new HashMap<>();
    }




    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
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
        } else if ("~list".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelById(channelId))) {
            returnQueue(event.getChannel());
        }
        super.onGuildMessageReceived(event);
              }

    private void loadAndPlay(final TextChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());


        playerManager.loadItemOrdered(getGuildAudioPlayer(channel.getGuild()), trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                    play(channel.getGuild(), track);

            }



            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();
                        play(channel.getGuild(), firstTrack);
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

    private void play(Guild guild, AudioTrack track){
        connectToFirstVoiceChannel(guild.getAudioManager());

        GuildMusicManager musicManagers = getGuildAudioPlayer(guild);

        musicManagers.scheduler.queue(track);
        musicManagers.scheduler.resumeTrack();
    }




    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.stopTrack();


        channel.sendMessage("Skipped to next track.").queue();
    }

    private void pauseTrack (TextChannel channel){
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.pauseTrack();

        channel.sendMessage("Paused current track").queue();
    }
    private void resumeTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.resumeTrack();

        channel.sendMessage("Resumed paused track").queue();

    }
    private void stopTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.stopTrack();

        channel.sendMessage("Stopped current track").queue();

    }


    private void stopMusic (TextChannel channel, AudioManager manager) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());
        musicManagers.scheduler.stopTrack();
        manager.closeAudioConnection();

        channel.sendMessage("Music was stopped").queue();

    }

    private void sendCapacity(TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        channel.sendMessage("Remaining Songs to Put: " + musicManagers.scheduler.sendQueueRemainingCapacity()).queue();
    }

    private void setVolume(TextChannel channel, int volume) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.setVolume(volume);

        channel.sendMessage("Volume Set to " + musicManagers.scheduler.getVolume()).queue();
    }

    private void getVolume (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        channel.sendMessage("Current Volume is " + musicManagers.scheduler.getVolume()).queue();
    }

    private void getPlayingTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        channel.sendMessage("Current Playing Track is " + musicManagers.scheduler.getPlayingTrack()
        .getInfo().title + "by " + musicManagers.scheduler.getPlayingTrack().getInfo().title + "." + musicManagers.scheduler.getPlayingTrack().getInfo().length + " Remaining").queue();
    }

    private void setRepeat (TextChannel channel, boolean repeat) {
            GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        if (musicManager.scheduler.isRepeat()) {
            channel.sendMessage("On Repeat OFF").queue();
        } else {
            channel.sendMessage("On Repeat ON").queue();
        }
    }

    private void returnQueue(TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        TrackScheduler schedulers = musicManagers.scheduler;
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




