package BotCasino;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class InBetween extends ListenerAdapter {


    private static final String apiKey = "62751e209788ea51c832b837b94323f0";

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String[] message = event.getMessage().getContentRaw().split(" ");
        long channelId = event.getMessage().getTextChannel().getIdLong();
        int random1 = new Random().nextInt(13);
        int random2 = new Random().nextInt(13);
        int random3 = new Random().nextInt(13);

        if ("!in".equals(message[0]) && "between".equals(message[1])) {
            event.getChannel().sendMessage("Your Card is `" + random1 + "`").queue();
            event.getChannel().sendMessage("The Random Card Drawn is `" + random2 + " and " + random3 + "`").queue();
            if (random1 == random2 || random1 == random3 ||
                    (random1 < random2 && random1 > random3 || random1 > random2 && random1 < random3)) {
                event.getChannel().sendMessage("You Lost!").queue();

            } else {
                event.getChannel().sendMessage("Congratulations You Won!");
            }
        }

            if ("!weather".equals(message[0])) {
                event.getChannel().sendMessage("wait for response").queue();
            try {


                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://api.openweathermap.org/data/2.5/weather?q=" + message[1] + "&appid=" + apiKey)
                        .build(); // defaults to GET

                Response response = client.newCall(request).execute();

                Gson gson = new Gson();
                JsonObject gson1 = gson.fromJson(response.body().string(), JsonObject.class);
                String weatherName = gson1.getAsJsonPrimitive("name").getAsString();
                double initialWeatherTemperature = gson1.getAsJsonObject("main").getAsJsonPrimitive("temp").getAsDouble();
                String countryName = gson1.getAsJsonObject("sys").getAsJsonPrimitive("country").getAsString();
                long sunsetUnixStyle = gson1.getAsJsonObject("sys").getAsJsonPrimitive("sunset").getAsLong();
                long sunriseUnixStyle = gson1.getAsJsonObject("sys").getAsJsonPrimitive("sunrise").getAsLong();
                double weatherWindSpeed = gson1.getAsJsonObject("wind").getAsJsonPrimitive("speed").getAsDouble();
                double initialTimeZone = gson1.getAsJsonPrimitive("timezone").getAsDouble();
                double finalTimeZone = initialTimeZone/3600D;
                double finalWeatherWindSpeed = weatherWindSpeed * 3.6D;
                double finalWeatherTemperature = initialWeatherTemperature - 273.15;

                // convert seconds to milliseconds
                Date dateSunSet = new java.util.Date(sunsetUnixStyle*1000L);
                Date dateSunRise = new Date(sunriseUnixStyle*1000L);
                // the format of your date
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                // give a timezone reference for formatting (see comment at the bottom)
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
                String formattedSunsetDate = sdf.format(dateSunSet);
                String formattedSunriseDate = sdf.format(dateSunRise);



                EmbedBuilder embed = new EmbedBuilder();

                embed.setColor(Color.YELLOW);
                embed.addField("Weather For " + weatherName + " " + countryName, "`SunRise: " + formattedSunriseDate +
                        "\nSunset: " + formattedSunsetDate + "\nWind Speed: " + finalWeatherWindSpeed + "km/h\nTemperature: " + finalWeatherTemperature
                        + "°C\nTimeZone: " + finalTimeZone + "`"
                        , true);

                event.getChannel().sendMessage(embed.build()).queue();


            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ("!bawas".equals(message[0]) && "points".equals(message[1])
                && "sa".equals(message[3]) && "langit".equals(message[4])) {

            event.getChannel().sendMessage("**BAWAS POINTS KA SA LANGIT** " +  event.getMessage().getMentionedMembers().get(0).getAsMention()).queue();
        }

    }
}


