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
import com.testtubebaby.artemismain.utils.ImmutableBag;
import com.testtubebaby.artemismain.utils.IntBag;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class CompData {
    private int numOfComp = 0;
    private final Map<Class<? extends Component>, Integer> compToInd = new HashMap<>();
    private final Bag<Class<? extends Component>> indToComp = new Bag<>();
    private IntBag pooledCompIndexes = new IntBag();
    private ObjectPool<Component>[] compPools;
    private boolean sealed = false;

    /**
     * Register component classes to this CompData. Attempting to register one twice will result in an exception!
     * @param compClass
     *      the component class to be registered
     * @return
     *      returns itself for chainability
     */
    private Class<PooledComponent> pooledCompClass = PooledComponent.class;
    public CompData registerComp(Class<? extends Component> compClass) {
        if(sealed)
            throw new RuntimeException("This CompData has already been sealed!");



        Integer keyReturned = compToInd.putIfAbsent(compClass, numOfComp++);
        if (keyReturned != null)
            throw new RuntimeException(
                    "This component class has already been registered to this CompData: "
                            + compClass.getName());
        if(pooledCompClass.isAssignableFrom(compClass))
            pooledCompIndexes.add(compToInd.get(compClass));
        indToComp.add(compClass);
        return this;
    }

    /**
     * Seals this CompData, preventing further attempts to modify it.
     * @return
     *      whether this CompData has already been sealed.
     */
    public CompData seal() {
        if(sealed == false) {
            sealed = true;
            compPools = new ObjectPool[compToInd.size()];
            int[] data = pooledCompIndexes.getData();
            int size = pooledCompIndexes.size();
            for (int i = 0; i < size; i++) {
                compPools[data[i]] = new GenericObjectPool<>(new ComponentFactory(indToComp.get(data[i])));
            }
        }
        return this;
    }

    /**
     * Returns the number of component classes registered into this CompData.
     * @return
     *      number of component classes registered
     */
    int getNumOfComp() {
        ensureIsSealed();
        return numOfComp;
    }

    /**
     * Returns an unmodifiable view of the underlying map.
     * <br>Drop in a comp class into this map, get back it's unique index.
     * @return
     *      Returns an unmodifiable view of the underlying map.
     */
    Map<Class<? extends Component>, Integer> getCompToIndMap() {
        ensureIsSealed();
        return Collections.unmodifiableMap(compToInd);
    }

    /**
     * Returns an immutableBag view of the underlying bag.
     * <br>Drop in an index, get back a CompClass. This can also be iterated over, unlike the map.
     * <br><br>Sidenote: if you cast this method's return into a Bag and attempt to modify it, then
     * it won't cry, but this CompData will be broken. ...You don't want that, do you?
     * @return
     */
    ImmutableBag<Class<? extends Component>> getIndToCompBag() {
        ensureIsSealed();
        return indToComp;
    }

    /**
     * Convenience method, drop in a component Class, get back it's unique index.
     * @param compClass
     *      component class whose idnex you need
     * @return
     *      component class' index
     */
    int compToInd(Class<? extends Component> compClass) {
        return compToInd.get(compClass);
    }

    /**
     * Convenience method, drop in an index, get back it's component Class
     * @param index
     *      the index of the component Class you want
     * @return
     *    component class of the index you dropped in
     */
    Class<? extends Component> indToComp(int index) {
        return indToComp.get(index);
    }

    ObjectPool<Component>[] getCompPools() {
        return compPools;
    }

    private void ensureIsSealed() {
        if(!sealed)
            throw new RuntimeException("This CompData has not yet been sealed, seal it before usage!");
    }

    public boolean isSealed() {
        return sealed;
    }
}
