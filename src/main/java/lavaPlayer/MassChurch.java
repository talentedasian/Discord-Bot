package lavaPlayer;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
public class MassChurch extends ListenerAdapter {

    private final YoutubeSearch youtubeSearch = new YoutubeSearch();

    public MassChurch() {
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] messages = event.getMessage().getContentRaw().split(" ", 4);

        TextChannel supposedChannel = event.getChannel();
        if ("!masson".equals(messages[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
            setMass(event.getChannel(), true);
        } else if ("!massoff".equals(messages[0]) && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
            setMass(event.getChannel(), false);
        } else if ("!homily".equals(messages[0]) && "tayo".equals(messages[1]) && "ngayon".equals(messages[2]) && messages.length == 4 && supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0))) {
            youtubeSearch.loadAndPlay(event.getChannel(), "ytsearch:" + messages[3]);
            setMass(event.getChannel(),  true);
            event.getChannel().sendMessage(":pray: :prayer_beads: **PRAYER TAYO NA NGAYON SA :church: TANG INA WAG MAINGAY**").queue();
        } else if (supposedChannel.equals(event.getGuild().getTextChannelsByName("music-room", true).get(0)) && youtubeSearch.getGuildMusicManager(event.getGuild()).scheduler.isMass()
                && !event.getMember().isOwner() && event.getAuthor().isBot()) {
            event.getChannel().sendMessage("**BAWAS POINTS KA SA :church: :cloud:** " +  event.getMember().getAsMention()).queue();
        }
    }

    private void setMass (TextChannel channel, boolean mass) {
        GuildMusicManager musicManagers = youtubeSearch.getGuildMusicManager(channel.getGuild());

        musicManagers.scheduler.setMass(mass);
        if (mass) {
            channel.sendMessage("BAWAL NA MAG-INGAY **TANG INA** MISA NA").queue();
        } else {
            channel.sendMessage("MAG INGAY NA TAPOS NA ANG MISA **TANG INA** NEXT WEEK NAMAN").queue();

        }
    }

}
