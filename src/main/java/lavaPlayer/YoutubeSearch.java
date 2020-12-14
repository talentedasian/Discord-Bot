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

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        TextChannel supposedChannel = event.getMessage().getTextChannel();



        if ("~playyt".equals(command[0]) && command.length == 2 && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                loadAndPlay(event.getChannel(), "ytsearch: " + command[1]);
                event.getChannel().sendMessage("**Jesus Searching** :mag_right: for " + command[1]).queue();
        } else if ("~playsc".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                loadAndPlay(event.getChannel(), "scsearch: " + command[1]);
        } else if ("~skip".equals(command[0])  && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                skipTrack(event.getChannel());
        } else if ("~pause".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                pauseTrack(event.getChannel());
        } else if ("~resume".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                resumeTrack(event.getChannel());
        } else if ("~stop".equals(command[0]) && "music".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                stopMusic(event.getChannel(), event.getGuild().getAudioManager());
        } else if ("~stop".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                stopTrack(event.getChannel());
        } else if ("~set".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                setVolume(event.getChannel(), Integer.parseInt(command[1]));
        } else if ("~volume".equals(command[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                getVolume(event.getChannel());
        } else if ("~playing".equals(command[0]) && "track".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                getPlayingTrack(event.getChannel());
        } else if ("~repeat".equals(command[0]) && "off".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                setRepeat(event.getChannel(), false);
        } else if ("~repeat".equals(command[0]) && "on".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && !BooleanMass.isMass()) {
                setRepeat(event.getChannel(), true);
        } else if (command[0].startsWith("~") && !supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))){
                event.getChannel().sendMessage(":do_not_litter::exclamation: Go to " + event.getGuild().getTextChannelsByName("music-room",true).get(0).getAsMention()).queue();
        } else if ("~queue".equals(command[0]) && "contents".equals(command[1]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
                sendQueue(event.getChannel());
        }


        super.onGuildMessageReceived(event);
              }

    public void loadAndPlay(final TextChannel channel, final String trackUrl) {

        playerManager.loadItemOrdered(getGuildAudioPlayer(channel.getGuild()), trackUrl, new AudioLoadResultHandler() {


            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Jesus Adding **")
                        .append(track.getInfo().title)
                        .append(" ** by ")
                        .append(track.getInfo().author)
                        .append("** :stopwatch:" + TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length))
                        .append(" and in case you want to search it up on youtube ")
                        .append(track.getInfo().uri + "**")
                        .queue();
                    play(channel.getGuild(), track);

            }


            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Jesus Adding **")
                        .append(firstTrack.getInfo().title)
                        .append(" ** by **")
                        .append(firstTrack.getInfo().author)
                        .append("** :stopwatch:" + TimeUnit.MILLISECONDS.toMinutes(firstTrack.getInfo().length))
                        .append(" and in case you want to search it up on youtube ")
                        .append(firstTrack.getInfo().uri + "**")
                        .queue();
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

        if (musicManagers.scheduler.getQueue().isEmpty()) {
                guild.getTextChannelsByName("music-room", true).get(0).sendMessage(":notes: **")
                        .append(track.getInfo().title)
                        .append("** by ** ")
                        .append(track.getInfo().author + " ")
                        .append(TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length) + " :stopwatch:**")
                        .queue();
            } else if (!musicManagers.scheduler.getQueue().isEmpty()) {
                guild.getTextChannelsByName("music-room", true).get(0).sendMessage("**")
                        .append(musicManagers.scheduler.getPlayingTrack().getInfo().title)
                        .append("** is Currently Playing")
                        .append(" wait for your song to be played by jesus ")
                        .queue();
            }

            musicManagers.scheduler.resumeTrack();

    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        if (!musicManagers.scheduler.getQueue().isEmpty()) {
            musicManagers.scheduler.stopTrack();
            channel.sendMessage(":track_next: Track.").queue();
        } else {
            channel.sendMessage("No track is playing for jesus to skip").queue();
        }
    }

    private void pauseTrack (TextChannel channel){
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());
        if (!musicManagers.player.isPaused()) {
            musicManagers.scheduler.pauseTrack();
            channel.sendMessage(":pause_button: current track").queue();
        } else if (musicManagers.scheduler.getQueue().isEmpty()) {
            channel.sendMessage("No track is playing for jesus to :pause_button:").queue();
        } else {
            channel.sendMessage("Track is currently :pause_button: by jesus.").queue();
        }

    }
    private void resumeTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());
        if (musicManagers.player.isPaused()) {
            musicManagers.scheduler.resumeTrack();
            channel.sendMessage(":play_pause: paused track").queue();
        } else if (musicManagers.scheduler.getQueue().isEmpty()) {
            channel.sendMessage("No track is playing for jesus to resume").queue();
        } else {
            channel.sendMessage("Track is currently playing by jesus.").queue();
        }


    }
    private void stopTrack (TextChannel channel) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());
        if (!musicManagers.scheduler.getQueue().isEmpty()) {
            musicManagers.scheduler.stopTrack();
            channel.sendMessage(":stop_button: current track").queue();
        } else {
            channel.sendMessage("No track is playing for jesus to :stop_button:").queue();
        }


    }


    private void stopMusic (TextChannel channel, AudioManager manager) {
        GuildMusicManager musicManagers = getGuildAudioPlayer(channel.getGuild());

        if (!musicManagers.scheduler.getQueue().isEmpty()) {
            musicManagers.scheduler.stopTrack();
            manager.closeAudioConnection();
            channel.sendMessage("**Music** :octagonal_sign:").queue();
        } else {
            channel.sendMessage("No track is playing for jesus to :octagonal_sign:").queue();
        }


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
        if (!musicManagers.scheduler.getQueue().isEmpty()) {
            channel.sendMessage("Current Playing Track is  :notes: **")
                .append(trackInfo.title)
                .append("** by **")
                .append(trackInfo.author)
                .append(" ")
                .append(TimeUnit.MILLISECONDS.toMinutes(trackInfo.length) + " :stopwatch:**")
                .queue();
        } else  {
            channel.sendMessage("No track to display information").queue();
        }
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
        Queue<AudioTrack> queue = musicManager.scheduler.getQueue();
        StringBuilder response = new StringBuilder();
        int count = 0;
        synchronized (queue) {
            for (AudioTrack tracks : queue) {
                if (count < 10) {
                    response.append("```Place: " + tracks.getPosition())
                            .append("\nTitle: " + tracks.getInfo().title)
                            .append("\nAuthor: " + tracks.getInfo().author)
                            .append("\nLength: " + tracks.getInfo().length + " Minutes```");
                    count++;
                }
            }
        }
        channel.sendMessage(response.toString()).queue();


    }


    private void connectToFirstVoiceChannel(AudioManager audioManager) {

        if (!audioManager.isConnected()) {
            long voiceId = audioManager.getGuild().getVoiceChannelsByName("Music Room",true).get(0).getIdLong();
            VoiceChannel voiceChannel = audioManager.getGuild().getVoiceChannelById(voiceId);
            audioManager.openAudioConnection(voiceChannel);
        }
    }





}




