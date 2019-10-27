package org.screamingsandals.bedwars.boss;

import org.screamingsandals.bedwars.api.boss.BossBar;

public class BossBarSelector {
    public static BossBar getBossBar() {
        try {
            return new BossBar19();
        } catch (Throwable t) {
            return new BossBar18();
        }
    }
}
