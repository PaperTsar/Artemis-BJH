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
import com.testtubebaby.artemismain.utils.IntBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */

@SuppressWarnings("unused")
public class SystemsData {

    private int numOfSys;
    private EntitySystem[] systems;
    private TranslatedAspect[] aspects;
    private boolean[] concurrent;
    private boolean[] dirty;

    private Map<Class<? extends EntitySystem>, EntitySystem> sysMap = new HashMap<>();
    private CompData compData = null;
    private ArchetypeData archData;
    private Bag<EntityFactory> factories = new Bag<>();

    private boolean sealed;
    World world = null;



    public SystemsData(CompData compData) {
        if(!compData.isSealed())
            throw new RuntimeException("CompData has not yet been sealed, seal it before tying it to a SystemsData!");

        this.compData = compData;
    }


    private List<EntitySystem> tempSystems = new ArrayList<>();

    public SystemsData addSystem(EntitySystem system) {
        if(system == null)
            throw new RuntimeException("Passed parameter is null!");
        if(sealed)
            throw new RuntimeException("This SystemsData has already been sealed!");

        if((sysMap.put(system.getClass(), system)) != null)
            throw new RuntimeException("This systems has already been added!");
        tempSystems.add(system);
        return this;
    }


    public SystemsData seal() {
        if(!sealed) {
            numOfSys = tempSystems.size();

            systems = tempSystems.toArray(new EntitySystem[numOfSys]);
            tempSystems = null;


            aspects = new TranslatedAspect[numOfSys];
            for (int i = 0; i < numOfSys; i++) {
                aspects[i] = new TranslatedAspect(systems[i].aspect, compData);
            }

            concurrent = new boolean[numOfSys];
            for (int i = 0, size = concurrent.length; i < size; i++) {
                concurrent[i] = systems[i].concurrent;
            }
            dirty = new boolean[numOfSys];
            Arrays.fill(dirty, false);

            archData = new ArchetypeData(this);
            sealed = true;
        }
        return  this;
    }


    boolean isSealed() {
        return sealed;
    }


    private void ensureSealed() {
        if(!sealed)
            throw new RuntimeException("This SystemsData has not yet been sealed, seal it before usage!");
    }

    // ----------------- Getters and Setters ------------------

    EntitySystem systemFromClass(Class<? extends EntitySystem> sysClass) {
        ensureSealed();
        return sysMap.get(sysClass);
    }

    int getNumOfSys() {
        ensureSealed();
        return numOfSys;
    }

    EntitySystem[] getSystems() {
        ensureSealed();
        return systems;
    }

    TranslatedAspect[] getAspects() {
        ensureSealed();
        return aspects;
    }

    boolean[] getDirty() {
        ensureSealed();
        return dirty;
    }

    CompData getCompData() {
        ensureSealed();
        return compData;
    }

    ArchetypeData getArchetypeData() {
        return archData;
    }

    boolean[] getConcurrent() {
        return concurrent;
    }

    //------------- Static inner class ArchetypeData --------------
    static class ArchetypeData {
        private int archCounter = 0;

        private Bag<EntitySystem[]> archIndToSystems = new Bag<>();
        private Bag<int[]> archIndToDirty = new Bag<>();
        private Map<String, Archetype> nameToArchetype = new HashMap<>();

        private SystemsData sysData;

        private ArchetypeData(SystemsData sysData) {
            this.sysData = sysData;
        }

        private Archetype createArchetype(ArchetypeBuilder builder) {
            Archetype arch = new Archetype(archCounter++, builder);
            Bag<EntitySystem> sysBag = new Bag<>();
            IntBag dirtyBag = new IntBag();

            int numOfSys = sysData.getNumOfSys();
            TranslatedAspect[] aspects = sysData.getAspects();
            EntitySystem[] systems = sysData.getSystems();

            // check composition against system aspects
            for (int i = 0; i < numOfSys; i++) {
                if (aspects[i].match(arch)) {
                    sysBag.add(systems[i]);
                    dirtyBag.add(i);
                }
            }

            archIndToSystems.add(sysBag.copyOfDataInto(new EntitySystem[sysBag.size()]));
            archIndToDirty.add(dirtyBag.copyOfData());
            if(arch.name != null)
                if((nameToArchetype.put(arch.name, arch)) != null)
                    throw new RuntimeException("Archetype name " + arch.name + " is already used up!");

            return arch;
        }

        EntitySystem[] archIndToSystems(int index) {
            return archIndToSystems.get(index);
        }

        int[] archIndToDirty(int index) {
            return archIndToDirty.get(index);
        }

        Archetype getArchetype(String name) {
            return nameToArchetype.get(name);
        }
    } // --------Here ends ArchetypeData---------------------

    Archetype addArchetype(ArchetypeBuilder builder) {
        return archData.createArchetype(builder);
    }

    void addFactory(EntityFactory factory) {
        factories.add(factory);
    }
    
    void init() {
        for (EntitySystem e : systems) {
            e.init();
        }
        for(int i = 0, size = factories.size(); i<size; i++) {
            factories.get(i).init();
        }
    }
}
