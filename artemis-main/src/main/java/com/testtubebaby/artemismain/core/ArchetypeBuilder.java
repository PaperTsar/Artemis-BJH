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


import java.util.BitSet;

public class ArchetypeBuilder {
    private Bag<Class<? extends Component>> basicComps = new Bag<>();
    private Bag<Class<? extends Component>> pooledComps = new Bag<>();
	private IntBag basicCompIndexes = new IntBag();
    private IntBag pooledCompIndexes = new IntBag();
    private BitSet bitMask = new BitSet();

	private SystemsData sysData;
	private CompData compData;

    private String name = null;

    public ArchetypeBuilder(SystemsData sysData, String name) {
        this(sysData);
        this.name = name;
    }

	public ArchetypeBuilder(SystemsData sysData) {
        this.sysData = sysData;
		compData = sysData.getCompData();
	}


    private Class<PooledComponent> pooledCompClass = PooledComponent.class;
	public ArchetypeBuilder with(Class<? extends Component> compClass) {
        int index = compData.compToInd(compClass);
        if(pooledCompClass.isAssignableFrom(compClass)) {
            pooledComps.add(compClass);
            pooledCompIndexes.add(index);
        } else {
            basicComps.add(compClass);
            basicCompIndexes.add(index);
        }
        bitMask.set(index);
		return this;
	}

	public Archetype build() {
        Archetype arch = sysData.addArchetype(this);
        basicComps = null;
        basicCompIndexes = null;
        bitMask = null;
        sysData = null;
        compData = null;
        name = null;
		return arch;
	}

    // ----------- Getters -------------------
    IntBag getBasicCompIndexes() {
        return basicCompIndexes;
    }

    IntBag getPooledCompIndexes() {
        return pooledCompIndexes;
    }

    BitSet getBitMask() {
        return bitMask;
    }

    SystemsData getSysData() {
        return sysData;
    }

    Bag<Class<? extends Component>> getBasicComps() {
        return basicComps;
    }

    Bag<Class<? extends Component>> getPooledComps() {
        return pooledComps;
    }

    String getName() {
        return name;
    }
}
