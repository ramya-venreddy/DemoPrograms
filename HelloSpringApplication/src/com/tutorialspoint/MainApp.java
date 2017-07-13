package com.tutorialspoint;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.tutorialspoint.MessageRenderer;

public class MainApp {

    public static void main(String[] args) {

        // Initialize Spring ApplicationContext
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");

        MessageRenderer mr = ctx.getBean("renderer", MessageRenderer.class);
        mr.render();
    }
}

