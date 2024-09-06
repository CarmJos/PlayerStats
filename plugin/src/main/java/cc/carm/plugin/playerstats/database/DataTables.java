package cc.carm.plugin.playerstats.database;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.ForeignKeyRule;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.enums.NumberType;
import cc.carm.plugin.minesql.MineSQL;
import cc.carm.plugin.playerstats.user.UserKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public enum DataTables implements SQLTable {

    USERS("users", (table) -> {
        table.addAutoIncrementColumn(UserKey.KeyType.ID.getColumnName());
        table.addColumn(UserKey.KeyType.UUID.getColumnName(), "CHAR(36) NOT NULL");
        table.addColumn(UserKey.KeyType.NAME.getColumnName(), "VARCHAR(20)");

        table.setIndex(IndexType.INDEX, "idx_user_name", UserKey.KeyType.NAME.getColumnName());
        table.setIndex(IndexType.UNIQUE_KEY, "idx_user_uuid", UserKey.KeyType.UUID.getColumnName());
    }),

    STATS_TYPE("types", table -> {
        table.addAutoIncrementColumn("id", NumberType.MEDIUMINT, true, true);
        table.addColumn("key", "VARCHAR(32) NOT NULL UNIQUE KEY");
        table.addColumn("name", "VARCHAR(32)");
        table.addColumn("description", "TEXT");
    }),

    STATS_ACCOUNT("accounts", table -> {
        table.addColumn("uid", "INT UNSIGNED NOT NULL");
        table.addColumn("type", "MEDIUMINT UNSIGNED NOT NULL");
        table.addColumn("value", "INT NOT NULL DEFAULT 0");
        table.setIndex(IndexType.PRIMARY_KEY, "pk_user_stats", "uid", "type");
        table.addForeignKey(
                "uid", "fk_user_stats",
                USERS.getTableName(), "id",
                ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE
        );
        table.addForeignKey(
                "type", "fk_stat_type",
                STATS_TYPE.getTableName(), "id",
                ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE
        );
    });

    private final @Nullable String tableName;
    private final @Nullable Consumer<TableCreateBuilder> builder;

    private SQLManager manager;

    DataTables(@Nullable String tableName, @Nullable Consumer<TableCreateBuilder> builder) {
        this.tableName = tableName;
        this.builder = builder;
    }

    public boolean create(@NotNull SQLManager sqlManager) throws SQLException {
        if (this.manager == null) this.manager = sqlManager;

        TableCreateBuilder tableBuilder = sqlManager.createTable(getTableName());
        if (builder != null) builder.accept(tableBuilder);
        return tableBuilder.build().executeFunction(l -> l > 0, false);
    }

    @Override
    public @Nullable SQLManager getSQLManager() {
        return this.manager;
    }

    public @NotNull String getTableName() {
        return Optional.ofNullable(this.tableName).orElse(name().toLowerCase(Locale.ROOT));
    }

    public static void initializeTables() {
        for (DataTables value : values()) {
            try {
                value.create(MineSQL.getRegistry().get());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
