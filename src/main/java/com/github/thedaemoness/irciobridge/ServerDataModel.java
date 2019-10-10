package com.github.thedaemoness.irciobridge;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ServerDataModel implements Serializable {
	private final UserDataModel user;
	
	private final String address;
	
	private final String serverPassword;
	
	private final int port;
	
	private final boolean useSsl;
	
	private final Map<String, ChannelDataModel> channels = new HashMap<>();

	private final String quitMessage;
	
    private final Cache<UUID, MessageDataModel> serverMessageBuffer = 
       CacheBuilder.newBuilder()
       .maximumSize(1000)
       //.expireAfterAccess(30, TimeUnit.MINUTES)
       .build();
	
	public UserDataModel getUser() {
		return user;
	}
	public String getAddress() {
		return address;
	}
	public String getServerPassword() {
		return serverPassword;
	}
	public int getPort() {
		return port;
	}
	public boolean isUseSsl() {
		return useSsl;
	}
	public Map<String, ChannelDataModel> getChannels() {
		return channels;
	}
	public String getQuitMessage() {
		return quitMessage;
	}
	public Charset getEncoding() {
		return encoding;
	}
	private final Charset encoding;
	
	private ServerDataModel(UserDataModel user,
		String address,
		String serverPassword,
		int port,
		boolean useSsl,
		Collection<ChannelDataModel> channels,
		String quitMessage,
		Charset encoding) {
		this.user = user;
		this.address = address;
		this.serverPassword = serverPassword;
		this.port = port;
		this.useSsl = useSsl;
		for (ChannelDataModel channel : channels) {
			Objects.requireNonNull(channel.getChannelName(), "Channel name cannot be null");
			this.channels.put(channel.getChannelName(), channel);
		}
		this.quitMessage = quitMessage;
		this.encoding = encoding;
	}
	public static final class Builder {
		private UserDataModel user = null;
		private String address = "irc.freenode.net";
		private String serverPassword = "";
		private int port = 6667;
		private boolean useSsl = false;
		private Collection<ChannelDataModel> channels = new HashSet<>();
		private String quitMessage = "IRCIOBridge Terminated!";
		private Charset encoding = StandardCharsets.UTF_8;
		
		public Builder setUser(UserDataModel user) {
			this.user = user;
			return this;
		}
		public Builder setAddress(String address) {
			this.address = address;
			return this;
		}
		public Builder setServerPassword(String serverPassword) {
			this.serverPassword = serverPassword;
			return this;
		}
		public Builder setPort(int port) {
			this.port = port; 
			return this;
		}
		public Builder useSsl() {
			useSsl = true;
			return this;
		}
		public Builder addChannel(ChannelDataModel channel) {
			channels.add(channel);
			return this;
		}
		public Builder setQuitMessage(String quitMessage) {
			this.quitMessage = quitMessage;
			return this;
		}
		public Builder setEncoding(Charset encoding) {
			this.encoding = encoding;
			return this;
		}
		public ServerDataModel build() {
			Objects.requireNonNull(user, "User cannot be null.");
			return new ServerDataModel(user, address, serverPassword, port, useSsl, channels, quitMessage, encoding);
			
		}
	}

}
