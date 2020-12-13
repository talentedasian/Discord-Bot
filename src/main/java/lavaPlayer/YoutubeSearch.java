package lavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class YoutubeSearch extends ListenerAdapter {

    private static DefaultAudioPlayerManager playerManager;
    private static Map<Long, GuildMusicManager> musicManagers;

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
        TextChannel supposedChannel = event.getMessage().getTextChannel();



        if ("~playyt".equals(command[0]) && command.length == 2 && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              loadAndPlay(event.getChannel(), "ytsearch: " + command[1]);
              event.getChannel().sendMessage("**Searching** :mag_right: for " + command[1]).queue();
        } else if ("~playsc".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              loadAndPlay(event.getChannel(), "scsearch: " + command[1]);
        } else if ("~skip".equals(command[0])  && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              skipTrack(event.getChannel());
        } else if ("~pause".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              pauseTrack(event.getChannel());
        } else if ("~resume".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              resumeTrack(event.getChannel());
        } else if ("~stop".equals(command[0]) && "music".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              stopMusic(event.getChannel(), event.getGuild().getAudioManager());
        } else if ("~stop".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              stopTrack(event.getChannel());
        } else if ("~set".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              setVolume(event.getChannel(), Integer.parseInt(command[1]));
        } else if ("~volume".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              getVolume(event.getChannel());
        } else if ("~playing".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              getPlayingTrack(event.getChannel());
        } else if ("~repeat".equals(command[0]) && "off".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
              setRepeat(event.getChannel(), false);
        } else if ("~repeat".equals(command[0]) && "on".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !getGuildAudioPlayer(guild).scheduler.isMass()) {
            setRepeat(event.getChannel(), true);
        } else if (command[0].startsWith("~") && !supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))){
            event.getChannel().sendMessage(":do_not_litter::exclamation: Go to " + event.getGuild().getTextChannelsByName("music-room",true).get(0).getAsMention()).queue();
        } else if ("~queue".equals(command[0]) && "contents".equals(command[1]) && !supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
            sendQueue(event.getChannel());
        }

        super.onGuildMessageReceived(event);
              }

    public void loadAndPlay(final TextChannel channel, final String trackUrl) {

        playerManager.loadItemOrdered(getGuildAudioPlayer(channel.getGuild()), trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding To Queue **" + track.getInfo().title + " ** by " + " **" + track.getInfo().author + "**").queue();

                    play(channel.getGuild(), track);

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding To Queue " + firstTrack.getInfo().title +  firstTrack.getInfo().title + " by **" + firstTrack.getInfo().author + "**").queue();
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


        try {
            if (!musicManagers.scheduler.getQueue().isEmpty()) {
                guild.getTextChannelsByName("music-room", true).get(0).sendMessage("There's a song that's currently playing").queue();
            } else {
                guild.getTextChannelsByName("music-room", true).get(0).sendMessage(":notes: **" + track.getInfo().title + "** by **" + track.getInfo().title + "  " + track.getInfo().length / 60000L + "minutes long**").queue();
                musicManagers.scheduler.queue(track);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        musicManagers.scheduler.resumeTrack();

    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.stopTrack();

        channel.sendMessage(":track_next: Track.").queue();
    }

    private void pauseTrack (TextChannel channel){
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.pauseTrack();

        channel.sendMessage(":pause_button: current track").queue();
    }
    private void resumeTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.resumeTrack();

        channel.sendMessage(":play_pause: paused track").queue();

    }
    private void stopTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.stopTrack();

        channel.sendMessage(":stop_button: current track").queue();

    }


    private void stopMusic (TextChannel channel, AudioManager manager) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());
        musicManagers.scheduler.stopTrack();
        manager.closeAudioConnection();

        channel.sendMessage("**Music** :octagonal_sign:").queue();

    }


    private void setVolume(TextChannel channel, int volume) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        musicManagers.scheduler.setVolume(volume);

        channel.sendMessage(":loud_sound: **Set** to " + musicManagers.scheduler.getVolume()).queue();
    }

    private void getVolume (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        channel.sendMessage(":loud_sound: is " + musicManagers.scheduler.getVolume()).queue();
    }

    private void getPlayingTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        AudioTrackInfo trackInfo = musicManagers.scheduler.getPlayingTrack().getInfo();
        channel.sendMessage("Current Playing Track is  :notes: **" + musicManagers.scheduler.getPlayingTrack()
        .getInfo().title + "** by **" + trackInfo.title + " by "+ musicManagers.scheduler.getPlayingTrack().getInfo() + musicManagers.scheduler.getPlayingTrack().getDuration()/60000 + " Long**").queue();
    }

    private void setRepeat (TextChannel channel, boolean repeat) {
            GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

            musicManager.scheduler.setRepeat(repeat);
        if (!repeat) {
            channel.sendMessage(":repeat: **OFF**").queue();
        } else {
            channel.sendMessage(":repeat: **ON**").queue();
        }
    }

    private void sendQueue (TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (AudioTrack tracks : queue) {
            if (count < 10) {
                builder.append("Place: " + tracks.getPosition())
                        .append("Title: " + tracks.getInfo().title)
                        .append("Author: " + tracks.getInfo().author)
                        .append("Length: " + tracks.getInfo().length)
                        .append("State: " +  tracks.getState())
                        .append("User Date" + tracks.getUserData());
               count++;
            }
        }
        channel.sendMessage(builder.toString()).queue();
    }


    private void connectToFirstVoiceChannel(AudioManager audioManager) {

        if (!audioManager.isConnected()) {
            long voiceId = audioManager.getGuild().getVoiceChannelsByName("Music Room",true).get(0).getIdLong();
            VoiceChannel voiceChannel = audioManager.getGuild().getVoiceChannelById(voiceId);
            audioManager.openAudioConnection(voiceChannel);
        }
    }





}




