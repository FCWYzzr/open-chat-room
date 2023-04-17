package toolkit.protocol;


import lombok.Getter;

import java.util.Objects;

public interface Friend {
    boolean launch();

    default String getName(){
        return getClass().getSimpleName();
    }

    static String getNameNullable(Friend friend){
        return Objects.isNull(friend) ? "": friend.getName();
    }


    boolean isAlive();
    void readMessage(String message);

    boolean hasMessages();
    String writeMessage();

    boolean reboot();

    default int defaultHash(){
        return getName().hashCode();
    }
}
