package cc.carm.plugin.playerstats.manager;

import cc.carm.plugin.playerstats.user.UserKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface UserKeyManager {

    /**
     * 通过指定的类型获取用户的键信息。
     *
     * @param type  键的类型
     * @param param 对应键的查询参数
     * @return {@link UserKey}
     */
    @Nullable UserKey getKey(UserKey.KeyType type, Object param);

    // 衍生方法

    default @Nullable UserKey getKey(long id) {
        return getKey(UserKey.KeyType.ID, id);
    }

    default @Nullable UserKey getKey(@NotNull UUID uuid) {
        return getKey(UserKey.KeyType.UUID, uuid);
    }

    default @Nullable UserKey getKey(@NotNull String username) {
        return getKey(UserKey.KeyType.NAME, username);
    }

    // 二次衍生

    default @Nullable Long getID(@NotNull String username) {
        return Optional.ofNullable(getKey(username)).map(UserKey::id).orElse(null);
    }

    default @Nullable Long getID(@NotNull UUID userUUID) {
        return Optional.ofNullable(getKey(userUUID)).map(UserKey::id).orElse(null);
    }

    default @Nullable String getUsername(long id) {
        return Optional.ofNullable(getKey(id)).map(UserKey::name).orElse(null);
    }

    default @Nullable String getUsername(@NotNull UUID userUUID) {
        return Optional.ofNullable(getKey(userUUID)).map(UserKey::name).orElse(null);
    }

    default @Nullable UUID getUUID(long id) {
        return Optional.ofNullable(getKey(id)).map(UserKey::uuid).orElse(null);
    }

    default @Nullable UUID getUUID(@NotNull String username) {
        return Optional.ofNullable(getKey(username)).map(UserKey::uuid).orElse(null);
    }

}
