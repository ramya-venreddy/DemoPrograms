package com.tutorialspoint;

import org.springframework.stereotype.Service;

//Rest of codes omitted
@Service("messageProvider")
public class HelloWorldMessageProvider implements MessageProvider {

    public String getMessage() {
        return "God is Great";
    }
}
