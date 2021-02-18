package org.screamingsandals.bedwars.lib.signmanager;

import java.util.List;

import org.bukkit.entity.Player;
import org.screamingsandals.lib.sender.permissions.Permission;

public interface SignOwner {
	boolean isNameExists(String name);
	
	void updateSign(SignBlock sign);
	
	List<String> getSignPrefixes();
	
	void onClick(Player player, SignBlock sign);
	
	Permission getSignCreationPermissions();
	
	String returnTranslate(String key);
}
