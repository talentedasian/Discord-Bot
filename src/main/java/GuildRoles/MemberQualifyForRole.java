package GuildRoles;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MemberQualifyForRole extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
        TextChannel channelName = event.getGuild().getTextChannelsByName("role-room", true).get(0);
        if ("!role".equals(message[0]) && "request".equals(message[1]) && channelName.equals(event.getChannel())) {
            String roleId = event.getGuild().getRolesByName(message[2], true).get(0).getId();
            event.getChannel().sendMessage("<@Moderator> Check if he meets the criteria").queue();
        } else if ("!role".equals(message[0]) && "add".equals(message[1]) && channelName.equals(event.getChannel())) {
            event.getGuild().addRoleToMember(event.getGuild().getMembersByName(Arrays.stream(message)
                    .findFirst().get(),true).get(0)
                    , event.getGuild().getRolesByName(message[-1],true).get(0)).queue();
            event.getChannel().sendMessage("Role Added").queue();
        } else if (message[0].equalsIgnoreCase("!role")  && !channelName.equals(event.getChannel())) {
                event.getChannel().sendMessage("Please Go To The Correct Room For this Message")
                        .queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
                event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);

        }

    }
}


