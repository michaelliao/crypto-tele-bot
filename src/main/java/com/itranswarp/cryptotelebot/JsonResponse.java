package com.itranswarp.cryptotelebot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JsonResponse {

	public Status status;
	public List<Coin> data;

	public static class Status {
		public int error_code;
		public String error_message;
	}

	public static class Coin {
		public String name;
		public String symbol;
		public BigDecimal max_supply;
		public BigDecimal circulating_supply;
		public BigDecimal total_supply;
		public Map<String, Quote> quote;
	}

	public static class Quote {
		public BigDecimal price;
		public BigDecimal volume_24h;
		public BigDecimal percent_change_1h;
		public BigDecimal percent_change_24h;
		public BigDecimal percent_change_7d;
		public BigDecimal market_cap;
		public String last_updated;
	}
}
