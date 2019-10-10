package com.github.thedaemoness.irciobridge;

import java.io.IOException;
import java.io.OutputStream;

class Output extends OutputStream {
	private static final int LIMIT = 512;
	private final OutputStream output; //DO NOT CLOSE!
	private final byte[] buffer = new byte[LIMIT];
	private final int prefixLength;
	private int end;

	@Override
	public void write(int bytein) throws IOException {
		final byte b = (byte)bytein;
		if(b == '\n') flush();
		else if(b == '\r') {}
		else {
			buffer[end++] = b;
			if(end == buffer.length-2) flush();
		}
	}

	@Override
	public void flush() throws IOException {
		if(end == prefixLength) return;
		buffer[end++] = '\r';
		buffer[end++] = '\n';
		output.write(buffer, 0, end);
		output.flush();
		end = prefixLength;
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	private static String makePrefix(String chan) {
		if(chan.charAt(0) != '#') throw new IllegalArgumentException("Unsupported channel "+chan);
		return String.join(" ", "PRIVMSG", chan, ":");
	}

	Output(OutputStream sockOut, String chan) {
		output = sockOut;
		byte[] prefix = makePrefix(chan).getBytes();
		if(prefix.length > LIMIT-2) throw new IllegalArgumentException("Channel name is definitely too long: "+chan);
		System.arraycopy(prefix, 0, buffer, 0, prefix.length);
		prefixLength = prefix.length;
		end = prefixLength;
	}
}