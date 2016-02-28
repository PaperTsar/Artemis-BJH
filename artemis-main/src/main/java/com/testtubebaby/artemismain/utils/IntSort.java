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

/*
 * Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.testtubebaby.artemismain.utils;

/**
 * Provides methods to sort arrays of objects. Sorting requires working memory
 * and this class allows that memory to be reused to avoid allocation. The
 * sorting is otherwise identical to the Arrays.sort methods (uses timsort).<br>
 * <br>
 * Note that sorting primitive arrays with the Arrays.sort methods does not
 * allocate memory (unless sorting large arrays of char, short, or byte).
 * 
 * @author Nathan Sweet
 * 
 *         </p> Changes over libGDX original: work on bags instead of libGXX's
 *         arrays.
 */
public class IntSort {
	static private IntSort instance;

	private IntTimSort timSort;

	public IntSort() {
		timSort = new IntTimSort();
	}

	public void sort(IntBag a) {
		timSort.doSort(a.getData(), 0, a.size());
	}

	/**
	 * Don't use this for IntBags
	 * 
	 * @param a
	 */
	public void sort(int[] a) {
		timSort.doSort(a, 0, a.length);
	}

	public void sort(int[] a, int fromIndex, int toIndex) {
		timSort.doSort(a, fromIndex, toIndex);
	}

	/**
	 * Returns a Sort instance for convenience. Multiple threads must not use
	 * this instance at the same time.
	 */
	static public IntSort instance() {
		if (instance == null)
			instance = new IntSort();
		return instance;
	}
}
