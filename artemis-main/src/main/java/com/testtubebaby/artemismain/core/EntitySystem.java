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
import com.testtubebaby.artemismain.utils.IntSort;

import java.util.Arrays;

public abstract class EntitySystem implements TableObserver {
	private IntBag addEntity = new IntBag();
	private IntBag removeEntity = new IntBag();
	protected IntBag iterableEntity = new IntBag();
    protected World world = null;
	private IntSort sorter = IntSort.instance();
	final Aspect aspect;
    int numberOfEntities = 0;
    final boolean concurrent;

	protected EntitySystem(Aspect aspect, boolean concurrent) {
		this.aspect = aspect;
        this.concurrent = concurrent;
	}

	final void updateEntities() {
		// add all the entities from the addEntity Bag
		iterableEntity.addAll(addEntity);

		// clear the addEntity Bag, so that the next time it's contents won't be
		// processed a second time
		addEntity.clear();

		int i, step; // declare loop variables outside

		// cache removeEntities size and underlying data
		int[] remData = removeEntity.getData();
		int remSize = removeEntity.size();

		// cache iterableEntities' size and underlying data
		int[] iterData = iterableEntity.getData();
		int iterSize = iterableEntity.size();

		// sort iterableEntites so we can use binarysearch on them
		sorter.sort(iterData, 0, iterSize);

		// sorting removeEntites now will ensure that the translated indexes
		// will be sorted since iterableEntities are sorted already
		sorter.sort(remData, 0, remSize);

		// translate entityIds in removeEntity Bag to
		// indexes in iterableEntity Bag
		for (i = 0; i < remSize; i++) {
			remData[i] = Arrays.binarySearch(iterData, 0, iterSize, remData[i]);
		}

		// marking remove places
		for (i = 0; i < remSize; i++) {
			if (remData[i] >= 0)
				iterData[remData[i]] = -1;
		}

		// step i to the first item after the first -1
		i = 0;
		while (iterData[i++] != -1 && i < iterSize)
			;


		// pull the array together
		for (step = 1; i < iterSize; i++) {
			if (iterData[i] != -1)
				iterData[i - step] = iterData[i];
			else
				step++;
		}

		// set the new size of iterableEntity
		iterableEntity.setSize(iterSize - remSize);

        numberOfEntities = iterableEntity.size();


		// clear removeEntity Bag, so that the next time it's contents won't be
		// processed a second time
		removeEntity.clear();
	}

    final void addEntity(int entityId) {
        addEntity.add(entityId);
    }

    final void removeEntity(int entityId) {
        removeEntity.add(entityId);
    }

    final void deleteAllEntities() {
        addEntity.clear();
        removeEntity.clear();
        iterableEntity.clear();
        numberOfEntities = 0;
    }

	public void init() {
        world.getCm().subToTable(this);
        refetchTables();
    }

	protected abstract void process(int from, int to);
}
