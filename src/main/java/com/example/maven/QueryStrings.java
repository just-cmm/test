package com.example.maven;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QueryStrings {
	private QueryStrings() {}

	public static Map<String, String> parse(String query) {
		if (query == null || query.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> params = new LinkedHashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf('=');
			if (idx <= 0) {
				continue;
			}
			String key = decode(pair.substring(0, idx));
			String value = decode(pair.substring(idx + 1));
			params.put(key, value);
		}
		return params;
	}

	private static String decode(String s) {
		try {
			return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}
}