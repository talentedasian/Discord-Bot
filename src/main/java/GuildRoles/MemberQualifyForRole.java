package GuildRoles;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MemberAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MemberQualifyForRole extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        List<Role> roles = event.getGuild().getRoles();
        String[] message = event.getMessage().getContentRaw().split(" ");
        List<TextChannel>  channelName = event.getGuild().getTextChannelsByName("role-room", true);
        Member user =event.getMember();
        if ("!role".equals(message[0]) && "request".equals(message[1]) && channelName.stream().findFirst().equals(event.getChannel())) {
            List<TextChannel> channels = event.getGuild().getTextChannels();
            CompletableFuture<List<Message>> allMessages;
            for (TextChannel channelAll : channels) {
                allMessages = channelAll.getIterableHistory().takeAsync(1000)
                        .thenApply(item -> item.stream().filter( items -> items.getAuthor().equals(user)).collect(Collectors.toList()));
                try {
                    int sizeOfMessages = allMessages.get().size();
                    if (sizeOfMessages == 500) {
                        Role role = roles.stream().filter(m -> m.equals(message[3])).collect(Collectors.toList()).get(0);
                        event.getGuild().addRoleToMember(user, role);
                    } else {
                        event.getChannel().sendMessage("Not Yet Qualified!").queue();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }





        } else if (!channelName.equals(event.getChannel())) {
             TextChannel channel = event.getChannel();
             channel.sendMessage("Sorry you are not yet qualified").queue(m -> m.delete().queueAfter(2, TimeUnit.MINUTES));
        }

    }
}
