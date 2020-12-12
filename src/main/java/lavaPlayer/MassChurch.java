package lavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MassChurch extends ListenerAdapter {

    private final DefaultAudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private final YoutubeSearch youtubeSearch = new YoutubeSearch();

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
        String message = event.getMessage().getContentRaw();
        String[] messages = event.getMessage().getContentRaw().split(" ", 4);

        if ("!masson".equals(message)) {
            setMass(event.getChannel(), true);
        } else if ("!massoff".equals(message)) {
            setMass(event.getChannel(), false);
        } else if ("!homily".equals(messages[0]) && "tayo".equals(messages[1]) && "ngayon".equals(messages[2]) && message.length() == 4) {
            youtubeSearch.loadAndPlay(event.getChannel(), "ytsearch:" + messages[4]);
            setMass(event.getChannel(),  true);
        }
    }

    private void setMass (TextChannel channel, boolean mass) {
        GuildMusicManager musicManagers = youtubeSearch.getGuildMusicManager(channel.getGuild());

        musicManagers.scheduler.setMass(mass);
        if (!mass) {
            channel.sendMessage("BAWAL NA MAG-INGAY **TANG INA** MISA NA").queue();
        } else {
            channel.sendMessage("MAG INGAY NA TAPOS NA ANG MISA **TANG INA**").queue();

        }
    }

}
