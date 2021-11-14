package org.screamingsandals.bedwars.econ;

import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;

public abstract class Economy {
    public void deposit(PlayerWrapper player, double coins) {
        if (MainConfig.getInstance().node("economy", "enabled").getBoolean()) {
            if (deposit0(player, coins)) {
                Message.of(LangKeys.IN_GAME_ECONOMY_DEPOSITED)
                        .defaultPrefix()
                        .placeholder("coins", coins)
                        .placeholder("currency", currencyName())
                        .send(player);
            }
        }
    }

    // implementations

    protected abstract boolean deposit0(PlayerWrapper player, double coins);

    public abstract boolean withdraw(PlayerWrapper player, double coins);

    public abstract String currencyName();
}
