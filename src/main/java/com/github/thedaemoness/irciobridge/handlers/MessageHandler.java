package com.github.thedaemoness.irciobridge.handlers;

import java.util.function.Consumer;

import com.github.thedaemoness.irciobridge.Message;

import io.reactivex.Observer;

public interface MessageHandler extends Observer<Message>, AutoCloseable {

	default void onError(Throwable e) {
		e.printStackTrace();
	}
	
	default void onComplete() {
		try {
			close();
		} catch (Exception e) {
			onError(e);
		}
	}
}
