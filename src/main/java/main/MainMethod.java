package main;

import BotCasino.InBetween;
import GuildRoles.MemberQualifyForRole;
import botCommands.BotInfoCommand;
import botCommands.botMemberVoiceCommands.DisconnectMember;
import botCommands.botMemberJoin.MemberJoinLeave;
import botCommands.botMemberVoiceCommands.MoveMember;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import lavaPlayer.MassChurch;
import lavaPlayer.YoutubeSearch;
import botCommands.botProfanityFilter.ProfanityFilter;
import embedBuilders.EmbedCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.stream.Collectors;


public class MainMethod implements EventListener {





	public static void main(String[] args) throws LoginException, InterruptedException {
		Collection<GatewayIntent> intents = new ArrayList<>();

		intents.add(GatewayIntent.GUILD_MEMBERS);
		intents.add(GatewayIntent.GUILD_MESSAGES);
		intents.add(GatewayIntent.GUILD_VOICE_STATES);

		JDA jda = JDABuilder.createDefault(new HiddenToken().getToken(), intents).setAudioSendFactory(new NativeAudioSendFactory())
				.setActivity(Activity.listening("Spotify"))
				.setStatus(OnlineStatus.ONLINE)
				.disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.ROLE_TAGS, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES)
				.setChunkingFilter(ChunkingFilter.ALL)
				.setAutoReconnect(true)
				.addEventListeners(new MassChurch())
				.addEventListeners(new InBetween())
				.addEventListeners(new MemberQualifyForRole())
				.addEventListeners(new MoveMember())
				.addEventListeners(new DisconnectMember())
				.addEventListeners(new MemberJoinLeave())
				.addEventListeners(new BotInfoCommand())
				.addEventListeners(new YoutubeSearch())
				.addEventListeners(new ProfanityFilter())
				.disableIntents(GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES)
				.setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.VOICE))
				.build();

				jda.awaitReady();

	}



}
