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


import com.testtubebaby.artemismain.utils.IntBag;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class World {
	private ComponentManager cm;
	private EntityManager em;
	private EntityIdSupply idSupply;

	private SystemsData sysData;

    private int delta;

    private RunnableWrapper[] runnables = new RunnableWrapper[Runtime.getRuntime().availableProcessors()];

    private final static int baseline = 3;

	public World(int maximumEntityCount) {
		idSupply = new EntityIdSupply(maximumEntityCount);
		cm = new ComponentManager(this);
		em = new EntityManager(this);
	}

    public void plugSystemsData(SystemsData sysDataParam) {
        if(sysDataParam.world != null)
            throw new RuntimeException("This SystemsData is already used by another world, tearDown the other world to make it free!");
        sysDataParam.world = this;

        sysData = sysDataParam;

        for(EntitySystem sys : sysData.getSystems()) {
            sys.world = this;
        }

        cm.plugSystemsData(sysDataParam);
        em.plugSystemsData(sysDataParam);

        sysData.init();
    }

    public void unplugSystemsData() {
        em.unplugSystemsData();
        cm.unplugSystemsData();
        idSupply.reset();

        for(EntitySystem sys : sysData.getSystems()) {
            sys.world = null;
        }

        sysData.world = null;
        sysData = null;
    }


	public void process(int delta) {
		this.delta = delta;

		int numOfSys = sysData.getNumOfSys();
		EntitySystem[] systems = sysData.getSystems();
		boolean[] dirty = sysData.getDirty();
        boolean[] concurrent = sysData.getConcurrent();


        int i, j, k, numOfEnti, div;
        double tmp;
		for (i = 0; i < numOfSys; i++) {
			if (dirty[i]) {
                systems[i].updateEntities();
                dirty[i] = false;
            }
            systems[i].process(0, systems[i].numberOfEntities);
        }

		idSupply.swapBuffer();
	}



	public int createEntity(Archetype arch) {
		int entityId = idSupply.get();
		cm.createComponents(entityId, arch);
        em.addEntity(entityId, arch);
		return entityId;
	}


	public void removeEntity(int entityId) {
        Archetype arch = em.removeEntity(entityId);
		cm.removeComponents(entityId, arch);
		idSupply.add(entityId);
	}

    public void deleteAllEntities(){
        cm.deleteAllEntities();
        em.deleteAllEntities();
        idSupply.reset();
    }


    //------------------------- Getters ---------------------

    int getMaximumEntityCount() {
        return idSupply.getMaxEntityCount();
    }

    public int getDelta() {
        return delta;
    }


	public ComponentManager getCm() {
		return cm;
	}

	EntityManager getEm() {
		return em;
	}


    CompData getCompData() {
        return sysData.getCompData();
    }

    public SystemsData getSystemsData() {
        return sysData;
    }

    SystemsData.ArchetypeData getArchetypeData() {
        return sysData.getArchetypeData();
    }


    public EntitySystem getSystem(Class<? extends EntitySystem> sysToGet) {
        return sysData.systemFromClass(sysToGet);
    }

    public Archetype getArchetype(String name) {
        return sysData.getArchetypeData().getArchetype(name);
    }


    // ---------------- idSupply------------------

    private class EntityIdSupply {
        private int next = 0;
        private IntBag[] buffers = new IntBag[3];
        private int old = 2, current = 0;
        private int maxEntityCount;

        private EntityIdSupply(int maxEntityCount) {
            this.maxEntityCount = maxEntityCount;
            buffers[0] = new IntBag();
            buffers[1] = new IntBag();
            buffers[2] = new IntBag();
        }

        private void add(int e) {
            buffers[old].add(e);
        }

        private int get() {
            if (buffers[current].size() == 0)
                if (next >= maxEntityCount) {
                    maxEntityCount = (maxEntityCount * 7) / 4 + 1;
                    cm.grow();
                    return next++;
                } else {
                    return next++;
                }

            return buffers[current].pop();
        }

        private int getMaxEntityCount() {
            return maxEntityCount;
        }

        // this is required to avoid duplicates in EntitySystems' bags
        private void swapBuffer() {
            if(++current == 3)
                current = 0;
            if(++old == 3)
                old = 0;
        }

        private void reset() {
            next = 0;
            for (IntBag e : buffers) {
                e.clear();
            }
        }
    } //----------- Here ends IdSupply ------------------------
}
