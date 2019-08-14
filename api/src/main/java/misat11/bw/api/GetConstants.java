package misat11.bw.api;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Bedwars Team
 *
 */
public class GetConstants {
	private static YamlConfiguration yaml;
	
	/**
	 * @return
	 */
	public static YamlConfiguration loadConfig() {
		if (yaml == null) {
			yaml = new YamlConfiguration();
			try {
				yaml.load(new InputStreamReader(GetConstants.class.getResourceAsStream("/misat11/bw/api/constants.yml"), StandardCharsets.UTF_8));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return yaml;
	}
}
