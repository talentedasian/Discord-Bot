package botCommands.botMemberJoin;

import botDataBase.ModelForUserCount;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;

public class BotAddToPostgre  extends ListenerAdapter {


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
            if ("!add".equals(message[0]) && "user".equals(message[1])) {
                ModelForUserCount user = new ModelForUserCount();
                Configuration config = new Configuration();

                SessionFactory factory = config.configure().buildSessionFactory();
                user.setMemberName(event.getMessage().getMentionedUsers().get(0).getName());
                user.setId(event.getMember().getIdLong());
                    if ("dsds".equals(message[1])) {

                    }

            }

    }
}
