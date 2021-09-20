package org.screamingsandals.bedwars.api.boss;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
@ApiStatus.NonExtendable
public interface BossBar<P extends Wrapper> extends StatusBar<P> {
    /**
     * @return current message
     */
	Wrapper getMessage();

    /**
     * @param message
     */
	void setMessage(@Nullable Object message);

    /**
     * @return color
     */
    Wrapper getColor();

    /**
     * @param color
     */
    void setColor(@NotNull Object color);

    /**
     * @return style
     */
    Wrapper getStyle();

    /**
     * @param style
     */
    void setStyle(@NotNull Object style);

}
