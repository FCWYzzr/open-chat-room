package client;


import lombok.SneakyThrows;

public class BootLauncher {
    @SneakyThrows
    public static void main(String[] args) {
        Class.forName("toolkit.protocol.Friend");
        Class.forName("toolkit.protocol.FriendFactory");

        Client.main(args);
    }
}

