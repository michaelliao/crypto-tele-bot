package com.itranswarp.cryptotelebot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.itranswarp.cryptotelebot.JsonResponse.Coin;

public class PriceBot {

	private Map<String, Coin> cached = new ConcurrentHashMap<>();
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public PriceBot(String apiKey) {
		String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=" + apiKey;
		executor.scheduleWithFixedDelay(() -> {
			try {
				scheduled(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 0, 5, TimeUnit.MINUTES);
	}

	public Coin getCoin(String name) {
		return cached.get(name.toLowerCase());
	}

	private void scheduled(String url) throws Exception {
		JsonResponse resp = HttpUtil.getJson(JsonResponse.class, url);
		if (resp.status.error_code == 0) {
			// ok:
			System.out.println(resp.data.size() + " coins fetched ok.");
			for (Coin coin : resp.data) {
				cached.put(coin.symbol.toLowerCase(), coin);
				cached.put(coin.name.toLowerCase(), coin);
			}
		}
	}
}
