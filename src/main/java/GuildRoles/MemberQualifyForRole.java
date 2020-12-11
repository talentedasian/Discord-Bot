package GuildRoles;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MemberQualifyForRole extends ListenerAdapter {





    public MemberQualifyForRole () {

    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
        long moderator = event.getGuild().getRolesByName("Moderator", true).get(0).getIdLong();
        Role moderatorRole = event.getGuild().getRoleById(moderator);

        TextChannel channelName = event.getGuild().getTextChannelsByName("role-room", true).get(0);

        long mentionedMemberId = event.getMessage().getMentionedMembers().get(0).getIdLong();
        Member mentionedMember = event.getChannel().getGuild().getMemberById(mentionedMemberId);
        long roleAssign = event.getGuild().getRolesByName(message[3], true).get(0).getIdLong();
        Role role = event.getGuild().getRoleById(roleAssign);

        if ("!role".equals(message[0]) && "request".equals(message[1]) && event.getChannel().equals(channelName)) {
            event.getChannel().sendMessage( moderatorRole.getAsMention() + " Please Check if" + event.getMember().getAsMention() +  "he meets the criteria of being a ***" + message[2] + "***").queue();
        } else if ("!role".equals(message[0]) && "add".equals(message[1]) && event.getChannel().equals(channelName)
                && (Objects.requireNonNull(event.getMember()).getRoles().contains(role) || event.getMember().isOwner())) {
            event.getGuild().addRoleToMember(mentionedMember, event.getGuild().getRoleById(roleAssign)).queue();
            event.getChannel().sendMessage("Congratulations " + mentionedMember.getAsMention() + ",You are now a ***" + message[3].toUpperCase() + "***")
                    .queue();
        }   else if ("!role".equals(message[0]) && "remove".equals(message[1]) && event.getChannel().equals(channelName)
                && (Objects.requireNonNull(event.getMember()).getRoles().contains(moderatorRole) || event.getMember().isOwner())   ) {
            event.getGuild().removeRoleFromMember(mentionedMember, role).queue();
            event.getChannel().sendMessage(mentionedMember.getAsMention() + "You Have Been Stripped of off Being a ***" + message[3].toUpperCase() + "***")
                    .queue();
        }
        else if (message[0].equalsIgnoreCase("!role")  && !event.getChannel().equals(channelName)) {
            event.getChannel().sendMessage("Please Go To The Correct Room For this Message")
                    .queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);

        }

    }
}


