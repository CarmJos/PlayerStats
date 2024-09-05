package cc.carm.plugin.playerstats.user;

import cc.carm.lib.minecraft.common.bukkit.BukkitCommonAPI;
import cc.carm.lib.minecraft.common.user.User;
import cc.carm.lib.minecraft.common.user.data.UserDataHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface StatsAccount extends UserDataHandler {

    static @Nullable StatsAccount get(@NotNull UUID userUUID) {
        return UserDataHandler.get(userUUID, StatsAccount.class);
    }

    static @Nullable StatsAccount get(@NotNull UserKey user) {
        return UserDataHandler.get(user, StatsAccount.class);
    }

    static @NotNull StatsAccount get(@NotNull Player player) {
        return BukkitCommonAPI.getUserManager().get(player).getHandler(StatsAccount.class);
    }

    /**
     * 得到缓存的全部战绩类型数据
     *
     * @return StatsType, Integer
     */
    Map<StatsType, Integer> getStatsCache();

    /**
     * 得到当前新修改的，且没有保存到数据库中的战绩类型。
     *
     * @return 战绩类型Set
     */
    Set<StatsType> getEditedTypes();

    /**
     * 判断玩家是否还有已被修改的数据没有写入到数据库中。
     *
     * @return 是否全部保存
     */
    boolean isSaved();

    /**
     * 保存当前已修改的数据到数据库中。
     */
    void saveData() throws Exception;


    /**
     * 从该账户缓存中移除战绩类型的数据
     *
     * @param statsType 战绩类型
     */
    void removeCache(StatsType statsType);

    /**
     * 从数据库中更新某种战绩类型的数值
     * 该方法应当异步执行。
     *
     * @param statsType 战绩类型
     */
    void updateCache(StatsType statsType);


    /**
     * 设置账户中某种战绩类型的数量
     *
     * @param statsType 战绩类型
     * @param value     数量
     */
    boolean setValue(StatsType statsType, int value);

    /**
     * 从账户中减少一定数量的战绩类型。
     * 请判断是否成功！
     *
     * @param statsType 战绩类型
     * @param value     数量
     * @return 是否成功
     */
    boolean decreaseValue(StatsType statsType, int value);

    /**
     * 向账户中添加一定数量的战绩类型。
     *
     * @param statsType 战绩类型
     * @param value     数量
     * @return 是否成功
     */
    boolean increaseValue(StatsType statsType, int value);

    /**
     * 得到该账户缓存中战绩类型的数量。
     *
     * @param statsType 战绩类型
     * @return 数量
     */
    int getStatsValue(StatsType statsType);


}
