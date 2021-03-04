package org.screamingsandals.bedwars.api.boss;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface BossBar<P extends Wrapper> extends StatusBar<P> {
    /**
     * @return current message
     */
	String getMessage();

    /**
     * @param message
     */
	void setMessage(String message);

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
