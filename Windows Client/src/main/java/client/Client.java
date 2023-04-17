package client;

import client.builtin.message.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import toolkit.logging.Level;
import toolkit.logging.Logger;
import toolkit.protocol.Friend;
import toolkit.protocol.FriendFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static javafx.application.Platform.runLater;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private final Map<Friend, List<Message>> history;
    private final ScheduledThreadPoolExecutor executor;

    public Client() {
        this.history = new HashMap<>();
        executor = new ScheduledThreadPoolExecutor(5);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var url = getClass().getResource("clientView.fxml");

        final var factories = new HashSet<FriendFactory>();
        ServiceLoader.load(FriendFactory.class)
                .forEach(factories::add);


        assert url != null;
        Parent root = FXMLLoader.load(url);

        primaryStage.setScene(
                new Scene(root)
        );

        primaryStage.setTitle("Chat Dev");

        final var control = ClientControl.getInstance();

        control.history = history;
        primaryStage.show();


        executor
                .scheduleWithFixedDelay(
                ()->{
                    final var existFriends =
                            control.friends.getItems();
                    var dead = new HashSet<Friend>();
                    existFriends.stream()
                            .filter(friend -> !friend.isAlive())
                            .forEach(dead::add);


                    if (dead.isEmpty())
                        return;

                    Platform.runLater(()->existFriends.removeAll(dead));

                    dead.forEach(deadFriend -> {
                        saveTo(deadFriend.getName(), history.remove(deadFriend));

                        factories.forEach(
                                factory -> factory.doRecycle(deadFriend)
                        );
                    });

                },
                3,
                3,
                TimeUnit.SECONDS
        );
        executor
                .scheduleWithFixedDelay(
            ()-> {
                final var existFriends =
                        control.friends.getItems();
                for (FriendFactory factory : factories) {
                    for (Friend friend : factory) {
                        runLater(() -> existFriends.add(friend));
                        history.put(friend, readFrom(friend.getName()));
                    }
                }
            },
            0,
            3,
            TimeUnit.SECONDS
        );

        executor
                .scheduleWithFixedDelay(
            ()-> control.friends.getItems()
                   .filtered(Friend::hasMessages)
                   .forEach(friend -> {
                  var message = friend.writeMessage();
                  var display = Message.build(friend.getName(), message);


                  history.get(friend).add(display);
                  if (control.friends
                          .getSelectionModel()
                          .getSelectedItems()
                          .contains(friend))
                      runLater(()->control.messages.getItems().add(
                            display
                      ));

            }),
            500,
            50,
            TimeUnit.MILLISECONDS
        );

        control.input.setOnKeyPressed(keyEvent -> {
            if (keyEvent.isControlDown())
                if (keyEvent.getCode() == KeyCode.ENTER)
                    control.sendMessage();

            keyEvent.consume();
        });

        control.messages.getItems().clear();
    }



    @Override
    public void stop() throws Exception {
        super.stop();
        executor.shutdownNow();

        history.forEach((key, value) -> saveTo(key.getName(), value));
    }

    @SneakyThrows
    public void saveTo(String filename, Iterable<Message> text){
        if (Files.notExists(Path.of(filename)))
        Files.createFile(Path.of(filename));

        try(var fileWriter = new PrintWriter(filename)){
            for (var msg :
                    text)
                fileWriter.println(msg);
        }
    }


    public List<Message> readFrom(String filename){

        var history = new LinkedList<Message>();

        try(
                var reader = new FileReader(filename);
                var scanner = new Scanner(reader)
        ){
             String line;

             while(scanner.hasNext())
                 if (!(line = scanner.nextLine()).equals(""))
                    history.add(Message.of(line));

        } catch (IOException ignored) {}

        return history;
    }
}

