package client.builtin.friends;

import toolkit.protocol.Friend;

import java.util.Map;
import java.util.Objects;

public class Assistant implements Friend {
    static final Map<String, String> help;

    private String reply;

    static {
        help = Map.of(
                "hello", "Hello world"
        );
    }

    @Override
    public boolean launch() {
        return true;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void readMessage(String message) {
        reply = help.get(message);
    }

    @Override
    public boolean hasMessages() {
        return reply != null;
    }

    @Override
    public String writeMessage() {
        var tmp = reply;
        reply = null;
        return tmp;
    }

    @Override
    public boolean reboot() {
        reply = "reboot";
        return true;
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Assistant;
    }

    @Override
    public int hashCode() {
        return defaultHash();
    }
}
