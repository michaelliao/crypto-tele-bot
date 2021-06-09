package com.itranswarp.cryptotelebot;

import java.util.regex.Pattern;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.itranswarp.cryptotelebot.JsonResponse.Coin;
import com.itranswarp.cryptotelebot.JsonResponse.Quote;

public class CyrptoTeleBot extends TelegramLongPollingBot {

	private final String username;
	private final String token;
	private final PriceBot priceBot;
	private final Pattern coinName = Pattern.compile("^[a-zA-Z0-9\\_\\-\\s]{1,50}$");

	public CyrptoTeleBot(String username, String token, DefaultBotOptions botOptions, PriceBot priceBot) {
		super(botOptions);
		this.username = username;
		this.token = token;
		this.priceBot = priceBot;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			Message msg = update.getMessage();
			String text = msg.getText().strip();
			log(">>> %s", text);
			boolean foundCoin = false;
			String resp = "Sorry, I don't understand what you said.";
			if (coinName.matcher(text).matches()) {
				Coin coin = this.priceBot.getCoin(text);
				if (coin == null) {
					resp = "Could not find coin which named \"" + update.getMessage().getText() + "\"";
				} else {
					Quote quote = coin.quote.get("USD");
					if (quote == null) {
						resp = "Sorry, there is no latest price info for " + coin.name + ". Please try later.";
					} else {
						resp = String.format(
								"%s: latest price is %.2f.\nPrice change in 24 hours: %.2f%%\nPrice change in 7 days: %.2f%%",
								coin.name, quote.price, quote.percent_change_24h, quote.percent_change_7d);
						foundCoin = true;
					}
				}
			}
			if (msg.isUserMessage() || foundCoin) {
				SendMessage message = new SendMessage();
				message.setChatId(msg.getChatId().toString());
				message.setText(resp);
				try {
					execute(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}
		if (update.hasChannelPost()) {
			Message message = update.getChannelPost();
			log("--> %s", message.getText());
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

	static void query(String url) {
		//
	}

	static void log(String s, Object... args) {
		System.out.printf(s, args);
		System.out.println();
	}
}
