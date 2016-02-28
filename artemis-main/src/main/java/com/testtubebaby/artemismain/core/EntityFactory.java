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

public abstract class EntityFactory {
    protected World world;
    protected Component[][] compTable;
    protected SystemsData sysData;
    protected Archetype arch;
	protected int currId;
	
	protected EntityFactory(SystemsData sysData, Archetype arch) {
		this.arch = arch;
        this.sysData = sysData;
        sysData.addFactory(this);
	}

    protected final void init() {
        if((world = sysData.world) != null) {
            world.getCm().subToWholeTable(this);
            compTable = world.getCm().getWholeTable();
        }
    }

    protected final int getCompIndex(Class<? extends Component> compClass) {
        return sysData.getCompData().compToInd(compClass);
    }

    protected final void refetchWholeTable() {
        compTable = world.getCm().getWholeTable();
    }


    public abstract EntityFactory newEntity();

    public abstract EntityFactory editEntity(int entityId);
	
	public final int build() {
		int tmp = currId;
		currId = -1;
		return tmp;
	}
}
