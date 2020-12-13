package botCommands.botMemberVoiceCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MoveMember extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] moveMessage = event.getMessage().getContentRaw().split(" ");
        long voiceChannelId = event.getGuild().getVoiceChannelsByName(moveMessage[2],true).get(0).getIdLong();
        long memberId = event.getMember().getIdLong();

        if ("!move".equals(moveMessage[0]) && "voice".equals(moveMessage[1])) {
                event.getGuild().moveVoiceMember(event.getGuild().getMemberById(memberId), event.getGuild().getVoiceChannelById(voiceChannelId)).queue();
                event.getChannel().sendMessage("IF YOU ARE NOT MOVED TO A NEW VOICE CHANNEL, REPORT AN ISSUE TO `https://github.com/talentedasian/Discord-Bot or https://github.com/godsofheaven/Discord-Bot`")
                        .queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
                event.getChannel().sendMessage("`CONNECT` to a voice channel first").queue();

        }
    }
}
