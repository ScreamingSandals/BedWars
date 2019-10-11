package misat11.bw.api.boss;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * @author Bedwars Team
 */
public interface BossBar19 extends BossBar {
    /**
     * @return color
     */
	BarColor getColor();

    /**
     * @param color
     */
	void setColor(BarColor color);

    /**
     * @return style
     */
	BarStyle getStyle();

    /**
     * @param style
     */
	void setStyle(BarStyle style);
}
