package client;

import client.builtin.message.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import toolkit.protocol.Friend;

import java.net.URL;
import java.util.*;


public class ClientControl implements Initializable {
    private static ClientControl instance = null;

    public ClientControl() {
        instance = this;
        history = null;
    }

    public Map<Friend, List<Message>> history;

    @FXML
    protected ListView<Message> messages;

    @FXML
    protected ListView<Friend> friends;

    @FXML
    protected TextArea input;

    @FXML
    protected Button sendButton;

    @FXML
    protected Button rebootButton;

    @FXML
    protected Button clearHistoryButton;

    public static ClientControl getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        friends.setCellFactory(lv -> {
            ListCell<Friend> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty().map(Friend::getNameNullable));
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                friends.requestFocus();

                if (cell.isEmpty())
                    return;

                int index = cell.getIndex();
                var selection = friends.getSelectionModel();

                if (selection.getSelectedIndices().contains(index))
                    selection.clearSelection(index);
                else
                    selection.select(index);

                event.consume();
            });
            return cell ;
        });

        messages.setCellFactory(lv -> {
            ListCell<Message> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty().map(Message::view));
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                friends.requestFocus();

                if (cell.isEmpty())
                    return;

                int index = cell.getIndex();
                var selection = friends.getSelectionModel();

                if (selection.getSelectedIndices().contains(index))
                    selection.clearSelection(index);
                else
                    selection.select(index);

                event.consume();
            });
            return cell ;
        });

        messages.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        friends.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        input.setPromptText("说些什么吧");

        friends.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            input.setPromptText(
                                    "向%s说些什么吧".formatted(newValue.getName())
                            );

                            sendButton.setDisable(false);
                            rebootButton.setDisable(false);
                            clearHistoryButton.setDisable(false);
                            if (oldValue != null)
                                messages.getItems().clear();

                            messages.getItems().addAll(history.get(newValue));
                        }
                        else if (oldValue != null){
                            input.setPromptText("");
                            sendButton.setDisable(true);
                            rebootButton.setDisable(true);
                            clearHistoryButton.setDisable(true);
                            messages.getItems().clear();
                        }

                    }
        );
    }

    @FXML
    protected void sendMessage(){
        String msg = input.getText();

        if (msg.equals(""))
            return;

        input.clear();
        var friend =
                friends.getSelectionModel()
                        .selectedItemProperty()
                        .get();

        var display = Message.say(msg);

        friend.readMessage(msg);
        messages.getItems().add(display);
        history.get(friend).add(display);
    }

    @FXML
    protected void clearHistory(){
        var friend =
                friends.getSelectionModel()
                        .selectedItemProperty()
                        .get();

        history.get(friend).clear();

        messages.getItems().clear();
    }

    @FXML
    protected void reboot(){
        var friend =
                friends.getSelectionModel()
                        .selectedItemProperty()
                        .get();
        boolean ret = friend.reboot();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ret? "Success" : "Fail");
        alert.setContentText(ret? "重启成功" : "重启失败");

        alert.show();
    }
}
