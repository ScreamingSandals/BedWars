package org.screamingsandals.bedwars.api.boss;

import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface XPBar<P extends Wrapper> extends StatusBar<P> {
    /**
     * @param seconds
     */
    void setSeconds(int seconds);

    /**
     * @return seconds
     */
    int getSeconds();

}
