/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.econ;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.Preconditions;

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
