package org.screamingsandals.bedwars.econ;

import com.google.common.base.Preconditions;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.screamingsandals.lib.player.PlayerWrapper;

public class VaultEconomy extends Economy {
    private final net.milkbowl.vault.economy.Economy vaultEcon;
    private final Chat vaultChat;

    public VaultEconomy() {
        var econProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (econProvider != null) {
            vaultEcon = econProvider.getProvider();
        } else {
            vaultEcon = Bukkit.getServer().getServicesManager().load(net.milkbowl.vault.economy.Economy.class);
        }
        var chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            vaultChat = chatProvider.getProvider();
        } else {
            vaultChat = Bukkit.getServer().getServicesManager().load(Chat.class);
        }
        Preconditions.checkNotNull(vaultEcon, "Vault economy could not be loaded!");
        Preconditions.checkNotNull(vaultChat, "Vault chat could not be loaded!");
    }

    @Override
    public boolean deposit0(PlayerWrapper player, double coins) {
        return vaultEcon.depositPlayer(player.as(Player.class), coins).transactionSuccess();
    }

    @Override
    public boolean withdraw(PlayerWrapper player, double coins) {
        return vaultEcon.withdrawPlayer(player.as(Player.class), coins).transactionSuccess();
    }

    @Override
    public String currencyName() {
        return vaultEcon.currencyNameSingular();
    }

    public String getPrefix(PlayerWrapper player) {
        return vaultChat.getPlayerPrefix(player.as(Player.class));
    }

    public String getSuffix(PlayerWrapper player) {
        return vaultChat.getPlayerSuffix(player.as(Player.class));
    }
}
