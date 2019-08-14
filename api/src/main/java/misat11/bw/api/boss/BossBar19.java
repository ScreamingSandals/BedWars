package misat11.bw.api.boss;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * @author Bedwars Team
 *
 */
public interface BossBar19 extends BossBar {
	/**
	 * @return color
	 */
	public BarColor getColor();
	
	/**
	 * @param color
	 */
	public void setColor(BarColor color);
	
	/**
	 * @return style
	 */
	public BarStyle getStyle();
	
	/**
	 * @param style
	 */
	public void setStyle(BarStyle style);
}
