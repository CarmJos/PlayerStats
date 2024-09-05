package cc.carm.plugin.playerstats.manager;

import cc.carm.plugin.playerstats.data.StatsType;
import cc.carm.plugin.playerstats.user.UserKey;

import java.util.LinkedHashMap;

public interface StatsManager extends AccountsManager, StatsTypeManager {
    /**
     * 得到由货币数值排序后的玩家UUID列表。
     *
     * @param type  货币类型
     * @param limit 列表长度(由于数据数量的不确定，返回的列表大小不一定等于列表长度)
     * @return 玩家UUID 对应 该账户所拥有的数值
     */
    LinkedHashMap<UserKey, Integer> getOrderedAccounts(StatsType type, int limit);

}
