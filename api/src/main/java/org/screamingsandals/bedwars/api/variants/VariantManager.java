package org.screamingsandals.bedwars.api.variants;

import java.util.List;
import java.util.Optional;

/**
 * Manages all BedWars variants
 *
 * @author Screaming Sandals
 * @since 0.3.0
 */
public interface VariantManager {
    Optional<? extends Variant> getVariant(String name);

    List<String> getVariantNames();

    List<? extends Variant> getVariants();

    boolean hasVariant(String name);
}
