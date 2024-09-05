package cc.carm.plugin.playerstats.manager;

import cc.carm.plugin.playerstats.data.StatsType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface StatsTypeManager {


    /**
     * 得到该服务端所有已注册的战绩类型
     * F
     *
     * @return 战绩类型列表
     */
    @Unmodifiable
    @NotNull Set<StatsType> getStatsTypes();

    int updateStatsTypes();

    /**
     * 数据库中更新某个战绩类型的设置。
     *
     * @param id 战绩类型的ID
     */
    void updateStatsType(int id);

    /**
     * 创建一个新战绩类型。
     * 若已存在相同标识的战绩类型，则会直接返回该已存在的战绩类型，不会额外创建。
     *
     * @param key         战绩类型的唯一标识，一般遵守 “游戏:类型”格式，如“PVP:WINS”
     *                    若该战绩类型为通用战绩类型，则依据“GENERIC:战绩类型名称”，如“GENERIC:ROUNDS”
     * @param name        战绩类型的名字，比如“杀敌数”
     * @param description 简单描述该战绩类型的作用，比如“游戏中杀敌数”
     * @return 创建完成后的战绩类型。
     */
    @NotNull StatsType createStatsType(@NotNull String key,
                                       @Nullable String name, @Nullable String description) throws Exception;

    /**
     * 添加战绩类型到服务器战绩类型列表。
     *
     * @param statsType 战绩类型
     * @return 若该战绩类型已存在则会返回false，不存在则返回true；
     */
    boolean addStatsType(@NotNull StatsType statsType);

    /**
     * 从该服务器战绩类型列表中移除一个战绩类型
     *
     * @param statsType 战绩类型
     */
    void removeStatsType(@NotNull StatsType statsType);

    /**
     * 通过ID从战绩类型列表中获得一个战绩类型的示例
     *
     * @param id 战绩的ID
     * @return 战绩类型(不存在则为null)
     */
    @Nullable StatsType getStatsType(int id);

    /**
     * 通过唯一标识从战绩类型列表中获得一个战绩类型的示例
     *
     * @param key 战绩类型的唯一标识
     * @return 战绩类型(不存在则为null)
     */
    @Nullable StatsType getStatsType(@NotNull String key);

    /**
     * 判断是否已有一个相同唯一标识的战绩类型。
     *
     * @param key 战绩类型的唯一标识
     * @return 是否存在
     */
    boolean hasStatsType(@NotNull String key);

    boolean hasStatsType(int id);

}
