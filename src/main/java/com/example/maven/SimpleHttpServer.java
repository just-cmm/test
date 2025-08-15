package com.example.maven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SimpleHttpServer {
	private final HttpServer server;

	public SimpleHttpServer(int port) throws IOException {
		this.server = HttpServer.create(new InetSocketAddress(port), 0);
		this.server.createContext("/proxy", new ProxyHandler());
	}

	public void start() {
		this.server.start();
		System.out.println("HTTP server started at http://127.0.0.1:" + this.server.getAddress().getPort());
	}

	static class ProxyHandler implements HttpHandler {
		public void handle(HttpExchange exchange) throws IOException {
			try {
				if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()) && !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
					exchange.sendResponseHeaders(405, -1);
					return;
				}

				URI requestURI = exchange.getRequestURI();
				String query = requestURI.getRawQuery();
				Map<String, String> queryParams = QueryStrings.parse(query);
				String targetUrl = queryParams.get("url");
				if (targetUrl == null || targetUrl.isEmpty()) {
					byte[] body = "Missing 'url' query parameter".getBytes(StandardCharsets.UTF_8);
					exchange.sendResponseHeaders(400, body.length);
					try (OutputStream os = exchange.getResponseBody()) {
						os.write(body);
					}
					return;
				}

				URL url = new URL(targetUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				if (conn instanceof HttpsURLConnection) {
					TrustAllSSL.applyTo((HttpsURLConnection) conn);
				}

				conn.setInstanceFollowRedirects(false);
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				conn.setRequestMethod(exchange.getRequestMethod());

				// forward headers
				for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
					String name = header.getKey();
					if (name == null) continue;
					// skip hop-by-hop headers
					if ("Host".equalsIgnoreCase(name) || "Content-Length".equalsIgnoreCase(name)) continue;
					for (String value : header.getValue()) {
						conn.addRequestProperty(name, value);
					}
				}

				// forward body if POST
				if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
					byte[] requestBody = readAllBytes(exchange.getRequestBody());
					conn.setDoOutput(true);
					try (OutputStream os = conn.getOutputStream()) {
						os.write(requestBody);
					}
				}

				int status = conn.getResponseCode();
				InputStream responseStream = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
				byte[] responseBytes = responseStream != null ? readAllBytes(responseStream) : new byte[0];

				// set response headers
				for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
					if (header.getKey() == null) continue; // status line
					if ("Transfer-Encoding".equalsIgnoreCase(header.getKey())) continue;
					for (String value : header.getValue()) {
						exchange.getResponseHeaders().add(header.getKey(), value);
					}
				}

				exchange.sendResponseHeaders(status, responseBytes.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(responseBytes);
				}
			} catch (Exception e) {
				byte[] body = ("Proxy error: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
				exchange.sendResponseHeaders(502, body.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(body);
				}
			}
		}
	}

	private static byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[8192];
		int nRead;
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		return buffer.toByteArray();
	}
}