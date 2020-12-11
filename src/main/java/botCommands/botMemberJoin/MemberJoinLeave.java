package botCommands.botMemberJoin;

import java.awt.Color;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MemberJoinLeave extends ListenerAdapter {
	EmbedBuilder build = new EmbedBuilder();
	
	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		super.onGuildMemberJoin(event);
		long channelId = event.getGuild().getDefaultChannel().getIdLong();
		TextChannel channel = event.getGuild().getTextChannelById(channelId);
		build.setColor(Color.DARK_GRAY);
		build.setDescription(">>> Type !help to see some useful commands");
		channel.sendMessage(build.build()).queue(m -> m.delete().queueAfter(1, TimeUnit.HOURS));
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event){
		super.onGuildMemberRemove(event);
		TextChannel channel = event.getGuild().getDefaultChannel();
		String user = Objects.requireNonNull(event.getMember()).getUser().getAsMention();
		build.setColor(Color.DARK_GRAY);
		build.setDescription("The user" + user + "has left the server");
		channel.sendMessage(build.build()).queue(message -> message.delete().queueAfter(1,TimeUnit.HOURS));
	}
}
