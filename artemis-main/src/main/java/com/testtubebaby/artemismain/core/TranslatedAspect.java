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

import java.util.BitSet;
import java.util.Map;

class TranslatedAspect {
	private BitSet hasToHave = new BitSet();
	private BitSet mustNotHave = new BitSet();
	private BitSet[] atleastOne;

	@SuppressWarnings("unchecked")
	TranslatedAspect(Aspect asp, CompData compData) {
		Map<Class<? extends Component>, Integer> compToInd = compData.getCompToIndMap();
		Object[] loopData;




		loopData = asp.hasToHave.copyOfData();
        for (Object e : loopData) {
            hasToHave.set(compToInd.get(e));
        }


		loopData = asp.mustNotHave.copyOfData();
        for (Object e : loopData) {
            mustNotHave.set(compToInd.get(e));
        }


        Bag<BitSet> tempBag = new Bag<>();
        for(int i = 0, size = asp.atleastOne.size(); i < size; i++) {
            Class<? extends Component>[] arr = asp.atleastOne.get(i);
            BitSet temp = new BitSet();
            for (Class<? extends Component> e : arr) {
                temp.set(compToInd.get(e));
            }
            tempBag.add(temp);
        }
        atleastOne = tempBag.copyOfDataInto(new BitSet[tempBag.size()]);
	}

	
	boolean match(Archetype arch) {
		BitSet temp = (BitSet) hasToHave.clone();

		temp.andNot(arch.compMask);
		if (!temp.isEmpty())
			return false;

		if (arch.compMask.intersects(mustNotHave))
			return false;

		temp = arch.compMask;
	    for(BitSet e : atleastOne) {
			if (!temp.intersects(e))
				return false;
		}

		return true;
	}
}
