package com.github.thedaemoness.irciobridge.handlers;

import java.io.IOException;
import java.io.Writer;

import com.github.thedaemoness.irciobridge.Message;

import io.reactivex.disposables.Disposable;

public class PingMessageHandler implements MessageHandler {
	
	private Writer writer;
	
	public PingMessageHandler(Writer writer) {
		this.writer = writer;
	}
	private Disposable d;
	@Override
	public void onSubscribe(Disposable d) {
		this.d = d;
		
	}

	@Override
	public void onNext(Message m) {
		try {
			writer.write("PONG :"+m.text+"\r\n");
		} catch (IOException e) {
			onError(e);
		}
	}


	@Override
	public void close() throws Exception {
		d.dispose();
	}

}
