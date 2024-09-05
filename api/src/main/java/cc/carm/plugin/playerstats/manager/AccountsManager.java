package cc.carm.plugin.playerstats.manager;

import cc.carm.lib.minecraft.common.bukkit.BukkitCommonAPI;
import cc.carm.lib.minecraft.common.user.data.UserDataHandler;
import cc.carm.service.essentials.bukkit.api.stats.StatsAccount;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface AccountsManager {

    default @Nullable StatsAccount getAccount(@NotNull UUID userUUID) {
        return UserDataHandler.get(userUUID, StatsAccount.class);
    }

    default @NotNull StatsAccount getAccount(@NotNull Player player) {
        return BukkitCommonAPI.getUserManager().get(player).getHandler(StatsAccount.class);
    }

}
