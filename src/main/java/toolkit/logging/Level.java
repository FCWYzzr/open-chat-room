package toolkit.logging;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Level {
    DEBUG(0),
    INFO(1),
    WARNING(2),
    ERROR(3),
    FATAL(4);

    int level;

    boolean filter(Level level){
        return level.level >= this.level;
    }
}
