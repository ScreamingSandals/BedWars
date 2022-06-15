package org.screamingsandals.bedwars.lobby;

import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.econ.Economy;
import org.screamingsandals.bedwars.econ.EconomyProvider;
import org.screamingsandals.bedwars.econ.VaultEconomy;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerChatEvent;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.mini.placeholders.Placeholder;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.screamingsandals.lib.utils.annotations.parameters.ProvidedBy;

@Service(dependsOn = {
        MainConfig.class
})
public class LobbyChatManager {
    private String format;
    private String world;
    private Economy economy;

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("main-lobby", "custom-chat", "enabled").getBoolean() && MainConfig.getInstance().node("main-lobby", "enabled").getBoolean();
    }

    @OnPostEnable
    public void enable(MainConfig mainConfig, @ProvidedBy(EconomyProvider.class) Economy economy) {
        format = mainConfig.node("main-lobby", "custom-chat", "format").getString("<level-prefix> <name>: <message>");
        world = mainConfig.node("main-lobby", "world").getString("");
        this.economy = economy;
    }

    @OnEvent
    public void onChat(SPlayerChatEvent event) {
        var player = event.player();

        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        if (world.isEmpty() || !player.getLocation().getWorld().getName().equals(world)) {
            return; // :(
        }

        var prefix = Component.empty();
        var suffix = Component.empty();

        if (economy != null && economy instanceof VaultEconomy) {
            prefix = Component.fromLegacy(((VaultEconomy) economy).getPrefix(player));
            suffix = Component.fromLegacy(((VaultEconomy) economy).getSuffix(player));
        }

        if (PlayerStatisticManager.isEnabled()) {
            var statistic = PlayerStatisticManager.getInstance().getStatistic(player);
            event.format(Component.fromMiniMessage(format,
                    Placeholder.component("name", player.getDisplayName()),
                    Placeholder.unparsed("message", event.message()),
                    Placeholder.component("level-prefix", Component.text("[" + statistic.getLevel() + "\u272B]", Color.GRAY)), // TODO: use prestiges when they are implemented
                    Placeholder.number("level", statistic.getLevel()),
                    Placeholder.component("prefix", prefix),
                    Placeholder.component("suffix", suffix)
            ).toLegacy());
        } else {
            event.format(Component.fromMiniMessage(format,
                    Placeholder.component("name", player.getDisplayName()),
                    Placeholder.unparsed("message", event.message()),
                    Placeholder.component("prefix", prefix),
                    Placeholder.component("suffix", suffix)
            ).toLegacy()); // TODO: why is this still legacy?
        }
    }
}
