/*
* Copyright (C) 2015 Bendegúz Nagy
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.testtubebaby.artemismain.core;


import com.testtubebaby.artemismain.utils.Bag;

class EntityManager {
	private World world;
    private SystemsData sysData;
    private SystemsData.ArchetypeData archData;
    private Bag<Archetype> entIdToArch = new Bag<>();
	
	EntityManager(World worldParam) {
		this.world = worldParam;
	}

    void plugSystemsData(SystemsData sysDataParam) {
        sysData = sysDataParam;
        archData = sysData.getArchetypeData();
    }

    void unplugSystemsData() {
        sysData = null;
        archData = null;
        entIdToArch.clear();
    }


	void addEntity(int entityId, Archetype arch) {
        int archIndex = arch.index;

		entIdToArch.set(entityId, arch);

		boolean[] dirty = sysData.getDirty();

		EntitySystem[] archSysArr = archData.archIndToSystems(archIndex);
		int[] dirtySysArr = archData.archIndToDirty(archIndex);

		int size = archSysArr.length;
		for (int i = 0; i < size; i++) {
			archSysArr[i].addEntity(entityId);
			dirty[dirtySysArr[i]] = true;
		}
	}

	Archetype removeEntity(int entityId) {
        Archetype arch = entIdToArch.get(entityId);
        int archIndex = arch.index;

        entIdToArch.set(entityId, null);

        boolean[] dirty = sysData.getDirty();

        EntitySystem[] archSysArr = archData.archIndToSystems(archIndex);
        int[] dirtySysArr = archData.archIndToDirty(archIndex);

        int size = archSysArr.length;
        for (int i = 0; i < size; i++) {
            archSysArr[i].removeEntity(entityId);
            dirty[dirtySysArr[i]] = true;
        }
        return arch;
    }

    void deleteAllEntities() {
        entIdToArch.clear();
        for (EntitySystem e : sysData.getSystems()) {
            e.deleteAllEntities();
        }
    }
}
