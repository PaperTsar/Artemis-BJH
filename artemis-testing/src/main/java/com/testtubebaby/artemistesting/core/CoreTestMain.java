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

import com.testtubebaby.artemismain.core.Archetype;
import com.testtubebaby.artemismain.core.ArchetypeBuilder;
import com.testtubebaby.artemismain.core.CompData;
import com.testtubebaby.artemismain.core.SystemsData;
import com.testtubebaby.artemismain.core.World;
import com.testtubebaby.artemismain.genfacts.Comp12FactImpl;
import com.testtubebaby.artemismain.genfacts.Comp1FactImpl;
import com.testtubebaby.artemismain.genfacts.Comp2FactImpl;

public class CoreTestMain {

	public static void main(String[] args) {
		new CoreTestMain().testNotMain(null);
	}

	public void testNotMain(String[] args) {
		CompData compData = new CompData()
		        .registerComp(Comp1.class)
                .registerComp(Comp2.class)
				.seal();

        SystemsData sysData = new SystemsData(compData)
                .addSystem(new Sys1())
                .addSystem(new Sys2())
                .seal();

		World world = new World(2);
        world.plugSystemsData(sysData);


        Comp1FactImpl myFact = new Comp1FactImpl(sysData);
        Comp2FactImpl mFact2 = new Comp2FactImpl(sysData);
        Comp12FactImpl myFact3 = new Comp12FactImpl(sysData);

        myFact.newEntity().comp1("Comp1Pooled!", 3);
        world.removeEntity(0);
        myFact.newEntity();


		Sys1 system = (Sys1) world.getSystem(Sys1.class);
		world.process(2);
	}

}
