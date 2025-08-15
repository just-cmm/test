package com.example.maven;

public class Hello {
	public String sayHello(String name) {
		return "Hello "+name+"!";
	}
public static void main(String[] args) {
System.out.print("hello");
System.out.print("第一次");
System.out.print("测试ssh");

try {
	SimpleHttpServer server = new SimpleHttpServer(8080);
	server.start();
	System.out.println("/proxy ready. Example: GET /proxy?url=https://self-signed.badssl.com/");
} catch (Exception e) {
	e.printStackTrace();
}
}
}
