package com.itranswarp.cryptotelebot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class CyrptoTeleBot extends TelegramLongPollingBot {

	public static void main(String[] args) {
		String username = null;
		String token = null;
		String proxy = null;
		for (String arg : args) {
			if (arg.startsWith("--username=")) {
				username = arg.substring("--username=".length());
			}
			if (arg.startsWith("--token=")) {
				token = arg.substring("--token=".length());
			}
			if (arg.startsWith("--proxy=")) {
				proxy = arg.substring("--proxy=".length());
			}
		}
		if (username == null) {
			throw new IllegalArgumentException("Missing argument. use: --username=xxx");
		}
		if (token == null) {
			throw new IllegalArgumentException("Missing argument. use: --token=xxx");
		}
		var botOptions = new DefaultBotOptions();
		if (proxy != null) {
			botOptions.setProxyType(ProxyType.HTTP);
			botOptions.setProxyHost(proxy);
			botOptions.setProxyPort(0);
		}
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new CyrptoTeleBot(username, token));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	String[] parseProxy(String url) {
		Pattern p = Pattern.compile("^(http|socks4|socks5)\\:\\/\\/([a-z0-9\\-\\.]+)\\:(\\d+)$");
		Matcher m = p.matcher(url.toLowerCase());
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid proxy.");
		}
		return new String[] { m.group(1), m.group(2), m.group(3) };
	}

	private final String username;
	private final String token;

	public CyrptoTeleBot(String username, String token) {
		this.username = username;
		this.token = token;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {

		}
	}

	@Override
	public String getBotUsername() {
		return this.username;
	}

	@Override
	public String getBotToken() {
		return this.token;
	}

	public static class BotConfig {
		public String username;
		public String token;
	}
}
