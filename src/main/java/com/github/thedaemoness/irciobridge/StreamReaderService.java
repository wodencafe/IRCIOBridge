package com.github.thedaemoness.irciobridge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import com.google.common.util.concurrent.AbstractIdleService;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class StreamReaderService extends AbstractIdleService {

	private final PublishSubject<Message> messageSubject = PublishSubject.create();

	public final Observable<Message> onMessage() {
		return messageSubject;
	}

	private final InputStream inputStream;

	private final Charset charset;

	public StreamReaderService(InputStream inputStream, Charset charset) {
		this.inputStream = inputStream;
		this.charset = charset;
	}

	@Override
	protected void startUp() throws Exception {
		try (Scanner input = new Scanner(inputStream, charset)) {
			while (input.hasNextLine() && state() == State.RUNNING) { // WARNING: break;
				final Message m = new Message(input.nextLine());

				messageSubject.onNext(m);
			}
		}
	}

	@Override
	protected void shutDown() throws Exception {
		try {
			inputStream.close();
		} catch (IOException ioe) {

		}
	}

}
