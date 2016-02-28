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

package com.testtubebaby.artemismain.utils;

import java.util.Iterator;

/**
 * Created by Malyom on 2015.07.13..
 */
public interface ImmutableBag<E> {
    boolean contains(E e);
    E get(int index);
    E safeGet(int index);
    int size();
    int getCapacity();
    boolean isIndexWithinBounds(int index);
    boolean isEmpty();
    E[] copyOfDataInto(E[] intoArray);
    Iterator<E> iterator();
}
