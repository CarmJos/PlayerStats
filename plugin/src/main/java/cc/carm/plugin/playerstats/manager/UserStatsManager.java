package cc.carm.plugin.playerstats.manager;

import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.plugin.playerstats.data.StatType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class UserStatsManager implements StatsManager {

    Set<StatType> statTypes = new HashSet<>();

    @Override
    public @NotNull Set<StatType> registry() {
        return Collections.unmodifiableSet(statTypes);
    }

    @Override
    public int updateStatsTypes() {
        Set<StatType> data = new HashSet<>();
        try (SQLQuery query = getTypesTable().createQuery().build().execute()) {
            ResultSet result = query.getResultSet();
            while (result.next()) {
                data.add(readStatsType(result));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.statTypes = data;
        return this.statTypes.size();
    }

    @Override
    public void updateStatsType(int id) {
        try (SQLQuery query = getTypesTable().createQuery()
                .addCondition("id", id)
                .setLimit(1).build().execute()) {
            ResultSet result = query.getResultSet();
            if (result.next()) {
                StatType type = readStatsType(result);
                removeStatsType(type); // Remove old currency
                addStatsType(type);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public @NotNull StatType createStatsType(@NotNull String key, String name, @Nullable String description) throws Exception {
        StatType registered = getStatsType(key);
        if (registered != null) return registered;

        int id = getTypesTable().createReplace()
                .setColumnNames("key", "name", "description")
                .setParams(key, name, description)
                .returnGeneratedKey().execute();

        StatType type = new StatType(id, key, name, description);
        MineCommonAPI.getRedisManager().publishAsync("stats.type.create", d -> d.writeInt(type.id()));
        this.statTypes.add(type);
        return type;
    }

    @Override
    public boolean addStatsType(@NotNull StatType statType) {
        if (hasStatsType(statType.id()) || hasStatsType(statType.key())) return false;
        this.statTypes.add(statType);
        return true;
    }

    @Override
    public void removeStatsType(@NotNull StatType statType) {
        this.statTypes.removeIf(c -> c.id() == statType.id() || c.key().equalsIgnoreCase(statType.key()));
    }

    @Override
    public StatType getStatsType(int id) {
        return this.statTypes.stream().filter(c -> c.id() == id).findFirst().orElse(null);
    }

    @Override
    public StatType getStatsType(@NotNull String key) {
        return this.statTypes.stream().filter(c -> c.key().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    @Override
    public boolean hasStatsType(@NotNull String key) {
        return this.statTypes.stream().anyMatch(c -> c.key().equalsIgnoreCase(key));
    }

    @Override
    public boolean hasStatsType(int id) {
        return this.statTypes.stream().anyMatch(c -> c.id() == id);
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
    public LinkedHashMap<Long, Integer> getOrderedAccounts(StatType stats, int limit) {
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

    protected StatType readStatsType(ResultSet result) throws SQLException {
        return new StatType(
                result.getInt("id"),
                result.getString("key"),
                result.getString("name"),
                result.getString("description")
        );
    }

}
