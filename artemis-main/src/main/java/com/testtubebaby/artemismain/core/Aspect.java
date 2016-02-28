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

public final class Aspect {
	final Bag<Class<? extends Component>> hasToHave = new Bag<>();
	final Bag<Class<? extends Component>> mustNotHave = new Bag<>();
	final Bag<Class<? extends Component>[]> atleastOne = new Bag<>();


	@SafeVarargs
	public final Aspect hasToHave(Class<? extends Component>... comps) {
		hasToHave.addAll(comps);
		return this;
	}

	@SafeVarargs
	public final Aspect mustNotHave(Class<? extends Component>... comps) {
		mustNotHave.addAll(comps);
		return this;
	}

	@SafeVarargs
	public final Aspect atleastOne(Class<? extends Component>... comps) {
		atleastOne.add(comps);
		return this;
	}
}
