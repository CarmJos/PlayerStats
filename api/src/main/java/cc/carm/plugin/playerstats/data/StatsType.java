package cc.carm.plugin.playerstats.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public abstract class StatsType<T> implements Comparator<T> {

    protected final int id;
    protected final String key;

    protected final Class<T> valueClass;
    protected final T defaultValue;

    protected StatsType(int id, String key, Class<T> valueClass, T defaultValue) {
        this.id = id;
        this.key = key;
        this.valueClass = valueClass;
        this.defaultValue = defaultValue;
    }

    public abstract @NotNull T parse(@NotNull String data) throws Exception;

    public abstract @Nullable String serialize(@NotNull T data);

    public int id() {
        return id;
    }

    public String key() {
        return key;
    }

    public Class<T> valueClass() {
        return valueClass;
    }

    public T defaultValue() {
        return defaultValue;
    }

}