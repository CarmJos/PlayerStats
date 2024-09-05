package cc.carm.plugin.playerstats.manager;

import cc.carm.plugin.playerstats.user.StatsAccount;
import cc.carm.plugin.playerstats.user.UserKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.UUID;

public interface AccountsManager {

    @Unmodifiable
    @NotNull Map<@NotNull UUID, StatsAccount> cached();
    
    @Nullable StatsAccount get(@NotNull UUID userUUID);

    @Nullable StatsAccount get(long uid);

    default @Nullable StatsAccount get(@NotNull UserKey key) {
        return get(key.uuid());
    }

    @NotNull StatsAccount load(@NotNull UUID userUUID, @NotNull String username) throws Exception;

    @NotNull StatsAccount loadTemp(@NotNull UserKey key) throws Exception;

    void unload(@NotNull UUID userUUID);

    void remove(@NotNull UUID userUUID);

}
