package org.screamingsandals.bedwars.lib.nms.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.lib.nms.accessors.CompoundTagAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.ItemStackAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.MinecraftServerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.TagParserAccessor;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class TagApplier {
    public static @NotNull ItemStack applyTag(@NotNull ItemStack stack, @NotNull String tag, int dataVersion) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object parsedTag = ClassStorage.getMethod(TagParserAccessor.METHOD_PARSE_COMPOUND_FULLY.get()).invokeStatic(tag);

        if (parsedTag == null) {
            return stack;
        }

        if (!ClassStorage.CB.CraftItemStack.isInstance(stack)) {
            stack = ClassStorage.asCBStack(stack);
        }

        // TODO: use DFU in case DataVersion is provided

        if (ItemStackAccessor.METHOD_PARSE.get() != null) {
            // 1.20.5+
            if (stack.getType().isAir()) {
                throw new UnsupportedOperationException("Cannot apply tag to AIR.");
            }

            Object compound = CompoundTagAccessor.CONSTRUCTOR_0.get().newInstance();
            ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_STRING.get()).invoke("id", stack.getType().getKey().toString());
            ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_INT.get()).invoke("count", stack.getAmount());
            ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT.get()).invoke("components", parsedTag);

            Object optional = ClassStorage.getMethod(ItemStackAccessor.METHOD_PARSE.get()).invokeStatic(
                    ClassStorage.getMethod(ClassStorage.getMethod(Bukkit.getServer(), "getServer").invoke(), MinecraftServerAccessor.METHOD_REGISTRY_ACCESS.get()).invoke(),
                    compound
            );
            if (optional instanceof Optional) {
                @NotNull ItemStack finalStack = stack;
                return ClassStorage.nmsAsStack(((Optional<?>) optional).orElseThrow(() ->
                        new IllegalArgumentException("The given tag is not applicable to the item of type " + finalStack.getType().getKey() + ": " + tag))
                );
            }
        } else {
            // 1.8.8-1.20.4
            ClassStorage.getMethod(
                    ClassStorage.getHandleOfItemStack(stack),
                    ItemStackAccessor.METHOD_SET_TAG.get()
            ).invoke(parsedTag);
        }
        return stack;
    }
}
