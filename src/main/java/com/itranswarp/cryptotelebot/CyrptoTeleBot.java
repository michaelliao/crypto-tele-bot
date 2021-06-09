package com.itranswarp.cryptotelebot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new CyrptoTeleBot(username, token, proxy));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	static DefaultBotOptions createBotOptions(String proxy) {
		var botOptions = new DefaultBotOptions();
		if (proxy != null) {
			String[] ps = parseProxy(proxy);
			System.out.println("proxy: " + ps[0] + "/" + ps[1] + "/" + ps[2]);
			botOptions.setProxyType(ps[0].equals("socks4") ? ProxyType.SOCKS4
					: (ps[0].equals("socks5") ? ProxyType.SOCKS5 : ProxyType.HTTP));
			botOptions.setProxyHost(ps[1]);
			botOptions.setProxyPort(Integer.parseInt(ps[2]));
		}
		return botOptions;
	}

	static String[] parseProxy(String url) {
		Pattern p = Pattern.compile("^(http|socks4|socks5)\\:\\/\\/([a-z0-9\\-\\.]+)\\:(\\d+)$");
		Matcher m = p.matcher(url.toLowerCase());
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid proxy.");
		}
		return new String[] { m.group(1), m.group(2), m.group(3) };
	}

	private final String username;
	private final String token;

	public CyrptoTeleBot(String username, String token, String proxy) {
		super(createBotOptions(proxy));
		this.username = username;
		this.token = token;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			String text = update.getMessage().getText();
			log(">>> %s", text);
			SendMessage message = new SendMessage();
			message.setChatId(update.getMessage().getChatId().toString());
			message.setText("RE: " + update.getMessage().getText());
			try {
				execute(message);
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
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

	static void log(String s, Object... args) {
		System.out.printf(s, args);
		System.out.println();
	}
}
