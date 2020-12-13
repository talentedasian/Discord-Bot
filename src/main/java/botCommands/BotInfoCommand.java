package botCommands;

import embedBuilders.EmbedCommands;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class BotInfoCommand extends ListenerAdapter {
	static EmbedCommands embeds = new EmbedCommands();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) throws NullPointerException {
			super.onMessageReceived(event);
			String message = event.getMessage().getContentRaw();
			MessageChannel supposedChannel = event.getChannel();




				if ("!markdown".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					 supposedChannel.sendMessage(embeds.texts(Color.BLACK, "> Markdown Commands",
							"Type the following commands to find out more", "`!bold` `\n!italic` `\n!underline` `\n!bolditalic` "
									+ "`\n!underlineitalic` `\n!underlinebold` `\n!underlinebolditalic` `\n!strikethrough`",
							true).build()).queue();
				} else if ("!bold".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					supposedChannel.sendMessage(embeds.textsEmbedMessage(Color.PINK,
							"``` put '**' before and after your text```")
					).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
				} else if ("!italic".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					supposedChannel.sendMessage(embeds.textsEmbedMessage(Color.CYAN,
							"```put '* or _' before and after your text```")
					).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
				} else if ("!underline".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					 supposedChannel.sendMessage(embeds.textsEmbedMessage(Color.DARK_GRAY,
							"```put  '__' before and after your text```")
					).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
				} else if ("!bolditalic".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					supposedChannel.sendMessage(embeds.textsEmbedMessage(Color.ORANGE,
							"```put  '***' before and after your text```")
					).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
				} else if ("!underlineitalic".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					supposedChannel.sendMessage(embeds.textsEmbedMessage(Color.RED,
							"```put  '__*' before and after your text```")
					).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
				} else if ("!underbold".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
						supposedChannel.sendMessage(embeds.textsEmbedMessage(Color.DARK_GRAY,
								"```put  '__**' before and after your text```")
						).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
				} else if ("!underlinebolditalic".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
							supposedChannel.sendMessage(embeds.textsEmbedMessage(Color.DARK_GRAY,
									"```put  '__***' before and after your text```")
							).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));


				}else if ("!voice".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
							supposedChannel.sendMessage(embeds.texts(Color.cyan, "Voice Commands",
						"Voice Channel Commands type the following commands to know more",
						"```!disconnect me - voice command to disconnect" +
								"\n!move voice **voice channel to move to** - move to the voice channel specified " +
								"\n!mute **member** **reason** - mute mentioned member and specify why```"
						,
						false).build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
				}

				else if ("!music".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					supposedChannel.sendMessage(embeds.texts(Color.GREEN, "Music Commands" ,
							"Music Bot Commands type the following commands to know more",
							"```~playyt - play music from youtube \n~playsc - play music from spotify \n~set **volume number** - set the volume of the music bot (**please do not abuse this command**)"
							+ "~volume - get the current volume of the music bot \n~playing track - get info about the current playing track",
							false).build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));

				}

				else if ("!help".equals(message) && supposedChannel.equals(event.getGuild().getTextChannelsByName("hangout",true).get(0))) {
					supposedChannel.sendMessage(embeds.texts(Color.BLACK, "Commands",
							"Type the following commands to know more",
							"```!markdown \n!voice \n!music```",
							false).build()).queue();
				}
	}
}	

	
		

	






