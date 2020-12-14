package botCommands.botMemberVoiceCommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MuteMembers extends ListenerAdapter {


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);

        String[] message = event.getMessage().getContentRaw().split(" ", 3);

        if (".mute".equals(message[0]) && event.getMember().getPermissions().contains(Permission.VOICE_MUTE_OTHERS) || event.getMember().isOwner()) {
            if (!event.getMessage().getMentionedMembers().get(0).getVoiceState().isMuted()) {
                event.getGuild().mute(event.getMessage().getMentionedMembers().get(0), true).queue();
                event.getChannel().sendMessage(":mute:")
                        .append(event.getMessage().getMentionedMembers().get(0).getAsMention() + " reason: ")
                        .append(message[message.length - 1])
                        .queue();
            } else if (!event.getMessage().getMentionedMembers().get(0).getVoiceState().inVoiceChannel()) {
                event.getChannel().sendMessage(event.getMessage().getMentionedMembers().get(0).getAsMention() + "User Not Yet in Voice Channel").queue();
            } else {
                event.getChannel().sendMessage(event.getMessage().getMentionedMembers().get(0).getAsMention() + ":mute: already").queue();
            }
        } else if (message[0].startsWith(".") && event.getMember().getPermissions().contains(Permission.VOICE_MUTE_OTHERS)) {
            event.getChannel().sendMessage("Only those who are not sinned are allowed to punish others").queue();
        } else if (".unmute".equals(message[0]) && event.getMember().getPermissions().contains(Permission.VOICE_MUTE_OTHERS)) {
            if (event.getMessage().getMentionedMembers().get(0).getVoiceState().isMuted()) {
                event.getGuild().mute(event.getMessage().getMentionedMembers().get(0), false).queue();
                event.getChannel().sendMessage(":sound:")
                        .append(event.getMessage().getMentionedMembers().get(0).getAsMention() + " reason: ")
                        .append(message[message.length - 1])
                        .queue();
            } else if (!event.getMessage().getMentionedMembers().get(0).getVoiceState().inVoiceChannel()) {
                event.getChannel().sendMessage(event.getMessage().getMentionedMembers().get(0).getAsMention() + "User Not Yet in Voice Channel").queue();
            } else if (event.getMember().getPermissions().contains(Permission.VOICE_MUTE_OTHERS) || event.getMember().isOwner()) {
                event.getChannel().sendMessage("Only those who are not sinned are allowed to punish others").queue();
            } else {
                event.getChannel().sendMessage("already :sound:").queue();
            }
        }
    }
}
