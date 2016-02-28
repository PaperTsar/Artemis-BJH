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

import org.apache.commons.pool2.ObjectPool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentManager {
	private World world; // the world this belongs to
    private CompData compData;
	private Component[][] compTable; // table which holds the components
    private ObjectPool<Component>[] compPools;

	ComponentManager(World worldParam) {
		this.world = worldParam;
	}

    void plugSystemsData(SystemsData sysData) {
        compData = sysData.getCompData();

        int numOfComp = compData.getNumOfComp();
        compTable = new Component[numOfComp][];

        for (int i = 0; i < numOfComp; i++) {
            compTable[i] = (Component[]) Array.newInstance(
                    compData.indToComp(i), world.getMaximumEntityCount());
        }

        compPools = compData.getCompPools();
    }

    void unplugSystemsData() {
        compData = null;
        compTable = null;
        reserveWholeTable();
        reserveTables();
        tableObs.clear();
        wholeTableObs.clear();
    }


	// ----------------- Subscribing to Tables----------------

        //one or more rows

	private List<TableObserver> tableObs = new ArrayList<>();

    private void reserveTables() {
        tableObs.forEach(TableObserver::refetchTables);
    }

    public void subToTable(TableObserver obs) {
        tableObs.add(obs);
    }

    public void unsubFromTable(TableObserver obs) {
        tableObs.remove(obs);
    }


    @SuppressWarnings("unchecked")
    public <T extends Component> T[] getCompRow(Class<? extends Component> comp) {
        if(compTable == null)
            return null;
        return (T[]) compTable[compData.compToInd(comp)];
    }

        //whole table

    private List<EntityFactory> wholeTableObs = new ArrayList<>();

    private void reserveWholeTable() {
        wholeTableObs.forEach(EntityFactory::refetchWholeTable);
    }

    void subToWholeTable(EntityFactory obs) {
        wholeTableObs.add(obs);
    }

    void unsubFromWholeTable(EntityFactory obs) {
        wholeTableObs.remove(obs);
    }

    Component[][] getWholeTable() {
        return compTable;
    }


	// FIN  ----------------- Subscribing to Tables---------------- FIN


	void removeComponents(int entityId, Archetype arch) {
        PooledComponent pooledCompCache;
        for (int e : arch.pooledCompIndexes) {
            try {
                pooledCompCache = (PooledComponent) compTable[e][entityId];
                pooledCompCache.reset();
                compPools[e].returnObject(pooledCompCache);
                compTable[e][entityId] = null;
            } catch (Exception e1) {
                throw new RuntimeException("Shit went south.");
            }
        }
        for (int e : arch.basicCompIndexes) {
            compTable[e][entityId] = null;
        }
    }

	void createComponents(int entityId, Archetype arch) {
		Class<? extends Component>[] basicComps = arch.basicComps;
		int[] basicCompIndexes = arch.basicCompIndexes;
        int[] pooledCompIndexes = arch.pooledCompIndexes;

        int i, size;

		for (i = 0, size = basicCompIndexes.length; i < size; i++) {
			try {
				compTable[basicCompIndexes[i]][entityId] = basicComps[i].newInstance();
			} catch (ArrayIndexOutOfBoundsException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Shit went south.");
			}
        }
        for(i = 0, size = pooledCompIndexes.length; i < size; i++) {
            try {
                compTable[pooledCompIndexes[i]][entityId] = compPools[pooledCompIndexes[i]].borrowObject();
            } catch (Exception e) {
                throw new RuntimeException("Shit went south.");
            }
        }
	}

	void grow() {
        // cache expectedEntityNum
        int expEntCount = world.getMaximumEntityCount();
        // cache number of components
        int numOfComp = compData.getNumOfComp();


        // Temporarily store the old table
        Component[][] oldTable = compTable;

        // create new Table, yay!
        compTable = new Component[numOfComp][];

        // copy over element with new table size
        for (int i = 0; i < numOfComp; i++) {
            compTable[i] = Arrays.copyOf(oldTable[i], expEntCount);
        }

        reserveTables();
        reserveWholeTable();
    }

    void deleteAllEntities() {
        for (Component[] e : compTable) {
            Arrays.fill(e, null);
        }
    }
}
