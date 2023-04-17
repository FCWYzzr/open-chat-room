package demo;

import toolkit.protocol.Friend;

import java.util.LinkedList;
import java.util.Queue;

public class Bot implements Friend {
    private final Queue<String> msg;

    public Bot() {
        this.msg = new LinkedList<>();
    }

    @Override
    public boolean launch() {
        msg.offer("hello");
        return true;
    }

    @Override
    public String getName() {
        return "Demo";
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void readMessage(String message) {
        msg.offer("you said '%s'".formatted(message));
    }

    @Override
    public boolean hasMessages() {
        return !msg.isEmpty();
    }

    @Override
    public String writeMessage() {
        return msg.poll();
    }

    @Override
    public boolean reboot() {
        msg.clear();
        msg.offer("reboot");
        return true;
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Bot;
    }

    @Override
    public int hashCode() {
        return defaultHash();
    }
}
