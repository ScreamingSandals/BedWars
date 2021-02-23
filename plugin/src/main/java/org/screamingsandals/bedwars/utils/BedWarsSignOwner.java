package org.screamingsandals.bedwars.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lib.signmanager.SignBlock;
import org.screamingsandals.bedwars.lib.signmanager.SignOwner;
import org.screamingsandals.lib.sender.permissions.Permission;
import org.spongepowered.configurate.ConfigurationNode;

import static org.screamingsandals.bedwars.lib.lang.I.*;

public class BedWarsSignOwner implements SignOwner {

    @Override
    public boolean isNameExists(String name) {
        return GameManager.getInstance().hasGame(name) || name.equalsIgnoreCase("leave");
    }

    @Override
    public void updateSign(SignBlock sign) {
        var name = sign.getName();
        GameManager.getInstance().getGame(name).ifPresentOrElse(game ->
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                game.updateSigns();
                            }
                        }.runTask(Main.getInstance().getPluginDescription().as(JavaPlugin.class)),
                () -> {
                    if ("leave".equalsIgnoreCase(name)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                updateLeaveSign(sign);
                            }
                        }.runTask(Main.getInstance().getPluginDescription().as(JavaPlugin.class));
                    }
                });
    }

    private void updateLeaveSign(SignBlock sign) {
        List<String> texts = MainConfig.getInstance().node("sign", "lines").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

        var opt = sign.getLocation().asOptional(Location.class);
        if (opt.isPresent()) {
            Block block = opt.get().getBlock();
            if (block.getState() instanceof Sign) {
                Sign state = (Sign) block.getState();

                for (int i = 0; i < texts.size(); i++) {
                    String text = texts.get(i);
                    state.setLine(i, text.replaceAll("%arena%", i18nonly("leave_from_game_item")).replaceAll("%status%", "")
                            .replaceAll("%players%", ""));
                }

                state.update();
            }
        }
    }

    @Override
    public List<String> getSignPrefixes() {
        return Arrays.asList("[bedwars]", "[bwgame]");
    }

    @Override
    public void onClick(Player player, SignBlock sign) {
        if (sign.getName().equalsIgnoreCase("leave")) {
            if (Main.isPlayerInGame(player)) {
                Main.getPlayerGameProfile(player).changeGame(null);
            }
        } else {
            GameManager.getInstance().getGame(sign.getName()).ifPresentOrElse(
                    game -> game.joinToGame(player),
                    () -> m("sign_game_not_exists").send(player)
            );
        }
    }

    @Override
    public Permission getSignCreationPermissions() {
        return BedWarsPermission.ADMIN_PERMISSION.asPermission();
    }

    @Override
    public String returnTranslate(String key) {
        return i18n(key);
    }

}
