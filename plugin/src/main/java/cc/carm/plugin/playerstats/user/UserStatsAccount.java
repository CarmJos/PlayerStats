package cc.carm.plugin.playerstats.user;

import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.minecraft.common.user.data.AbstractDataHandler;
import cc.carm.service.essentials.bukkit.Main;
import cc.carm.service.essentials.bukkit.MineEssentialsAPI;
import cc.carm.service.essentials.bukkit.api.stats.StatsType;
import cc.carm.service.essentials.bukkit.data.DataTables;
import cc.carm.service.essentials.bukkit.event.stats.StatsAccountSyncedEvent;
import cc.carm.service.essentials.bukkit.event.stats.StatsValueChangeEvent;
import cc.carm.service.essentials.bukkit.event.stats.StatsValueChangedEvent;
import cc.carm.service.essentials.bukkit.event.stats.StatsValuePreChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class UserStatsAccount extends AbstractDataHandler implements StatsAccount {

    private Map<Integer, Integer> statsCache = new ConcurrentHashMap<>();
    private final Set<Integer> modifiedStats = new HashSet<>();


    @Override
    public void load() throws Exception {
        Map<Integer, Integer> data = new HashMap<>();
        try (SQLQuery query = DataTables.STATS_ACCOUNT.createQuery()
                .addCondition("uid", getUID()).build().execute()) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                data.put(result.getInt("type"), result.getInt("value"));
            }
        }
        this.statsCache = data;
    }

    @Override
    public void unload() {

    }

    @Override
    public void save() {
        try {
            this.saveData();
        } catch (Exception e) {
            Main.getInstance().error("保存用户 " + getUsername() + " 的战绩数据失败！");
            e.printStackTrace();
        }
    }

    @Override
    public Map<StatsType, Integer> cache() {
        Map<StatsType, Integer> data = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : this.statsCache.entrySet()) {
            StatsType statsType = MineEssentialsAPI.getStatsManager().getStatsType(entry.getKey());
            if (statsType != null) data.put(statsType, entry.getValue());
        }

        return Collections.unmodifiableMap(data);
    }

    @Override
    public Set<StatsType> modifiedTypes() {
        Set<StatsType> types = new HashSet<>();
        for (Integer id : this.modifiedStats) {
            StatsType statsType = MineEssentialsAPI.getStatsManager().getStatsType(id);
            if (statsType != null) types.add(statsType);
        }
        return Collections.unmodifiableSet(types);
    }

    @Override
    public boolean isSaved() {
        return this.modifiedStats.isEmpty();
    }

    @Override
    public void saveData() throws Exception {
        if (this.modifiedStats.isEmpty()) return;

        Map<Integer, Integer> changedValues = new HashMap<>();
        for (Integer statsID : this.modifiedStats) {
            int value = this.statsCache.getOrDefault(statsID, 0);
            changedValues.put(statsID, value);
        }

        List<Object[]> params = changedValues.entrySet().stream()
                .map(entry -> new Object[]{getUID(), entry.getKey(), entry.getValue()})
                .collect(Collectors.toList());

        DataTables.STATS_ACCOUNT.createReplaceBatch()
                .setColumnNames("uid", "type", "value")
                .setAllParams(params)
                .execute(); // 执行更新。

        DataTables.STATS_ACCOUNT.createDelete()
                .addCondition("uid", getUID())
                .addCondition("value", 0)
                .build().executeAsync(); // 删除无效数据。
    }

    @Override
    public void removeCache(StatsType statsType) {
        this.statsCache.remove(statsType.id());
    }

    @Override
    public void updateCache(StatsType statsType) {
        try {
            int value = DataTables.STATS_ACCOUNT.createQuery()
                    .addCondition("uid", getUID())
                    .addCondition("type", statsType.id())
                    .build().executeFunction((query) -> {
                        ResultSet result = query.getResultSet();
                        return result.next() ? result.getInt("value") : 0;
                    }, 0);
            if (value != 0) {
                this.statsCache.put(statsType.id(), value);
            } else {
                this.statsCache.remove(statsType.id());
            }
            Main.getInstance().callAsync(new StatsAccountSyncedEvent(this));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setValue(StatsType statsType, int value) {
        return doChanges(StatsValueChangeEvent.ChangeType.SET, statsType, value, (before, change) -> change);
    }

    @Override
    public boolean decreaseValue(StatsType statsType, int value) {
        return doChanges(StatsValueChangeEvent.ChangeType.DECREASE, statsType, value, (before, change) -> before - change);
    }

    @Override
    public boolean increaseValue(StatsType statsType, int value) {
        return doChanges(StatsValueChangeEvent.ChangeType.INCREASE, statsType, value, Integer::sum);
    }

    @Override
    public int getStatsValue(StatsType statsType) {
        return this.statsCache.getOrDefault(statsType.id(), 0);
    }

    protected boolean doChanges(@NotNull StatsValueChangeEvent.ChangeType changeType,
                                @NotNull StatsType statsType, int changeValue,
                                @NotNull BiFunction<Integer, Integer, Integer> function) {
        CompletableFuture<StatsValuePreChangeEvent> future = Main.getInstance().callAsync(
                new StatsValuePreChangeEvent(this, changeType, statsType, changeValue)
        );
        
        try {
            StatsValuePreChangeEvent event = future.get();
            if (event.isCancelled()) return false;

            int before = this.getStatsValue(statsType);
            int after = function.apply(before, event.getValue());

            this.statsCache.put(statsType.id(), after);
            this.modifiedStats.add(statsType.id());

            Main.getInstance().callAsync(new StatsValueChangedEvent(this, changeType, statsType, changeValue));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
