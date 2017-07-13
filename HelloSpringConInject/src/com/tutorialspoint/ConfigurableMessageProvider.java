package com.tutorialspoint;

import com.tutorialspoint.MessageProvider;

public class ConfigurableMessageProvider implements MessageProvider {

    private String message;

    public ConfigurableMessageProvider(String message) {
        this.message = message;
     }

    public String getMessage() {
        return message;
     }
}
