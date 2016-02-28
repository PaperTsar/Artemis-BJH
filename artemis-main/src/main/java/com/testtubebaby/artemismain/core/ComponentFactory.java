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

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by Bendegúz on 2015.07.19..
 */
public class ComponentFactory extends BasePooledObjectFactory<Component>{
    Class<? extends Component> compClass;

    ComponentFactory(Class<? extends Component> compClass) {
        this.compClass = compClass;
    }
    @Override
    public Component create() throws Exception {
        return compClass.newInstance();
    }

    @Override
    public PooledObject<Component> wrap(Component component) {
        return new DefaultPooledObject<Component>(component);
    }
}
