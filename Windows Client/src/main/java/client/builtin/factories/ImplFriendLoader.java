package client.builtin.factories;

import toolkit.protocol.Friend;
import toolkit.protocol.FriendFactory;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ImplFriendLoader implements FriendFactory {
        
    private final Set<Class<? extends Friend>> toBeLoad;
    private final Set<Class<? extends Friend>> beingLoad;
    private ClassLoader loader;

    public ImplFriendLoader(){
        toBeLoad = new HashSet<>();
        beingLoad = new HashSet<>();
        try {
            if (Files.notExists(Path.of("plugins/")))
                Files.createDirectory(Path.of("plugins"));


            var plugins = Arrays.stream(
                            Objects.requireNonNull(
                                    new File("plugins/")
                                            .listFiles()))
                    .filter(File::isFile)
                    .filter(file-> file.getName().endsWith(".jar"))
                    .map(File::toURI)
                    .map((Function<URI, Optional<URL>>) uri -> {
                        try {
                            return Optional.of(uri.toURL());
                        }catch (Exception ignored){
                            return Optional.empty();
                        }
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            var urls = new URL[plugins.size()];

            plugins.toArray(urls);

            loader = new URLClassLoader(
                    urls,
                    ClassLoader.getSystemClassLoader()
            );


            var services = loader.getResources(
                    "META-INF/services/toolkit.protocol.Friend"
            );

            services.asIterator()
                    .forEachRemaining(this::loadService);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void doRecycle(Friend instance) {
        var clazz = instance.getClass();

        //noinspection RedundantCollectionOperation
        if (beingLoad.contains(clazz))
            beingLoad.remove(clazz);
    }


    private void loadService(URL service){
        try {
            var implement = new String(service.openStream().readAllBytes(), UTF_8);

            for (var impl : implement.split("\n"))
                //noinspection unchecked
                toBeLoad.add(
                        (Class<? extends Friend>)
                                loader.loadClass(impl)
                );
        }
        catch (Exception ignored){}
    }

    @Override
    public Iterator<Friend> iterator() {
        return new defaultLoader<>(
                toBeLoad,
                beingLoad,
                ImplFriendLoader::newInstance
        );
    }

    static Optional<Friend> newInstance(Class<? extends Friend> type){
        try{
            return Optional.of(
                    type.getDeclaredConstructor()
                            .newInstance()
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}