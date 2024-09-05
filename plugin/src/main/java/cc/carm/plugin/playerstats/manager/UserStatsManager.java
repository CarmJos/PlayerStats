package cc.carm.plugin.playerstats.manager;

import cc.carm.lib.easyplugin.utils.MessageUtils;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.minecraft.common.MineCommonAPI;
import cc.carm.lib.minecraft.common.bukkit.BukkitCommonAPI;
import cc.carm.lib.minecraft.common.exception.handler.HandlerOnRegisterException;
import cc.carm.lib.minecraft.common.exception.handler.HandlerRegisteredException;
import cc.carm.service.essentials.bukkit.Main;
import cc.carm.service.essentials.bukkit.api.stats.StatsType;
import cc.carm.service.essentials.bukkit.data.DataTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class UserStatsManager implements StatsManager {

    Set<StatsType> statsTypes = new HashSet<>();

    public UserStatsManager(Main plugin) {
        plugin.log("  加载现有战绩类型...");
        int i = updateStatsTypes(); // 从数据库中读取所有货币
        plugin.log("  从数据库中加载了 " + i + " 种类型");

        plugin.log("  注册用户战绩管理器");
        try {
            BukkitCommonAPI.getUserManager().registerHandler(plugin, UserStatsAccount.class);
        } catch (HandlerRegisteredException | HandlerOnRegisterException e) {
            e.printStackTrace();
        }

        plugin.log("  注册变量...");
        if (MessageUtils.hasPlaceholderAPI()) {
            new StatsExpansion(plugin).register();
        }
    }

    @Override
    public @NotNull Set<StatsType> getStatsTypes() {
        return Collections.unmodifiableSet(statsTypes);
    }

    @Override
    public int updateStatsTypes() {
        Set<StatsType> data = new HashSet<>();
        try (SQLQuery query = getTypesTable().createQuery().build().execute()) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                data.add(readStatsType(result));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.statsTypes = data;
        return this.statsTypes.size();
    }

    @Override
    public void updateStatsType(int id) {
        try (SQLQuery query = getTypesTable().createQuery()
                .addCondition("id", id)
                .setLimit(1).build().execute()) {
            ResultSet result = query.getResultSet();
            if (result.next()) {
                StatsType type = readStatsType(result);
                removeStatsType(type); // Remove old currency
                addStatsType(type);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public @NotNull StatsType createStatsType(@NotNull String key, String name, @Nullable String description) throws Exception {
        StatsType registered = getStatsType(key);
        if (registered != null) return registered;

        int id = getTypesTable().createReplace()
                .setColumnNames("key", "name", "description")
                .setParams(key, name, description)
                .returnGeneratedKey().execute();

        StatsType type = new StatsType(id, key, name, description);
        MineCommonAPI.getRedisManager().publishAsync("stats.type.create", d -> d.writeInt(type.id()));
        this.statsTypes.add(type);
        return type;
    }

    @Override
    public boolean addStatsType(@NotNull StatsType statsType) {
        if (hasStatsType(statsType.id()) || hasStatsType(statsType.key())) return false;
        this.statsTypes.add(statsType);
        return true;
    }

    @Override
    public void removeStatsType(@NotNull StatsType statsType) {
        this.statsTypes.removeIf(c -> c.id() == statsType.id() || c.key().equalsIgnoreCase(statsType.key()));
    }

    @Override
    public StatsType getStatsType(int id) {
        return this.statsTypes.stream().filter(c -> c.id() == id).findFirst().orElse(null);
    }

    @Override
    public StatsType getStatsType(@NotNull String key) {
        return this.statsTypes.stream().filter(c -> c.key().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    @Override
    public boolean hasStatsType(@NotNull String key) {
        return this.statsTypes.stream().anyMatch(c -> c.key().equalsIgnoreCase(key));
    }

    @Override
    public boolean hasStatsType(int id) {
        return this.statsTypes.stream().anyMatch(c -> c.id() == id);
    }


    @Override
    public SQLTable getTypesTable() {
        return DataTables.STATS_TYPE;
    }

    @Override
    public SQLTable getAccountsTable() {
        return DataTables.STATS_ACCOUNT;
    }

    @Override
    public LinkedHashMap<Long, Integer> getOrderedAccounts(StatsType stats, int limit) {
        LinkedHashMap<Long, Integer> data = new LinkedHashMap<>();
        try (SQLQuery query = MineCommonAPI.getSQLRegistry().get().createQuery().withPreparedSQL(
                "SELECT * FROM " + DataTables.STATS_ACCOUNT.getTableName() + " " +
                        "WHERE type = ? ORDER BY value DESC LIMIT " + limit
        ).setParams(stats.id()).execute()) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                data.put(result.getLong("uid"), result.getInt("value"));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return data;
    }

    protected StatsType readStatsType(ResultSet result) throws SQLException {
        return new StatsType(
                result.getInt("id"),
                result.getString("key"),
                result.getString("name"),
                result.getString("description")
        );
    }

}
