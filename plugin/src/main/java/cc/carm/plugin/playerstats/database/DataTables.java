package cc.carm.plugin.playerstats.database;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.ForeignKeyRule;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.enums.NumberType;
import cc.carm.lib.minecraft.common.MineCommonAPI;
import cc.carm.lib.minecraft.common.data.CommonTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public enum DataTables implements SQLTable {

    STATS_TYPE("data", "stats_types", table -> {
        table.addAutoIncrementColumn("id", NumberType.TINYINT, true, true);
        table.addColumn("key", "VARCHAR(32) NOT NULL UNIQUE KEY");
        table.addColumn("name", "VARCHAR(32)");
        table.addColumn("description", "TEXT");
    }),

    STATS_ACCOUNT("data", "user_stats", table -> {
        table.addColumn("uid", "INT UNSIGNED NOT NULL");
        table.addColumn("type", "TINYINT UNSIGNED NOT NULL");
        table.addColumn("value", "INT NOT NULL DEFAULT 0");
        table.setIndex(IndexType.PRIMARY_KEY, "pk_user_stats", "uid", "type");
        table.addForeignKey(
                "uid", "fk_user_stats",
                CommonTables.USERS.getTableName(), "id",
                ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE
        );
        table.addForeignKey(
                "type", "fk_stats_type",
                STATS_TYPE.getTableName(), "id",
                ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE
        );
    });

    private final @NotNull String database;
    private final @Nullable String tableName;

    private final @Nullable Consumer<TableCreateBuilder> builder;

    private SQLManager manager;

    DataTables(@NotNull String database, @Nullable String tableName,
               @Nullable Consumer<TableCreateBuilder> builder) {
        this.database = database;
        this.tableName = tableName;
        this.builder = builder;
    }

    public boolean create(@NotNull SQLManager sqlManager) throws SQLException {
        if (this.manager == null) this.manager = sqlManager;

        TableCreateBuilder tableBuilder = sqlManager.createTable(getTableName());
        if (builder != null) builder.accept(tableBuilder);
        return tabluilder.build().executeFunction(l -> l > 0, false);
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
                value.create(MineCommonAPI.getSQLRegistry().getNotNull(value.database));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
