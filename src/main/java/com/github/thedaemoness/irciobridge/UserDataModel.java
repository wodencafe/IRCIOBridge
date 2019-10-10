package com.github.thedaemoness.irciobridge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class UserDataModel {
	public String getRealName() {
		return realName;
	}
	public String getIdent() {
		return ident;
	}
	public Collection<String> getNicks() {
		return Collections.unmodifiableCollection(nicks);
	}
	private final String realName;
	
	private final String ident;
	
	private final Collection<String> nicks = new ArrayList<>();
	
	public UserDataModel(
			String realName,
			String ident,
			Collection<String> nicks) {
		this.realName = realName;
		this.ident = ident;
		this.nicks.addAll(nicks);
	}
	public static final class Builder {
		private String realName = "Beep Boop";
		
		private String ident = "foo";
		
		private final Collection<String> nicks = new ArrayList<>();
		
		public Builder setRealName(String realName) {
			this.realName = realName;
			return this;
		}
		
		public Builder setIdent(String ident) {
			this.ident = ident;
			return this;
		}
		
		public Builder addNick(String nick) {
			nicks.add(nick);
			return this;
		}
		
		public UserDataModel build() {
			if (nicks.isEmpty()) {
				nicks.add("IRCIOBridge_User");
			}
			return new UserDataModel(realName, ident, nicks);
		}
	}
}
