package org.screamingsandals.lib.signmanager;

import java.util.List;

import org.bukkit.entity.Player;

public interface SignOwner {
	boolean isNameExists(String name);
	
	void updateSign(SignBlock sign);
	
	List<String> getSignPrefixes();
	
	void onClick(Player player, SignBlock sign);
	
	List<String> getSignCreationPermissions();
	
	String returnTranslate(String key);
}
