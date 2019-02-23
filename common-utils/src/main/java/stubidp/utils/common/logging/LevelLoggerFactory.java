package stubidp.utils.common.logging;

public class LevelLoggerFactory<T> {
    public LevelLogger createLevelLogger(Class<T> clazz) {
        return LevelLogger.getLevelLogger(clazz);
    }
}
