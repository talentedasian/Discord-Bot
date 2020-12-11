package botCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BotWeather extends ListenerAdapter {

    private static final String apiKey = "62751e209788ea51c832b837b94323f0";


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");

        if ("!weather".equals(message[0]) && message.length == 1) {
//                URL url = new URL("api.openweathermap.org/data/2.5/weather?q=" + message[1] + "&appid=" + apiKey);
//                 var connection = (HttpURLConnection) url.openConnection();


//                //Get Response
//                InputStream stream = connection.getInputStream();
//                ObjectMapper mapper = new ObjectMapper();
//                String obj = mapper.readValue(stream, String.class);
//                event.getChannel().sendMessage(obj).queue();
            event.getChannel().sendMessage("wait for response").queue();


        }


    }
}
