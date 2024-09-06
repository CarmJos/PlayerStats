package cc.carm.plugin.playerstats.user;

import cc.carm.plugin.playerstats.data.StatType;

import java.util.Map;
import java.util.Set;

public interface StatsAccount {

    /**
     * 得到缓存的全部战绩类型数据
     *
     * @return StatsType, Integer
     */
    Map<StatType, Integer> cache();

    /**
     * 得到当前新修改的，且没有保存到数据库中的战绩类型。
     *
     * @return 战绩类型Set
     */
    Set<StatType> modifiedTypes();

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
     * @param statType 战绩类型
     */
    void removeCache(StatType statType);

    /**
     * 从数据库中更新某种战绩类型的数值
     * 该方法应当异步执行。
     *
     * @param statType 战绩类型
     */
    void updateCache(StatType statType);

    /**
     * 设置账户中某种战绩类型的数量
     *
     * @param statType 战绩类型
     * @param value     数量
     */
    boolean setValue(StatType statType, int value);

    /**
     * 从账户中减少一定数量的战绩类型。
     * 请判断是否成功！
     *
     * @param statType 战绩类型
     * @param value     数量
     * @return 是否成功
     */
    boolean decreaseValue(StatType statType, int value);

    /**
     * 向账户中添加一定数量的战绩类型。
     *
     * @param statType 战绩类型
     * @param value     数量
     * @return 是否成功
     */
    boolean increaseValue(StatType statType, int value);

    /**
     * 得到该账户缓存中战绩类型的数量。
     *
     * @param statType 战绩类型
     * @return 数量
     */
    int getStatsValue(StatType statType);


}
