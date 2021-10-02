package org.screamingsandals.bedwars.utils;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
public class VaultUtils {
    private Economy econ;
    private Chat chat;
    @Getter
    private boolean isVault;

    public static VaultUtils getInstance() {
        return ServiceManager.get(VaultUtils.class);
    }

    @OnPostEnable
    public void postEnable() {
        if (!PluginManager.isEnabled(PluginManager.createKey("Vault").orElseThrow())) {
            isVault = false;
        } else {
            var plugin = PluginManager.getPlugin(PluginManager.createKey("Vault").orElseThrow());
            if (plugin.isEmpty()) {
                isVault = false;
            } else {
                var rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    isVault = false;
                } else {
                    econ = rsp.getProvider();
                    isVault = true;

                    PlayerMapper.getConsoleSender().sendMessage(
                            Component
                                    .text("[B")
                                    .color(NamedTextColor.RED)
                                    .append(
                                            Component
                                                    .text("W] ")
                                                    .color(NamedTextColor.WHITE)
                                    )
                                    .append(
                                            Component
                                                    .text("Found Vault")
                                                    .color(NamedTextColor.GOLD)
                                    ));
                }
            }
        }
    }

    public void depositPlayer(PlayerWrapper player, double coins) {
        try {
            if (isVault() && MainConfig.getInstance().node("vault", "enabled").getBoolean()) {
                var response = econ.depositPlayer(player.as(Player.class), coins);
                if (response.transactionSuccess()) {
                    Message
                            .of(LangKeys.IN_GAME_VAULT_DEPOSITE)
                            .defaultPrefix()
                            .placeholder("coins", coins)
                            .placeholder("currency",  (coins == 1 ? econ.currencyNameSingular() : econ.currencyNamePlural()))
                            .send(player);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public String getPrefix(PlayerWrapper player) {
        if (chat == null) {
            chat = Bukkit.getServer().getServicesManager().load(Chat.class);
        }
        return chat.getPlayerPrefix(player.as(Player.class));
    }

    public String getSuffix(PlayerWrapper player) {
        if (chat == null) {
            chat = Bukkit.getServer().getServicesManager().load(Chat.class);
        }
        return chat.getPlayerSuffix(player.as(Player.class));
    }
}
