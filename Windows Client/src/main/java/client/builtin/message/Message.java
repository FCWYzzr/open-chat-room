package client.builtin.message;

public record Message(
        String sender,
        String content,
        String senderFormatted
) {
    public static Message build(String sender, String content) {
        return new Message(
                sender,
                content,
                formatName(sender)
        );
    }

    private static String formatName(String name){
        if (name.length() >= 20)
            return name;

        int margin = 20 - name.length();
        var sb = new StringBuilder(21);
        sb.append(
                  " ".repeat(margin/2)
          ).append(
                  name
          ).append(
                  " ".repeat(margin/2)
          );

        return sb.substring(0, 18);
    }

    public String view(){
        return "%s: %s".formatted(senderFormatted, content);
    }

    @Override
    public String toString() {
        return "%s, %s".formatted(sender, content);
    }

    public static Message of(String line){
        final var param = line.split(", ");
        return Message.build(param[0], param[1]);
    }

    public static Message say(String content){
        return Message.build(
                " ".repeat(9) + "me" + " ".repeat(9),
                content);
    }
}
