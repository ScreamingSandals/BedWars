package org.screamingsandals.bedwars.tab;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service(dependsOn = {
        MainConfig.class,
        PlayerMapper.class
})
@RequiredArgsConstructor
public class TabManager {
    private final MainConfig mainConfig;

    private List<String> header;
    private List<String> footer;

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("tab", "enabled").getBoolean();
    }

    public static TabManager getInstance() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("TabManager are not enabled!");
        }
        return ServiceManager.get(TabManager.class);
    }

    @OnEnable
    public void onEnable() {
        if (mainConfig.node("tab", "header", "enabled").getBoolean()) {
            header = mainConfig.node("tab", "header", "contents").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        }
        if (mainConfig.node("tab", "footer", "enabled").getBoolean()) {
            footer = mainConfig.node("tab", "footer", "contents").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        }
    }

    public void modifyForPlayer(BedWarsPlayer player) {
        if (player.isOnline() && (header != null || footer != null)) {
            if (header != null) {
                player.sendPlayerListHeader(translate(player, header));
            } else {
                player.sendPlayerListHeader(Component.empty());
            }
            if (header != null) {
                player.sendPlayerListFooter(translate(player, footer));
            } else {
                player.sendPlayerListFooter(Component.empty());
            }
        }
    }

    public void clear(BedWarsPlayer player) {
        if (player.isOnline() && (header != null || footer != null)) {
            player.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
        }
    }

    public Component translate(BedWarsPlayer gamePlayer, List<String> origin) {
        var component = Component.text();
        var first = new AtomicBoolean(true);
        origin.forEach(a -> {
            if (!first.get()) {
                component.append(Component.text("\n"));
            } else {
                first.set(false);
            }
            component.append(
                    AdventureHelper.toComponent(
                            a.replace("%players%", String.valueOf(gamePlayer.getGame().countPlayers()))
                                    .replace("%alive%", String.valueOf(gamePlayer.getGame().countAlive()))
                                    .replace("%spectating%", String.valueOf(gamePlayer.getGame().countSpectating()))
                                    .replace("%spectators%", String.valueOf(gamePlayer.getGame().countSpectators()))
                                    .replace("%respawnable%", String.valueOf(gamePlayer.getGame().countRespawnable()))
                                    .replace("%max%", String.valueOf(gamePlayer.getGame().getMaxPlayers()))
                                    .replace("%map%", gamePlayer.getGame().getName()))
            );
        });
        return component.asComponent();
    }
}
