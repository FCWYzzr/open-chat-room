package toolkit.protocol;


import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;


public class OnlineFriend implements Friend {
    private final Socket socket;
    private final String name;

    private final InetAddress address;
    private final int port;

    public static Optional<Prototype> parse(String line){
        String[] params = line.split(", ");

        if ( params.length != 3 )
            throw new InputMismatchException("file content must be [" +
                    "hostname, port, name" +
                    "] each line");

        try{
            int port = Integer.parseInt(params[1]);

            if (port < 0 || port >= 65536)
                throw new NumberFormatException();

            return Optional.of(new Prototype(
                    (params[0].equals("localhost")?
                            InetAddress.getLocalHost() :
                            InetAddress.getByName(params[0]))
                            .getHostAddress(),
                    port,
                    params[2]
            ));
        }
        catch (Exception any){
            return Optional.empty();
        }
    }

    private OnlineFriend(String hostname, int port, String name)
            throws IOException {

        this.address = InetAddress.getByName(hostname);
        this.port = port;

        socket = new Socket();
        socket.setSoTimeout(1);

        this.name = name;
    }

    @Override
    public boolean launch() {
        try {
            socket.connect(new InetSocketAddress(address, port), 50);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAlive() {

        try{
            socket.sendUrgentData(0xFF);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void readMessage(String message) {
        try {
            var out = socket.getOutputStream();


            out.write(message.getBytes(UTF_8));
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public boolean hasMessages() {
        try {
            var in = socket.getInputStream();

            return in.available() > 0;
        }catch (Exception any){

            return false;
        }
    }

    @SneakyThrows
    @Override
    public String writeMessage() {
        var in = socket.getInputStream();

        return new String(in.readNBytes(in.available()), UTF_8);
    }

    @Override
    public boolean reboot() {
        return false;
    }


    public static record Prototype(
            String hostname,
            int port,
            String name
    ){
        public Optional<Friend> makeInstance(){
            try {
                return Optional.of(new OnlineFriend(
                        hostname,
                        port,
                        name));
            } catch (IOException e) {
                return Optional.empty();
            }
        }
    }

    public Prototype decay(){
        try {socket.close();}
        catch (IOException ignored) {}

        return new Prototype(
                address.getHostAddress(),
                port,
                name
        );
    }
}
