package misat11.bw.api.special;

import org.bukkit.entity.LivingEntity;

public interface Golem extends SpecialItem {

	public LivingEntity getEntity();

	public boolean isUsed();

	public double getSpeed();

	public double getFollowRange();

}
