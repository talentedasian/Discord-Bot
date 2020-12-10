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
        List<VoiceChannel> voiceChannels = event.getGuild().getVoiceChannels();
        int random = new Random().nextInt(voiceChannels.toArray().length);



        Member memberToBeMoved = event.getMember();
        String[] moveMessage = event.getMessage().getContentRaw().split(" ");
        if ("!move".equals(moveMessage[0]) && "voice".equals(moveMessage[1])) {
            try {
                assert memberToBeMoved != null;
                event.getGuild().moveVoiceMember(memberToBeMoved, event.getGuild().getVoiceChannelsByName(moveMessage[2],true).get(0)).queue();
                event.getChannel().sendMessage("IF YOU ARE NOT MOVED TO A NEW VOICE CHANNEL, REPORT AN ISSUE TO `https://github.com/talentedasian/Discord-Bot or https://github.com/godsofheaven/Discord-Bot`")
                        .queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
            } catch (IllegalStateException e) {
                event.getChannel().sendMessage("`CONNECT` to a voice channel first").queue();
            }

        }
    }
}
