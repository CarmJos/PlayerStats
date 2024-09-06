package cc.carm.plugin.playerstats.data;

public class StatType {

    protected final int id;
    protected final String key;

    protected final int defaultValue;

    protected final String name;
    protected final String description;

    protected StatType(int id, String key, int defaultValue, String name, String description) {
        this.id = id;
        this.key = key;
        this.defaultValue = defaultValue;
        this.name = name;
        this.description = description;
    }

    public int id() {
        return id;
    }

    public String key() {
        return key;
    }

    public int defaultValue() {
        return defaultValue;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

}