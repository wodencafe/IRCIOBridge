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
	public final int PORT;
	public final String SERVER;
	public final String CHANNEL;
	public final String NICK;

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
