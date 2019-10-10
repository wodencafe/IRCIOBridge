package com.github.thedaemoness.irciobridge;

import java.util.UUID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ChannelDataModel {
	private final String channelName;
	
	public ChannelDataModel(String channelName) {
		
	}
	
    private final Cache<UUID, MessageDataModel> messageBuffer = 
       CacheBuilder.newBuilder()
       .maximumSize(1000)
       //.expireAfterAccess(30, TimeUnit.MINUTES)
       .build();
    
    private final PublishSubject<MessageDataModel> messageSubject = PublishSubject.create();

    public final Observable<MessageDataModel> onMessage() {
    	return messageSubject;
    }
    public void addMessage(MessageDataModel message) {
    	messageBuffer.put(UUID.randomUUID(), message);
    	messageSubject.onNext(message);
    }
    
	public final String getChannelName() {
		return channelName;
	}
}
