package com.weapp.redis;

public class SessionKey extends BasePrefix{

	public SessionKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static SessionKey session = new SessionKey(0, "session:");
}
