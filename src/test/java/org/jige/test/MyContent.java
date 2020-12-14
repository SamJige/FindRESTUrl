package org.jige.test;

import org.slf4j.LoggerFactory;

public class MyContent {
    public String name;
    public String content;

    public MyContent(String name, String content) {
        this.name = name;
        this.content = content;
        LoggerFactory.getLogger(getClass()).info("load file -> {}", name);
    }
}