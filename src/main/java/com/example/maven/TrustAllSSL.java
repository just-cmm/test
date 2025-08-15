package com.example.maven;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public final class TrustAllSSL {
	private TrustAllSSL() {}

	public static SSLSocketFactory createTrustAllSslSocketFactory() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public void checkClientTrusted(X509Certificate[] chain, String authType) {}
					public void checkServerTrusted(X509Certificate[] chain, String authType) {}
					public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
				}
			};
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			return sc.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("TLS algorithm not available", e);
		} catch (KeyManagementException e) {
			throw new RuntimeException("Failed to initialize trust-all SSLContext", e);
		}
	}

	public static HostnameVerifier createTrustAllHostnameVerifier() {
		return new HostnameVerifier() {
			public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
				return true;
			}
		};
	}

	public static void applyTo(HttpsURLConnection httpsURLConnection) {
		httpsURLConnection.setSSLSocketFactory(createTrustAllSslSocketFactory());
		httpsURLConnection.setHostnameVerifier(createTrustAllHostnameVerifier());
	}
}