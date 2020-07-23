package org.jige.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

abstract public class MyKeyListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    abstract public void keyReleased(KeyEvent e);
}