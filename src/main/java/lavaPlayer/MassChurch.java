package lavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MassChurch extends ListenerAdapter {
    private final DefaultAudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public MassChurch() {
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        musicManagers = new HashMap<>();
    }
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] messages = event.getMessage().getContentRaw().split(" ", 2);
        String[] massMessage = event.getMessage().getContentRaw().split(" ", 4);
        TextChannel supposedChannel = event.getChannel();
        if ("!masson".equals(messages[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
            BooleanMass.settingMass(event.getChannel(),  true);
        } else if ("!massoff".equals(messages[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
            BooleanMass.settingMass(event.getChannel(), false);
        } else if ("!homily".equals(massMessage[0]) && "tayo".equals(massMessage[1]) && "ngayon".equals(massMessage[2]) && massMessage.length == 4
                && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
            loadAndPlay(event.getChannel(), "ytsearch:" + messages[1]);
            BooleanMass.settingMass(event.getChannel(),  true);
            event.getChannel().sendMessage(":pray: :prayer_beads: **PRAYER TAYO NA NGAYON SA :church: TANG INA WAG MAINGAY**").queue();
        } else if (event.getMessage().getChannel().equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && BooleanMass.isMass()
                && !event.getMember().isOwner() && !event.getAuthor().isBot() && event.getMember().getPermissions().contains(Permission.PRIORITY_SPEAKER)) {
            event.getMessage().delete().queueAfter(4, TimeUnit.SECONDS);
            event.getChannel().sendMessage("**BAWAS POINTS KA SA :church: :cloud:** " +  event.getMember().getAsMention() + "**NAGMIMISA TAYO EH TANG INA NAMAN OH**").queue();
        }
    }
    public void loadAndPlay(final TextChannel channel, final String trackUrl) {

        playerManager.loadItemOrdered(getGuildAudioPlayer(channel.getGuild()), trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage(":heavy_plus_sign: Mass Upcoming **" + track.getInfo().title + " ** by " + " **JESUS**").queue();

                play(channel.getGuild(), track);

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage(":heavy_plus_sign: Mass Upcoming **" + firstTrack.getInfo().title + " ** by " + " **JESUS**").queue();
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

        guild.getTextChannelsByName("music-room",true).get(0).sendMessage("**DUMATING NA ANG PARI TUMAHIMIK NA TANG INA**").queue();

            musicManagers.scheduler.queue(track);

        musicManagers.scheduler.resumeTrack();

    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {

        if (!audioManager.isConnected()) {
            long voiceId = audioManager.getGuild().getVoiceChannelsByName("Music Room",true).get(0).getIdLong();
            VoiceChannel voiceChannel = audioManager.getGuild().getVoiceChannelById(voiceId);
            audioManager.openAudioConnection(voiceChannel);
        }
    }
}
