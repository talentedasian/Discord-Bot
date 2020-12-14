package botCommands.botMemberVoiceCommands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MoveMember extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ", 3);

        if ("!move".equals(message[0]) && "voice".equals(message[1]) && message.length == 3 && !event.getMember().getVoiceState().inVoiceChannel()) {
                event.getGuild().moveVoiceMember(event.getMember(), event.getGuild().getVoiceChannelsByName(message[2],true).get(0)).queue();
                event.getChannel().sendMessage("IF YOU ARE NOT MOVED TO A NEW VOICE CHANNEL, REPORT AN ISSUE TO **https://github.com/talentedasian/Discord-Bot or https://github.com/godsofheaven/Discord-Bot**")
                        .queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
                if (!event.getMember().getVoiceState().inVoiceChannel()) {
                    event.getChannel().sendMessage("**CONNECT** to a voice channel first").queue();
                }
        } else if ("!mute".equals(message[0]) && event.getMember().getRoles().contains(event.getGuild().getRolesByName("moderator", true).get(0))) {
                if (!event.getMessage().getMentionedMembers().get(0).getVoiceState().isMuted()) {
                    event.getGuild().mute(event.getMessage().getMentionedMembers().get(0), true).queue();
                    event.getChannel().sendMessage(":mute:")
                            .append(event.getMessage().getMentionedMembers().get(0).getAsMention() + " reason: ")
                            .append(message[message.length - 1])
                            .queue();
                } else if (!event.getMessage().getMentionedMembers().get(0).getVoiceState().inVoiceChannel()) {
                    event.getChannel().sendMessage(event.getMessage().getMentionedMembers().get(0).getAsMention() + "Not Yet in Voice Channel").queue();
                } else {
                    event.getChannel().sendMessage(event.getMessage().getMentionedMembers().get(0).getAsMention() + ":mute: already").queue();
                }
        }
    }
}
