package main;

import GuildRoles.MemberQualifyForRole;
import botCommands.BotInfoCommand;
import botCommands.botMemberJoin.BotAddToPostgre;
import botCommands.botMemberVoiceCommands.DisconnectMember;
import botCommands.botMemberJoin.MemberJoinLeave;
import botCommands.botMemberVoiceCommands.MoveMember;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import lavaPlayer.YoutubeSearch;
import botCommands.botProfanityFilter.ProfanityFilter;
import embedBuilders.EmbedCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class MainMethod {




	public static void main(String[] args)throws LoginException {
		Collection<GatewayIntent> intents = new ArrayList<>();

			intents.add(GatewayIntent.GUILD_MEMBERS);
			intents.add(GatewayIntent.GUILD_MESSAGE_TYPING);
			intents.add(GatewayIntent.GUILD_MESSAGES);
			intents.add(GatewayIntent.GUILD_VOICE_STATES);
			intents.add(GatewayIntent.GUILD_EMOJIS);

		JDA jda = JDABuilder.createDefault(new HiddenToken().getToken(), intents).setAudioSendFactory(new NativeAudioSendFactory())
				.setActivity(Activity.listening("Spotify"))
				.setStatus(OnlineStatus.ONLINE).build();

		jda.addEventListener(new BotAddToPostgre());
		jda.addEventListener(new MemberQualifyForRole());
		jda.addEventListener(new MoveMember());
		jda.addEventListener(new DisconnectMember());
		jda.addEventListener(new MemberJoinLeave());
		jda.addEventListener(new BotInfoCommand());
		jda.addEventListener(new YoutubeSearch());
		jda.addEventListener(new ProfanityFilter());
		jda.addEventListener(new EmbedCommands());

		Map<String,String> jdbcUrlSettings = new HashMap<>();
		String jdbcDbUrl = System.getenv("JDBC_DATABASE_URL");
		if (null != jdbcDbUrl) {
			jdbcUrlSettings.put("hibernate.connection.url", System.getenv("JDBC_DATABASE_URL"));
		}

		var registry = new StandardServiceRegistryBuilder().
				configure("hibernate.cfg.xml").
				applySettings(jdbcUrlSettings).
				build();
	}



}
