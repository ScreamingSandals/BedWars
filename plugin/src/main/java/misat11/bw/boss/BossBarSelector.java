package misat11.bw.boss;

import misat11.bw.api.boss.BossBar;

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
