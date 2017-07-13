package com.spring;

public interface MessageRenderer {
	void render();
	void setMessageProvider(MessageProvider provider);
    MessageProvider getMessageProvider();

}
