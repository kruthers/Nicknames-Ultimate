/*
 * Nickname Ultimate - A comprehensive  nickname plugin for spigot
 * Copyright (C) 2020 kruthers
 *
 * This Program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kruthers.nicknames.events;

import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.storage.FileStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldSave implements Listener {
    private NicknamesUltimate plugin;
    public WorldSave(NicknamesUltimate coreClass){ plugin=coreClass; }

    @EventHandler
    public void onSave(WorldSaveEvent event){
        if (plugin.getConfig().getBoolean("storage.autosave_data") && NicknamesUltimate.storageMethod.equals("file")){
            FileStorage.save();
        }
    }
}
