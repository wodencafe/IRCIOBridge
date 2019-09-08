package com.github.thedaemoness.irciobridge;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.net.*;
//import javax.annotation.Nonnull;

/** Implements a single-channel connection to an IRC network as an object exposing both a PrintStream and InputStream.
 * Zero support for capabilities, SSL, or more interesting connection processes. Use at your own risk.
 * Provides a helper function to set System.out and System.in.
 * @author TheDaemoness
 * */
public class IRCIOBridge implements AutoCloseable {
	public static class Message {
		public final String prefix, command, args, text;
		Message(String s) {
			Scanner line = new Scanner(s);
			final String cmdbuf = line.next();
			if(cmdbuf.charAt(0) == ':') {
				prefix = cmdbuf.substring(1);
				command = line.next();
			} else {
				prefix = "";
				command = cmdbuf;
			}
			String[] divided = line.nextLine().split(":",2);
			args = divided[0].trim();
			if(divided.length == 2) text = divided[1];
			else text = "";
		}
		@Override
		public String toString() {
			return String.join("", ":", prefix, " ", command, " ", args, " :", text);
		}
	}
	public final int PORT;
	public final String SERVER;
	public final String CHANNEL;
	public final String NICK;

	private static class Output extends OutputStream {
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

	private static class Input extends InputStream {
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

	private final Socket s;

	private final PrintStream out;
	private final InputStream in;

	public IRCIOBridge(
		String server,
		String channel,
		List<String> nicks,
		int port
	) throws IOException {
		Objects.requireNonNull(server);
		Objects.requireNonNull(channel);
		Objects.requireNonNull(nicks);
		if(nicks.isEmpty()) throw new IllegalArgumentException("Passed nick list is empty");
		PORT = port;
		SERVER = server;
		CHANNEL = channel;
		s = new Socket(SERVER, PORT);
		Scanner input = new Scanner(s.getInputStream(), StandardCharsets.UTF_8);
		Writer output = new OutputStreamWriter(s.getOutputStream());
		final Iterator<String> nickit = nicks.iterator();
		String nick = nickit.next();
		output.write("NICK "+nick+"\r\n");
		output.write("USER "+nick+" 8 * :Beep boop.\r\n");
		output.flush();
		while(input.hasNextLine()) { //WARNING: break;
			final Message m = new Message(input.nextLine());
			System.err.println(m);
			if("004".equals(m.command)) {
				output.write("JOIN "+CHANNEL+"\r\n");
			} else if("366".equals(m.command)) {
				break;
			} else if("PING".equals(m.command)) {
				output.write("PONG :"+m.text+"\r\n");
			} else if("433".equals(m.command)) {
				if(nickit.hasNext()) {
					nick = nickit.next();
					output.write("NICK "+nick+"\r\n");
				} else {
					throw new IOException("All nick options are in use");
				}
			}
			output.flush();
		}
		NICK = nick;
		this.out = new PrintStream(new Output(s.getOutputStream(), CHANNEL));
		this.in = new Input(input, output, CHANNEL);
	}
	public IRCIOBridge(
		String server,
		String channel,
		List<String> nicks
	) throws IOException {
		this(server, channel, nicks, 6667);
	}
	public IRCIOBridge(
		String server,
		String channel,
		String... nicks
	) throws IOException {
		this(server, channel, Arrays.asList(nicks));
	}

	@Override
	public void close() throws IOException {
		if(s.isConnected()) {
			out.flush();
			s.close();
		}
	}

	public IRCIOBridge setSystemStreams() {
		System.setOut(out);
		System.setIn(in);
		return this;
	}

	public static IRCIOBridge setSystemStreams(String server, String channel, String... nicks)
	throws IOException {
		return new IRCIOBridge(server, channel, nicks).setSystemStreams();
	}
}

//Example use: IRCIOBridge.setSystemStreams("irc.freenode.net", "#somechannel", "MyBotName");
