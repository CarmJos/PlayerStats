package cc.carm.plugin.playerstats.data;

import java.util.List;

public abstract class StatsType<T> implements Comparable<StatsType<T>> {

    protected final int id;
    protected final String key;

    protected final List<String> description;

    protected final Class<T> valueClass;
    protected final T defaultValue;

    public StatsType(int id, String key, List<String> description,
                     Class<T> valueClass, T defaultValue) {
        this.id = id;
        this.key = key;
        this.description = description;
        this.valueClass = valueClass;
        this.defaultValue = defaultValue;
    }

    public int id() {
        return id;
    }

    public String key() {
        return key;
    }

    public List<String> description() {
        return description;
    }

    public Class<T> valueClass() {
        return valueClass;
    }

    public T defaultValue() {
        return defaultValue;
    }

}