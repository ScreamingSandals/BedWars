package org.screamingsandals.bedwars.api.special;

import org.bukkit.entity.LivingEntity;

/**
 * @author Bedwars Team
 */
public interface Golem extends SpecialItem {

    /**
     * @return
     */
    LivingEntity getEntity();

    /**
     * @return
     */
    double getSpeed();

    /**
     * @return
     */
    double getFollowRange();

}
