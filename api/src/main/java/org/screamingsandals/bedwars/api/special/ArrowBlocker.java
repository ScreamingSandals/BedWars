package org.screamingsandals.bedwars.api.special;

/**
 * @author Bedwars Team
 */
public interface ArrowBlocker extends SpecialItem {
    /**
     * @return
     */
    int getProtectionTime();

    /**
     * @return
     */
    int getUsedTime();

    /**
     * @return
     */
    boolean isActivated();

    /**
     *
     */
    void runTask();
}
