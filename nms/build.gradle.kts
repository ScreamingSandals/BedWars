plugins {
    alias(libs.plugins.takenaka)
}

dependencies {
    mappingBundle(libs.takenaka.mappings)
    api(libs.takenaka.runtime)
}

// after any changes in this file, please run the generateAccessors task to see changes in your ide

accessors {
    // uncomment this and remove the mappingBundle dependency,
    // if you want to develop against custom versions
    /*versionRange("1.8.8", "26.1.2") {
        // exclude 1.20, 1.20.3, 1.20.5 and 1.21.2 - hotfixed versions
        // exclude 1.16 and 1.10.1, they don't have most mappings and are basically not used at all
        // exclude 1.8.9, client-only update - no Spigot mappings, no thank you
        // exclude 1.9.1 and 1.9.3 - no mappings at all
        exclude("1.16", "1.10.1", "1.8.9", "1.9.1", "1.9.3", "1.20", "1.20.3", "1.20.5", "1.21.2")

        // include only releases, no snapshots
        includeTypes("release")
    }*/

    basePackage("org.screamingsandals.bedwars.lib.nms.accessors")
    namespaces("mojang", "spigot")
    accessorType("reflection")
    mappingWebsite("https://mappings.dev")

    var CompoundTag = mapClass("net.minecraft.nbt.CompoundTag") {
        constructor()
        method("void", "putInt", String::class, "int")
        method("void", "putString", String::class, String::class)
        methodChain {
            item("net.minecraft.nbt.Tag", "put", String::class, "net.minecraft.nbt.Tag")
            item("void", "set", String::class, "net.minecraft.server.VVV.NBTBase") // Spigot mapped
        }
        method("net.minecraft.nbt.Tag", "get", String::class)
    }
    mapClass("net.minecraft.nbt.TagParser") {
        methodChain {
            item(CompoundTag, "parseCompoundFully", String::class)
            item(CompoundTag, "parseTag", String::class)
        }
    }
    var Component = mapClass("net.minecraft.network.chat.Component") {
        getter(String::class, "coloredString")
        getter(String::class, "string")
        method("net.minecraft.network.chat.MutableComponent", "literal", String::class)
    }
    var Attribute = mapClass("net.minecraft.world.entity.ai.attributes.Attribute")
    mapClass("net.minecraft.core.RegistryAccess") {
        field("net.minecraft.core.RegistryAccess\$Frozen", "EMPTY")
    }
    mapClass("net.minecraft.network.chat.Component\$Serializer") {
        method(Component, "fromJson", String::class) // 1.16 and below
        method("net.minecraft.network.chat.MutableComponent", "fromJson", String::class) // 1.16.1 and higher
        method("net.minecraft.network.chat.MutableComponent", "fromJsonLenient", String::class, "net.minecraft.core.HolderLookup\$Provider") // 1.20.5+
    }
    var SynchedEntityData = mapClass("net.minecraft.network.syncher.SynchedEntityData") {
        method("void", "watch", "int", "java.lang.Object")
        method("java.util.List", "packDirty")
        method("void", "clearDirty")
        getter("java.util.List", "all")
        getter("java.util.List", "nonDefaultValues")
    }
    var Level = mapClass("net.minecraft.world.level.Level") {
        getter("java.util.function.Supplier", "profilerSupplier") // 1.17-1.21.1, no longer needed for GoalSelector since 1.21.2
        getter("net.minecraft.util.profiling.ProfilerFiller", "profiler") // for something between new and old versions
        field("net.minecraft.server.VVV.MethodProfiler", "methodProfiler") // for older versions, Spigot mapped
    }
    var Entity = mapClass("net.minecraft.world.entity.Entity") {
        field(Level, "level")
        methodChain {
            item(Level, "getCommandSenderWorld")
            item(Level, "level")
        }
        method("void", "load", CompoundTag)
        method("void", "absSnapTo", "double", "double", "double", "float", "float")
        getter("int", "id")
        getter("java.util.UUID", "UUID")
        getter("net.minecraft.world.entity.EntityType", "type")
        getter(SynchedEntityData, "entityData")
        getter("net.minecraft.world.phys.Vec3", "deltaMovement")
        getter(Component, "customName")
        setter(Component, "customName")
        getter("double", "x")
        getter("double", "y")
        getter("double", "z")
        getter("float", "xRot")
        getter("float", "yRot")
        getter("float", "yHeadRot")
        getter("boolean", "customNameVisible")
        setter("boolean", "customNameVisible")
        getter("boolean", "invisible")
        setter("boolean", "invisible")
        getter("boolean", "noGravity")
        setter("boolean", "noGravity")
        method("boolean", "onGround")
        // old
        field("float", "yRot")
        field("float", "xRot")
        field("double", "x")
        field("double", "y")
        field("double", "z")
        setter(String::class, "customName") // Spigot mapped
    }
    mapClass("net.minecraft.world.entity.decoration.ArmorStand") {
        constructor(Level, "double", "double", "double")
        getter("boolean", "small")
        setter("boolean", "small")
        setter("boolean", "showArms")
        setter("boolean", "noBasePlate")
        setter("boolean", "marker")
    }
    var PathfinderMob = mapClass("net.minecraft.world.entity.PathfinderMob")
    var AttributeSupplier = mapClass("net.minecraft.world.entity.ai.attributes.AttributeSupplier") {
        field("java.util.Map", "instances")
    }
    var AttributeInstance = mapClass("net.minecraft.world.entity.ai.attributes.AttributeInstance") {
        constructor(Attribute, "java.util.function.Consumer")
        constructor("net.minecraft.core.Holder", "java.util.function.Consumer") // 1.20.5+
        getter("double", "value")
        setter("double", "baseValue")
    }
    var AttributeMap = mapClass("net.minecraft.world.entity.ai.attributes.AttributeMap") {
        method(AttributeInstance, "registerAttribute", Attribute)
        field(AttributeSupplier, "supplier")
    }
    var LivingEntity = mapClass("net.minecraft.world.entity.LivingEntity") {
        method(AttributeInstance, "getAttribute", Attribute)
        method(AttributeInstance, "getAttribute", "net.minecraft.core.Holder")
        getter(AttributeMap, "attributes")
        getter("net.minecraft.world.damagesource.CombatTracker", "combatTracker")
    }
    var Mob = mapClass("net.minecraft.world.entity.Mob") {
        field("net.minecraft.world.entity.ai.goal.GoalSelector", "goalSelector")
        field("net.minecraft.world.entity.ai.goal.GoalSelector", "targetSelector")
        field(LivingEntity, "target")
    }
    var Packet = mapClass("net.minecraft.network.protocol.Packet")
    var ServerboundClientCommandPacket_Action = mapClass("net.minecraft.network.protocol.game.ServerboundClientCommandPacket\$Action") {
        enumConstant("PERFORM_RESPAWN")
    }
    var ServerboundClientCommandPacket = mapClass("net.minecraft.network.protocol.game.ServerboundClientCommandPacket") {
        constructor(ServerboundClientCommandPacket_Action)
    }
    var Connection = mapClass("net.minecraft.network.Connection") {
        field("io.netty.channel.Channel", "channel")
    }
    var ServerGamePacketListenerImpl = mapClass("net.minecraft.server.network.ServerGamePacketListenerImpl") {
        field(Connection, "connection") // <= 1.20.1
        method("void", "send", Packet) // <= 1.20.1
        method("void", "handleClientCommand", ServerboundClientCommandPacket)
    }
    mapClass("net.minecraft.server.network.ServerCommonPacketListenerImpl") { // 1.20.2+
        field(Connection, "connection")
        method("void", "send", Packet)
    }
    mapClass("net.minecraft.server.level.ServerPlayer") {
        field(ServerGamePacketListenerImpl, "connection")
        method("void", "tellNeutralMobsThatIDied")
        setter(Entity, "camera")
    }
    mapClass("net.minecraft.world.entity.ai.attributes.Attributes") {
        fieldChain {
            item(Attribute, "MAX_HEALTH")
            item("net.minecraft.core.Holder", "MAX_HEALTH")
        }
        fieldChain {
            item(Attribute, "FOLLOW_RANGE")
            item("net.minecraft.core.Holder", "FOLLOW_RANGE")
        }
        fieldChain {
            item(Attribute, "KNOCKBACK_RESISTANCE")
            item("net.minecraft.core.Holder", "KNOCKBACK_RESISTANCE")
        }
        fieldChain {
            item(Attribute, "MOVEMENT_SPEED")
            item("net.minecraft.core.Holder", "MOVEMENT_SPEED")
        }
        fieldChain {
            item(Attribute, "FLYING_SPEED")
            item("net.minecraft.core.Holder", "FLYING_SPEED")
        }
        fieldChain {
            item(Attribute, "ATTACK_DAMAGE")
            item("net.minecraft.core.Holder", "ATTACK_DAMAGE")
        }
        fieldChain {
            item(Attribute, "ATTACK_KNOCKBACK")
            item("net.minecraft.core.Holder", "ATTACK_KNOCKBACK")
        }
        fieldChain {
            item(Attribute, "ATTACK_SPEED")
            item("net.minecraft.core.Holder", "ATTACK_SPEED")
        }
        fieldChain {
            item(Attribute, "ARMOR")
            item("net.minecraft.core.Holder", "ARMOR")
        }
        fieldChain {
            item(Attribute, "ARMOR_TOUGHNESS")
            item("net.minecraft.core.Holder", "ARMOR_TOUGHNESS")
        }
        fieldChain {
            item(Attribute, "LUCK")
            item("net.minecraft.core.Holder", "LUCK")
        }
    }
    mapClass("net.minecraft.network.protocol.game.ServerboundInteractPacket") {
        field("int", "entityId")
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket") {
        constructor("int") // 1.17
        constructor("int[]")
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket") {
        constructor("int", SynchedEntityData, "boolean") // <= 1.19.2
        constructor("int", "java.util.List") // 1.19.3
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket") {
        constructor(Entity) // <= 1.21.1
        constructor("int", "net.minecraft.world.entity.PositionMoveRotation", "java.util.Set", "boolean") // 1.21.2+
    }
    mapClass("net.minecraft.world.entity.PositionMoveRotation") { // 1.21.2+
        method(this.name, "of", "net.minecraft.world.entity.Entity")
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundSetExperiencePacket") {
        constructor("float", "int", "int")
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundAddEntityPacket") {
        constructor(LivingEntity) // 1.19-1.19.2
        constructor(Entity) // 1.19.3-1.20.6
        constructor("int", "java.util.UUID", "double", "double", "double", "float", "float", "net.minecraft.world.entity.EntityType", "int", "net.minecraft.world.phys.Vec3", "double") // used 1.21+
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundAddMobPacket") {
        constructor(LivingEntity)
    }
    var ClientboundSetTitlesPacket_Type = mapClass("net.minecraft.network.protocol.game.ClientboundSetTitlesPacket\$Type") { // 1.16.5 and lower
        enumConstant("TITLE")
        enumConstant("SUBTITLE")
        enumConstant("TIMES")
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundSetTitlesPacket") {
        constructor(ClientboundSetTitlesPacket_Type, Component)
        constructor(ClientboundSetTitlesPacket_Type, Component, "int", "int", "int")
    }
    var EnumParticle = mapClass("net.minecraft.server.VVV.EnumParticle") // 1.8.8, Spigot mapped
    mapClass("net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket") {
        constructor(EnumParticle, "boolean", "float", "float", "float", "float", "float", "float", "float", "int", "int[]")
    }
    var Goal = mapClass("net.minecraft.world.entity.ai.goal.Goal")
    mapClass("net.minecraft.world.entity.ai.goal.GoalSelector") {
        constructor("java.util.function.Supplier") // <= 1.21.1
        constructor() // 1.21.2+
        method("void", "addGoal", "int", Goal)
    }
    mapClass("net.minecraft.world.entity.ai.goal.MeleeAttackGoal") {
        constructor(PathfinderMob, "double", "boolean")
    }
    mapClass("net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal") {
        constructor(Mob, "java.lang.Class", "boolean") // 1.14+
        constructor("net.minecraft.server.VVV.EntityCreature", "java.lang.Class", "boolean") // <= 1.13.2, Spigot mapped
    }
    mapClass("net.minecraft.network.protocol.game.ClientboundTabListPacket") {
        constructor()
        constructor(Component, Component)
        field(Component, "header")
        field(Component, "footer")
    }
    mapClass("net.minecraft.world.damagesource.CombatTracker") {
        getter(Component, "deathMessage")
        method("void", "recheckStatus")
    }
    mapClass("net.minecraft.world.entity.player.Player") {
        method("void", "removeEntitiesOnShoulder")
    }
    mapClass("net.minecraft.world.entity.boss.wither.WitherBoss") {
        constructor("net.minecraft.server.VVV.World") // Spigot mapped
    }
    mapClass("net.minecraft.world.entity.boss.enderdragon.EnderDragon") {
        constructor("net.minecraft.server.VVV.World") // Spigot mapped
        field("net.minecraft.world.entity.boss.enderdragon.EnderDragonPart[]", "subEntities")
    }
    mapClass("net.minecraft.world.entity.ai.goal.FloatGoal") {
        constructor(Mob)
    }
    mapClass("net.minecraft.world.entity.ai.goal.RandomStrollGoal") {
        constructor(PathfinderMob, "double")
    }
    mapClass("net.minecraft.world.entity.ai.goal.RandomLookAroundGoal") {
        constructor(Mob)
    }
    mapClass("net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal") {
        constructor("net.minecraft.server.VVV.EntityCreature", "boolean", "java.lang.Class[]") // old, Spigot mapped
        constructor(PathfinderMob, "java.lang.Class[]") // new
    }
    mapClass("net.minecraft.world.entity.animal.golem.IronGolem")
    var EntityType = mapClass("net.minecraft.world.entity.EntityType") {
        enumConstant("TEXT_DISPLAY")
    }
    var Display_BillboardConstraints = mapClass("net.minecraft.world.entity.Display\$BillboardConstraints") {
        enumConstant("CENTER")
    }
    mapClass("net.minecraft.world.entity.Display") {
        setter(Display_BillboardConstraints, "billboardConstraints")
    }
    mapClass("net.minecraft.world.entity.Display\$TextDisplay") {
        constructor(EntityType, Level)

        getter(Component, "text")
        setter(Component, "text")
    }
    mapClass("net.minecraft.world.item.ItemStack") {
        method("void", "setTag", CompoundTag)
        getter("net.minecraft.world.item.Item", "item")

        // 1.20.5-1.21.5
        method("java.util.Optional", "parse", "net.minecraft.core.HolderLookup\$Provider", "net.minecraft.nbt.Tag")
        field("com.mojang.serialization.Codec", "CODEC")
     }

    mapClass("net.minecraft.server.MinecraftServer") {
        field("com.mojang.datafixers.DataFixer", "fixerUpper")
        field("net.minecraft.server.VVV.DataConverterManager", "dataConverterManager")
        method("net.minecraft.core.RegistryAccess\$Frozen", "registryAccess")
    }

    // Datafixer
    mapClass("net.minecraft.util.datafix.fixes.References") {
        fieldChain {
            item("com.mojang.datafixers.DSL\$TypeReference", "ITEM_STACK")
            item("net.minecraft.server.VVV.DataConverterTypes", "ITEM_INSTANCE")
        }
    }
    mapClass("net.minecraft.server.VVV.DataConverterManager") {
        field("int", "field_188262_d")
        method("net.minecraft.nbt.NBTTagCompound", "func_188251_a", "net.minecraft.util.datafix.IFixType", "net.minecraft.nbt.NBTTagCompound", "int")
    }
    mapClass("net.minecraft.nbt.NbtOps") {
        field(this.name, "INSTANCE")
    }
    mapClass("net.minecraft.world.item.Item") {
        field("net.minecraft.server.VVV.RegistryMaterials", "REGISTRY") // <= 1.13
    }

    mapClass("net.minecraft.core.MappedRegistry") {
        method("java.lang.Object", "func_177774_c", "java.lang.Object") // < 1.13
    }
    mapClass("net.minecraft.core.HolderLookup\$Provider") {
        method("net.minecraft.resources.RegistryOps", "createSerializationContext", "com.mojang.serialization.DynamicOps")
    }
}
