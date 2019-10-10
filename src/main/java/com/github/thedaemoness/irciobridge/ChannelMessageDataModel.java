package com.github.thedaemoness.irciobridge;

public class ChannelMessageDataModel {
	private final String senderNick;
	
	private final long timeStamp;
	
	private final String channelName;

	private ChannelMessageDataModel(String senderNick,
			long timeStamp,
			String channelName) {
		this.senderNick = senderNick;
		this.timeStamp = timeStamp;
		this.channelName = channelName;
	}
	
	public static class Builder {

		private String senderNick = null;
		
		private long timeStamp = System.currentTimeMillis();
		
		private String channelName = null;
		
		public Builder setSenderNick(String senderNick) {
			this.senderNick = senderNick;
			return this;
		}
		
		public Builder setTimeStamp(long timeStamp) {
			this.timeStamp = timeStamp;
			return this;
		}
		
		public Builder setChannelName(String channelName) {
			this.channelName = channelName;
			return this;
		}
		
		public ChannelMessageDataModel build() {
			return new ChannelMessageDataModel(senderNick, timeStamp, channelName);
		}
	}

	public String getSenderNick() {
		return senderNick;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public String getChannelName() {
		return channelName;
	}
	
}
