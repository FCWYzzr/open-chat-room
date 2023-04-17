package client.builtin.factories;

import toolkit.protocol.OnlineFriend;
import lombok.SneakyThrows;
import toolkit.protocol.Friend;
import toolkit.protocol.FriendFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;


import static java.nio.charset.StandardCharsets.UTF_8;


public class OnlineFriendLoader implements FriendFactory {
    private final Set<OnlineFriend.Prototype> toBeLoad;
    private final Set<OnlineFriend.Prototype> beingLoad;

    public OnlineFriendLoader() {

        toBeLoad = new HashSet<>();
        beingLoad = new HashSet<>();

        try {

            if (Files.notExists(Path.of("online friends.csv")))
                Files.createFile(Path.of("online friends.csv"));

            var lines = Files.readAllLines(
                    Path.of("online friends.csv"),
                    UTF_8
            );

            lines.stream()
                 .map(OnlineFriend::parse)
                 .filter(Optional::isPresent)
                 .map(Optional::get)
                 .forEach(toBeLoad::add);

        }
        catch (Exception ignored){}
    }

    @SneakyThrows
    @Override
    public Iterator<Friend> iterator() {
        if (toBeLoad.isEmpty())
            return emptyIterator;
        else
            return new defaultLoader<>(
                    toBeLoad,
                    beingLoad,
                    OnlineFriend.Prototype::makeInstance
            );
    }

    @Override
    public void doRecycle(Friend instance) {
        if (! (instance instanceof OnlineFriend onlineFriend))
            return;

        var proto = onlineFriend.decay();
        beingLoad.remove(proto);
    }

}
