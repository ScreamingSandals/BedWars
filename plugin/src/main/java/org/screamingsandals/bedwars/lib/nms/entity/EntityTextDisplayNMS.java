/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.Location;
import org.screamingsandals.bedwars.lib.nms.accessors.*;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;
import org.screamingsandals.bedwars.lib.nms.utils.InstanceMethod;
import org.screamingsandals.bedwars.tab.TabManager;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.getHandle;

public class EntityTextDisplayNMS extends EntityNMS {
    public EntityTextDisplayNMS(Object handler) {
        super(handler);
        if (!Display$TextDisplayAccessor.TYPE.get().isInstance(handler)) {
            throw new IllegalArgumentException("Entity must be instance of Display$TextDisplay!!");
        }
        ClassStorage.getMethod(handler, DisplayAccessor.METHOD_SETBILLBOARDCONSTRAINTS.get()).invoke(Display$BillboardConstraintsMapping.FIELD_CENTER.getConstantValue());
    }

    public EntityTextDisplayNMS(Location loc) throws Throwable {
        this(Display$TextDisplayAccessor.CONSTRUCTOR_0.get().newInstance(EntityTypeMapping.FIELD_TEXT_DISPLAY.getConstantValue(), getHandle(loc.getWorld())));
        this.setLocation(loc);
    }

    public void setText(String name) {
        InstanceMethod method = ClassStorage.getMethod(handler, Display$TextDisplayAccessor.METHOD_SETTEXT.get());
        method.invoke(ClassStorage.getMethod(TabManager.getCorrectSerializingMethod()).invokeStatic("{\"text\": \"" + name + "\"}"));
    }

    public String getText() {
        Object textComponent = ClassStorage.getMethod(handler, Display$TextDisplayAccessor.METHOD_GETTEXT.get()).invoke();
        return (String) ClassStorage.getMethod(textComponent, ComponentAccessor.METHOD_GETCOLOREDSTRING.get()).invoke();
    }

}
