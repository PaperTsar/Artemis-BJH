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

package com.testtubebaby.artemistesting.core;

import com.testtubebaby.artemismain.core.Aspect;
import com.testtubebaby.artemismain.core.EntitySystem;

public class Sys1 extends EntitySystem {
	private Comp1[] compTable1;

	public Sys1() {
		super(new Aspect().hasToHave(Comp1.class), false);
	}


	@Override
	public void init() {
        super.init();
	}
	
	


	@Override
	public void process(int from, int to) {
		Comp1 comp1;

		for (int e = 0, s = iterableEntity.size(); e < s; e++) {
			comp1 = compTable1[iterableEntity.get(e)];

			System.out.print(comp1.mddddessage);
			System.out.println(" & System1 message");

		}
	}


	@Override
	public void refetchTables() {
		compTable1 = world.getCm().getCompRow(Comp1.class);
	}
}
