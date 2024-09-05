package cc.carm.plugin.playerstats.user;

import cc.carm.plugin.playerstats.manager.UserKeyManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UserKey {

    public enum KeyType {
        ID("id", Long.class),
        UUID("uuid", java.util.UUID.class),
        NAME("name", String.class);

        final String columnName;
        final Class<?> clazz;

        KeyType(String columnName, Class<?> type) {
            this.columnName = columnName;
            this.clazz = type;
        }

        public String getColumnName() {
            return columnName;
        }

        public Class<?> typeClass() {
            return clazz;
        }

        public boolean isInstance(Object obj) {
            return clazz.isInstance(obj);
        }

    }

    public static UserKey of(long id, @NotNull UUID uuid, @NotNull String name) {
        return new UserKey(id, uuid, name);
    }

    protected final long id;
    protected final @NotNull UUID uuid;
    protected final @NotNull String name;

    public UserKey(long id, @NotNull UUID uuid, @NotNull String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    public long id() {
        return id;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;

    }

    public Object getValue(KeyType type) {
        switch (type) {
            case ID:
                return id;
            case UUID:
                return uuid;
            case NAME:
                return name;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public String toString(KeyType type) {
        return Objects.toString(getValue(type));
    }

    public boolean equals(KeyType type, Object param) {
        switch (type) {
            case ID:
                return param instanceof Number && id == ((Number) param).longValue();
            case UUID:
                return param instanceof UUID && uuid.equals(param);
            case NAME:
                return name.equals(param);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserKey userKey = (UserKey) o;
        return id == userKey.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
