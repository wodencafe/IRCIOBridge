package com.github.thedaemoness.irciobridge;

import java.util.Arrays;
import java.util.List;

public class UserDataModel {
	private int nickIndex = 0; //Do not make final.

	private final String ident;
	private final String realName;
	private final List<String> nickOptions;

	public UserDataModel(String ident, String realName, String... nicks) {
		assert(nicks.length != 0);
		this.ident = ident;
		this.realName = realName;
		this.nickOptions = Arrays.asList(nicks);
	}

	public String getIdent() {
		return ident;
	}

	public String getRealName() {
		return realName;
	}

	public String getNick() {
		return nickOptions.get(nickIndex);
	}

	public boolean nextNick() {
		if(nickIndex != nickOptions.size()) {
			++nickIndex;
			return true;
		} else return false;
	}
}
