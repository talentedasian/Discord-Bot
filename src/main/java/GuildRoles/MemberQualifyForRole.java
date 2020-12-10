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
        String roleId = event.getGuild().getRolesByName(message[3], true).get(0).getId();
        Role role = event.getGuild().getRolesByName("Moderator", true).get(0);
        var channelName = event.getChannel().toString();
        if ("!role".equals(message[0]) && "request".equals(message[1]) && event.getChannel().toString().equalsIgnoreCase("role-room")) {
            event.getChannel().sendMessage("<@" + role + "> Check if he meets the criteria").queue();
            } else if ("!role".equals(message[0]) && "add".equals(message[1]) && channelName.equalsIgnoreCase("role-room")
                && (Objects.requireNonNull(event.getMember()).getRoles().contains(role) || event.getMember().isOwner())) {
            event.getGuild().addRoleToMember(event.getChannel().getAsMention(),event.getGuild().getRoleById(roleId)).queue();
            event.getChannel().sendMessage("Role Added You Can Now Enjoy The Benefits of Being a " + message[3].toUpperCase()).queue();
        } else if (message[0].equalsIgnoreCase("!role")  && !channelName.equalsIgnoreCase("role-room")) {
                event.getChannel().sendMessage("Please Go To The Correct Room For this Message")
                        .queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
                event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);

        }

    }
}


