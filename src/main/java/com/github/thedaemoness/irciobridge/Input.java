package com.github.thedaemoness.irciobridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Scanner;

class Input extends InputStream {
	private final String chan;
	private final Scanner input; //DO NOT CLOSE!
	private final Writer output; //DO NOT CLOSE!
	private byte[] buffer = new byte[0];
	private int index = buffer.length;
	private int end = 0;

	private void acquireLine() throws IOException {
		while(input.hasNextLine()) { //WARNING: break;
			final Message m = new Message(input.nextLine());
			System.err.println(m);
			if("PING".equals(m.command)) {
				output.write("PONG :"+m.text);
			} else if("PRIVMSG".equals(m.command) && chan.equals(m.args)) {
				buffer = (m.text+'\n').getBytes();
				end = buffer.length;
				index = 0;
				break;
			}
			output.flush();
		}
	}

	@Override
	public int available() {
		return end-index;
	}

	@Override
	public int read() throws IOException {
		while(index == end) acquireLine();
		return buffer[index++];
	}

	@Override
	public int read(byte[] to, int off, int len) throws IOException {
		if(len == 0) return 0;
		while(index == end) acquireLine();
		final int reallen = Math.min(len, available());
		System.arraycopy(buffer, index, to, off, reallen);
		index += reallen;
		return reallen;
	}

	@Override
	public void close() {}

	Input(Scanner input, Writer output, String chan) {
		this.input = input;
		this.output = output;
		this.chan = chan;
	}
}