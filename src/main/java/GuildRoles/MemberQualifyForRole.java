package GuildRoles;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MemberQualifyForRole extends ListenerAdapter {



    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");

        long moderatorId = event.getGuild().getRolesByName("moderator",true).get(0).getIdLong();
        TextChannel channelName = event.getGuild().getTextChannelsByName("role-room", true).get(0);

        Role roleAssign = event.getGuild().getRolesByName(String.valueOf(message[message.length-1]), true).get(0);
        if ("!role".equals(message[0]) && "request".equals(message[1]) && event.getChannel().equals(channelName)) {
                event.getChannel().sendMessage(event.getGuild().getRoleById(moderatorId).getAsMention() +
                        " Please Check if " + event.getMember().getAsMention() +  " he meets the criteria of being a ***" + message[message.length-1] +  "***").queue();
        } else if ("!role".equals(message[0]) && "add".equals(message[1]) && event.getChannel().equals(channelName)
                && (Objects.requireNonNull(event.getMember()).getRoles().contains(event.getGuild().getRoleById(moderatorId)) || event.getMember().isOwner())) {
            event.getGuild().modifyMemberRoles(event.getMessage().getMentionedMembers().get(0), roleAssign).queue();
            event.getChannel().sendMessage("Congratulations " + event.getMessage().getMentionedMembers().get(0) + ",You are now a ***" + message[message.length-1].toUpperCase() + "***")
                    .queue();
        } else if ("!role".equals(message[0]) && "remove".equals(message[1]) && event.getChannel().equals(channelName)
                && (Objects.requireNonNull(event.getMember()).getRoles().contains(event.getGuild().getRoleById(moderatorId)) || event.getMember().isOwner())   ) {
            event.getGuild().removeRoleFromMember(event.getMessage().getMentionedMembers().get(0), roleAssign).queue();
            event.getChannel().sendMessage(event.getGuild().getRoleById(moderatorId).getAsMention() + "You Have Been Stripped of off Being a ***" + message[message.length-1].toUpperCase() + "***")
                    .queue();
        } else if ("!role".equals(message[0]) && "add".equals(message[1]) && "everyone".equals(message[2]) && event.getChannel().equals(channelName)
                && (Objects.requireNonNull(event.getMember()).getRoles().contains(event.getGuild().getRoleById(moderatorId)) || event.getMember().isOwner())) {
                event.getGuild().getMembers().forEach(m -> event.getGuild().modifyMemberRoles
                        (event.getGuild().getMemberById(m.getIdLong()), event.getMessage().getMentionedRoles()));
        }
        else if (message[0].equalsIgnoreCase("!role")  && !event.getChannel().equals(channelName)) {
            event.getChannel().sendMessage("Please Go To The Correct Room For this Message")
                    .queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);

        }

    }
}


