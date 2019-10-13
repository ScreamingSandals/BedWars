package org.screamingsandals.bedwars.boss;

import org.screamingsandals.bedwars.api.boss.BossBar;

public class BossBarSelector {
    public static BossBar getBossBar() {
        try {
            BossBar bar = new BossBar19();
            return bar;
        } catch (Throwable t) {
            BossBar bar = new BossBar18();
            return bar;
        }
    }
}
