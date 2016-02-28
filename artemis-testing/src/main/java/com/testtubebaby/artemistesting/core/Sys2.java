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

public class Sys2 extends EntitySystem {
	private Comp2[] compTable2;

	public Sys2() {
		super(new Aspect().hasToHave(Comp2.class), false);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void refetchTables() {
		compTable2 = world.getCm().getCompRow(Comp2.class);
		
	}
	
	@Override
	public void process(int from, int to) {
		Comp2 comp2;
		
		for (int e =  0, s = iterableEntity.size(); e < s; e++) {
			comp2 = compTable2[iterableEntity.get(e)];
			
			System.out.print(comp2.messasdfdge);
			System.out.println(" & System2 message");
			
		}
	}
}
