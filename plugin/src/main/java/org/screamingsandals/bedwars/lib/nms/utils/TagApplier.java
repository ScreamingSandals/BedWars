package org.screamingsandals.bedwars.lib.nms.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.nms.accessors.CompoundTagAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.DataConverterManagerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.HolderLookup$ProviderAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.ItemAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.ItemStackAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.MappedRegistryAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.MinecraftServerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.NbtOpsAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.ReferencesAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.TagParserAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public class TagApplier {
    private static final Class<?> DATAFIXER = ClassStorage.safeGetClass("com.mojang.datafixers.DataFixer");
    private static final Method UPDATE_METHOD = DATAFIXER != null ? Arrays.stream(DATAFIXER.getMethods()).filter(method -> "update".equals(method.getName())).findFirst().orElse(null) : null;
    private static final Class<?> DYNAMIC = ClassStorage.safeGetClass("com.mojang.serialization.Dynamic", "com.mojang.datafixers.Dynamic");
    private static final Constructor<?> DYNAMIC_CONSTRUCTOR = DYNAMIC != null ? Arrays.stream(DYNAMIC.getConstructors()).filter(constructor -> constructor.getParameterCount() == 2).findFirst().orElse(null) : null;
    private static final Class<?> DYNAMIC_OPS = ClassStorage.safeGetClass("com.mojang.serialization.DynamicOps");

    public static @NotNull ItemStack applyTag(@NotNull ItemStack stack, @NotNull String tag, int dataVersion) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object parsedTag = ClassStorage.getMethod(TagParserAccessor.METHOD_PARSE_COMPOUND_FULLY.get()).invokeStatic(tag);

        if (parsedTag == null) {
            return stack;
        }

        if (!ClassStorage.CB.CraftItemStack.isInstance(stack)) {
            stack = ClassStorage.asCBStack(stack);
        }
        // Applying CompoundTag with conversion
        conversion:
        if (dataVersion != 0 && Version.isVersion(1, 9)) {
            if (Version.isVersion(1, 13)) {
                // 1.13+
                int currentVersion = Bukkit.getUnsafe().getDataVersion();

                if (UPDATE_METHOD == null || DYNAMIC_CONSTRUCTOR == null || dataVersion >= currentVersion) {
                    break conversion;
                }

                Object compound = CompoundTagAccessor.CONSTRUCTOR_0.get().newInstance();
                ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_STRING.get()).invoke("id", stack.getType().getKey().toString());
                ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_INT.get()).invoke(dataVersion > 3837 ? "count" : "Count", stack.getAmount());
                ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT.get()).invoke(dataVersion > 3837 ? "components" : "tag", parsedTag);

                Object mcServer = ClassStorage.getMethod(Bukkit.getServer(), "getServer").invoke();
                Object fixerUpper = ClassStorage.getField(mcServer, MinecraftServerAccessor.FIELD_FIXER_UPPER.get());
                Object result = ClassStorage.getMethod(fixerUpper, UPDATE_METHOD).invoke(ReferencesAccessor.FIELD_ITEM_STACK.get(), DYNAMIC_CONSTRUCTOR.newInstance(NbtOpsAccessor.FIELD_INSTANCE.get(), compound), dataVersion, currentVersion);
                compound = ClassStorage.getMethod(result, "getValue").invoke();

                if (ItemStackAccessor.METHOD_PARSE.get() != null || Version.isVersion(1, 21, 6)) {
                    Object optional;
                    if (Version.isVersion(1, 21, 6)) {
                        Object codecRes = ClassStorage.getMethod(ItemStackAccessor.FIELD_CODEC.get(), "parse", DYNAMIC_OPS, Object.class).invoke(
                                ClassStorage
                                        .getMethod(
                                                ClassStorage.getMethod(ClassStorage.getMethod(Bukkit.getServer(), "getServer").invoke(), MinecraftServerAccessor.METHOD_REGISTRY_ACCESS.get()).invoke(),
                                                HolderLookup$ProviderAccessor.METHOD_CREATE_SERIALIZATION_CONTEXT.get()
                                        )
                                        .invoke(NbtOpsAccessor.FIELD_INSTANCE.get()),
                                compound
                        );
                        optional = ClassStorage.getMethod(codecRes, "resultOrPartial", Consumer.class)
                                .invoke((Consumer<String>) string -> Main.getInstance().getLogger().warning("Tried to load invalid item: '" + string + "'"));
                    } else {
                        optional = ClassStorage.getMethod(ItemStackAccessor.METHOD_PARSE.get()).invokeStatic(
                                ClassStorage.getMethod(ClassStorage.getMethod(Bukkit.getServer(), "getServer").invoke(), MinecraftServerAccessor.METHOD_REGISTRY_ACCESS.get()).invoke(),
                                compound
                        );
                    }
                    if (optional instanceof Optional) {
                        @NotNull ItemStack finalStack = stack;
                        return ClassStorage.nmsAsStack(((Optional<?>) optional).orElseThrow(() ->
                                new IllegalArgumentException("The given tag is not applicable to the item of type " + finalStack.getType().getKey() + ": " + tag))
                        );
                    }
                } else {
                    ClassStorage.getMethod(
                            ClassStorage.getHandleOfItemStack(stack),
                            ItemStackAccessor.METHOD_SET_TAG.get()
                    ).invoke(ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_GET.get()).invoke("tag"));
                }
            } else {
                Object mcServer = ClassStorage.getMethod(Bukkit.getServer(), "getServer").invoke();
                Object fixerUpper = ClassStorage.getField(mcServer, MinecraftServerAccessor.FIELD_DATA_CONVERTER_MANAGER.get());
                Integer currentVersion = (Integer) ClassStorage.getField(fixerUpper, DataConverterManagerAccessor.FIELD_FIELD_188262_D.get());
                if (currentVersion == null || dataVersion >= currentVersion) {
                    break conversion;
                }

                // 1.9-1.12.2
                Object compound = CompoundTagAccessor.CONSTRUCTOR_0.get().newInstance();
                ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_STRING.get()).invoke("id", ClassStorage.getMethod(ItemAccessor.FIELD_REGISTRY.get(), MappedRegistryAccessor.METHOD_FUNC_177774_C.get()).invoke(ClassStorage.getMethod(ClassStorage.getHandleOfItemStack(stack), ItemStackAccessor.METHOD_GET_ITEM.get()).invoke()).toString());
                ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_INT.get()).invoke("Count", stack.getAmount());
                ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT.get()).invoke("tag", parsedTag);

                compound = ClassStorage.getMethod(fixerUpper, DataConverterManagerAccessor.METHOD_FUNC_188251_A.get()).invoke(ReferencesAccessor.FIELD_ITEM_STACK.get(), compound, dataVersion);

                ClassStorage.getMethod(
                        ClassStorage.getHandleOfItemStack(stack),
                        ItemStackAccessor.METHOD_SET_TAG.get()
                ).invoke(ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_GET.get()).invoke("tag"));
            }
            return stack;
        }

        if (ItemStackAccessor.METHOD_PARSE.get() != null || Version.isVersion(1, 21, 6)) {
            // 1.20.5+
            if (stack.getType().isAir()) {
                throw new UnsupportedOperationException("Cannot apply tag to AIR.");
            }

            Object compound = CompoundTagAccessor.CONSTRUCTOR_0.get().newInstance();
            ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_STRING.get()).invoke("id", stack.getType().getKey().toString());
            ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT_INT.get()).invoke("count", stack.getAmount());
            ClassStorage.getMethod(compound, CompoundTagAccessor.METHOD_PUT.get()).invoke("components", parsedTag);

            Object optional;
            if (Version.isVersion(1, 21, 6)) {
                Object codecRes = ClassStorage.getMethod(ItemStackAccessor.FIELD_CODEC.get(), "parse", DYNAMIC_OPS, Object.class).invoke(
                        ClassStorage
                                .getMethod(
                                        ClassStorage.getMethod(ClassStorage.getMethod(Bukkit.getServer(), "getServer").invoke(), MinecraftServerAccessor.METHOD_REGISTRY_ACCESS.get()).invoke(),
                                        HolderLookup$ProviderAccessor.METHOD_CREATE_SERIALIZATION_CONTEXT.get()
                                )
                                .invoke(NbtOpsAccessor.FIELD_INSTANCE.get()),
                        compound
                );
                optional = ClassStorage.getMethod(codecRes, "resultOrPartial", Consumer.class)
                        .invoke((Consumer<String>) string -> Main.getInstance().getLogger().warning("Tried to load invalid item: '" + string + "'"));
            } else {
                optional = ClassStorage.getMethod(ItemStackAccessor.METHOD_PARSE.get()).invokeStatic(
                        ClassStorage.getMethod(ClassStorage.getMethod(Bukkit.getServer(), "getServer").invoke(), MinecraftServerAccessor.METHOD_REGISTRY_ACCESS.get()).invoke(),
                        compound
                );
            }
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
