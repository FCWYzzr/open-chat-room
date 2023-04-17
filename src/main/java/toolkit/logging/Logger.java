package toolkit.logging;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.PrintWriter;


/** @noinspection ClassCanBeRecord*/
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
public class Logger {

    private final PrintWriter writer;
    private final String format;
    private final Level threshold;
    private final String name;

    private void log(String msg, Level level){
        if (threshold.filter(level))
            writer.println(format.formatted(msg, level));
    }
    private void log(Exception exp, Level level){
        if (threshold.filter(level)) {
            writer.println(format.formatted(name, level, exp.getMessage()));
            exp.printStackTrace(writer);
        }
    }

    public void debug(String msg){
        log(msg, Level.DEBUG);
    }
    public void info(String msg){
        log(msg, Level.INFO);
    }
    public void warning(String msg){
        log(msg, Level.WARNING);
    }
    public void error(String msg){
        log(msg, Level.ERROR);
    }
    public void fatal(String msg){
        log(msg, Level.FATAL);
        System.exit(0);
    }


    @NoArgsConstructor
    public static class Factory{
        private PrintWriter writer;
        private String format;
        private Level threshold;
        private String name;

        public Factory setWriter(PrintWriter writer){
            this.writer =  writer;
            return this;
        }

        public Factory setFormat(String format) {
            this.format = format;
            return this;
        }

        public Factory setThreshold(Level threshold) {
            this.threshold = threshold;
            return this;
        }

        public Factory setName(String name) {
            this.name = name;
            return this;
        }

        public Logger build(){
            return new Logger(
                    writer,
                    format,
                    threshold,
                    name
            );
        }
    }
}
