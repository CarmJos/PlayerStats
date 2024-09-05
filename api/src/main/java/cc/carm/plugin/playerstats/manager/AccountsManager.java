package cc.carm.plugin.playerstats.manager;

import cc.carm.lib.minecraft.common.bukkit.BukkitCommonAPI;
import cc.carm.lib.minecraft.common.user.data.UserDataHandler;
import cc.carm.plugin.playerstats.user.StatsAccount;
import cc.carm.plugin.playerstats.user.UserKey;
import cc.carm.service.essentials.bukkit.api.stats.StatsAccount;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AccountsManager {

    @Unmodifiable
    @NotNull Map<@NotNull UUID, StatsAccount> cached();


    /**
     * 从已加载的用户中获取一个用户
     *
     * @param userUUID 用户的UUID
     * @return 用户实例
     */
    @Nullable StatsAccount get(@NotNull UUID userUUID);

    /**
     * 从已加载的用户中获取一个用户
     *
     * @param uid 用户的UID
     * @return 用户实例
     */
    @Nullable StatsAccount get(long uid);

    default @Nullable StatsAccount get(@NotNull UserKey key) {
        return get(key.uuid());
    }

    /**
     * 从数据库中读取并加载一个用户及其相关处理器。
     * 将触发所有处理器的 {@link AbstractDataHandler#load()} 。
     *
     * @param userUUID 用户UUID
     * @param username 用户名
     * @return 用户实例
     * @throws Exception 当出现任何错误时抛出
     */
    @NotNull StatsAccount load(@NotNull UUID userUUID, @NotNull String username) throws Exception;

    /**
     * 从数据库直接加载制定用户的数据用作临时使用。
     * <br> 请注意： 此方法不应当对用户的任何数据做变更！
     *
     * @param key            用户的键
     * @param handlerClasses 需要的处理器的类型
     * @return 用户实例
     * @throws Exception 当出现任何错误时抛出
     */
    @NotNull StatsAccount loadTemp(@NotNull UserKey key, @NotNull List<Class<? extends UserDataHandler>> handlerClasses) throws Exception;

    /**
     * 获取一个用户的数据，如果用户在线，则直接返回用户的数据，否则从数据库中读取。
     * <br> 请注意： 此方法不应当对用户的任何数据做变更！
     *
     * @param key            用户的键
     * @param handlerClasses 需要的处理器的类型
     * @return 用户实例
     * @throws Exception 当出现任何错误时抛出
     */
    default @NotNull StatsAccount peek(@NotNull UserKey key, @NotNull List<Class<? extends UserDataHandler>> handlerClasses) throws Exception {
        return Optional.ofNullable(get(key)).orElse(loadTemp(key, handlerClasses));
    }

    /**
     * 卸载并保存用户。
     * 将触发所有处理器的 {@link AbstractDataHandler#save()}  。
     *
     * @param userUUID 用户的UUID
     */
    void unload(@NotNull UUID userUUID);

    /**
     * 直接从内存中移除用户
     * 将触发所有处理器的 {@link AbstractDataHandler#unload()} 。
     *
     * @param userUUID 用户的UUID
     */
    void remove(@NotNull UUID userUUID);

}
