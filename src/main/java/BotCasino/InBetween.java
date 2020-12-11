package BotCasino;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Random;

public class InBetween extends ListenerAdapter {


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
        TextChannel channel = event.getMessage().getTextChannel();
        int random1 = new Random().nextInt(13);
        int random2 = new Random().nextInt(13);
        int random3 = new Random().nextInt(13);
        if ("!in".equals(message[0]) && "between".equals(message[1])) {
            event.getChannel().sendMessage("Your Card is `" + random1 + "`").queue();
            event.getChannel().sendMessage("The Random Card Drawn is `" + random1 + "and " + random3 + "`").queue();
            if ((random1 < random2 && random1 > random3) || random1 < random3 && random1 > random2 || (random1 == random2) || (random1 == random3)) {
                event.getChannel().sendMessage("You Lost!").queue();

            } else {
                event.getChannel().sendMessage("Congratulations You Won!");
            }
        }
    }
}


