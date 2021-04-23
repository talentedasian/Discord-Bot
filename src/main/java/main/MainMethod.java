package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import BotCasino.InBetween;
import GuildRoles.MemberQualifyForRole;
import botCommands.BotInfoCommand;
import botCommands.botMemberJoin.MemberJoinLeave;
import botCommands.botMemberVoiceCommands.DisconnectMember;
import botCommands.botMemberVoiceCommands.MoveMember;
import botCommands.botMemberVoiceCommands.MuteMembers;
import botCommands.botProfanityFilter.ProfanityFilter;
import lavaPlayer.MassChurch;
import lavaPlayer.YoutubeSearch;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;


public class MainMethod implements EventListener {

	public static void main(String[] args) throws LoginException, InterruptedException {
		Collection<GatewayIntent> intents = new ArrayList<>();

		intents.add(GatewayIntent.GUILD_MEMBERS);
		intents.add(GatewayIntent.GUILD_MESSAGES);
		intents.add(GatewayIntent.GUILD_VOICE_STATES);

		JDA jda = JDABuilder.createDefault("NzU3NTE3MzcwNjE2MTg0ODgz.X2hjBw.sKD7Zcr7ZkM-bdQ3gl_cP5aVl98", intents).setAudioSendFactory(new NativeAudioSendFactory())
				.setActivity(Activity.listening("Playing death With Satan"))
				.setStatus(OnlineStatus.ONLINE)
				.disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.ROLE_TAGS, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES)
				.setAutoReconnect(true)
				.addEventListeners(new MuteMembers())
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
