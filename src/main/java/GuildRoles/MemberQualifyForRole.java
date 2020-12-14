package GuildRoles;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MemberQualifyForRole extends ListenerAdapter {



    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
        long moderatorId = event.getGuild().getRolesByName("moderator",true).get(0).getIdLong();
        TextChannel channelName = event.getGuild().getTextChannelsByName("role-room", true).get(0);

        Role roleAssign = event.getGuild().getRolesByName(String.valueOf(message[message.length-1]), true).get(0);
        if ("role".equals(message[0]) && "request".equals(message[1]) && event.getChannel().equals(channelName)) {
                event.getChannel().sendMessage(event.getGuild().getRoleById(moderatorId).getAsMention() +
                        " Please Check if " + event.getMember().getAsMention() +  " he meets the criteria of being a ***" + message[message.length-1] +  "***").queue();
        } else if ("role".equals(message[0]) && "add".equals(message[1]) && event.getChannel().equals(channelName)
                && (event.getMember().isOwner() || event.getMember().getPermissions().contains(Permission.MANAGE_ROLES))) {
            event.getGuild().addRoleToMember(event.getMessage().getMentionedMembers().get(0), roleAssign).queue();
            event.getChannel().sendMessage("Congratulations " + event.getMessage().getMentionedMembers().get(0).getAsMention() + ",You are now a ***" + message[message.length-1].toUpperCase() + "***")
                    .queue();
        } else if ("role".equals(message[0]) && "remove".equals(message[1]) && event.getChannel().equals(channelName)
                && (event.getMember().isOwner() || event.getMember().getPermissions().contains(Permission.MANAGE_ROLES))) {
            event.getGuild().removeRoleFromMember(event.getMessage().getMentionedMembers().get(0), roleAssign).queue();
            event.getChannel().sendMessage(event.getMessage().getMentionedMembers().get(0).getAsMention() + "You Have Been Stripped of off Being a ***" + message[message.length-1].toUpperCase() + "***")
                    .queue();
        } else if (message[0].equalsIgnoreCase("role")  && !event.getChannel().equals(channelName)) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessage("***Please Go To The Correct Room For this Message***")
                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
        } else if ("roles".equals(message[0])) {
            event.getGuild().getMember(event.getMessage().getMentionedMembers().get(0).getUser()).getRoles().stream().forEach(role -> event.getChannel().sendMessage("Role: " + role.getName() + " \n").queue());
        }

    }
}


