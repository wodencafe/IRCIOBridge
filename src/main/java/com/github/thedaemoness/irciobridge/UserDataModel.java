package com.github.thedaemoness.irciobridge;

import java.util.ArrayList;
import java.util.Collection;

public class UserDataModel {
	private final String nick;
	
	private final String realName;
	
	private final String ident;
	
	private final Collection<String> alternativeNicks = new ArrayList<>();
	
}
