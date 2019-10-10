package com.github.thedaemoness.irciobridge.handlers;

import java.util.function.Consumer;

import com.github.thedaemoness.irciobridge.Message;

import io.reactivex.Observer;

public interface MessageHandler extends Observer<Message> {

}
