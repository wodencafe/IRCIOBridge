package com.github.thedaemoness.irciobridge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class UserDataModel {
	private final String nick;
	
	public String getNick() {
		return nick;
	}
	public String getRealName() {
		return realName;
	}
	public String getIdent() {
		return ident;
	}
	public Collection<String> getAlternativeNicks() {
		return Collections.unmodifiableCollection(alternativeNicks);
	}
	private final String realName;
	
	private final String ident;
	
	private final Collection<String> alternativeNicks = new ArrayList<>();
	
	public UserDataModel(String nick,
			String realName,
			String ident,
			Collection<String> alternativeNicks) {
		this.nick = nick;
		this.realName = realName;
		this.ident = ident;
		this.alternativeNicks.addAll(alternativeNicks);
	}
	public static final class Builder {
		private String nick = "IRCIOBridge_User";
		
		private String realName = "Beep Boop";
		
		private String ident = "foo";
		
		private final Collection<String> alternativeNicks = new ArrayList<>();

		public void setNick(String nick) {
			this.nick = nick;
		}
		
		public void setRealName(String realName) {
			this.realName = realName;
		}
		
		public void setIdent(String ident) {
			this.ident = ident;
		}
		
		public void addAlternativeNick(String alternativeNick) {
			alternativeNicks.add(alternativeNick);
		}
		
		public UserDataModel build() {
			return new UserDataModel(nick, realName, ident, alternativeNicks);
		}
	}
}
