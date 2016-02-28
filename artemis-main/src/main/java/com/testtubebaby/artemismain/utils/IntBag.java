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

import java.util.Arrays;


/**
 * @author original Bag by Arni Arent
 * @author Adrian Papari
 *
 */

public class IntBag {

	/** The backing array. */
	private int[] data;
	/** The number of values stored by this bag. */
	protected int size = 0;

	/**
	 * Constructs an empty Bag with an initial capacity of 64.
	 */
	public IntBag() {
		this(64);
	}

	/**
	 * Constructs an empty Bag with the specified initial capacity.
	 * 
	 * @param capacity
	 *            the initial capacity of Bag
	 */
	public IntBag(int capacity) {
		data = new int[capacity];
	}


	/**
	 * Removes the element at the specified position in this Bag.
	 * <p>
	 * It does this by overwriting it was last element then removing last
	 * element
	 * </p>
	 * 
	 * @param index
	 *            the index of element to be removed
	 *
	 * @return element that was removed from the Bag
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public int remove(int index) throws ArrayIndexOutOfBoundsException {
		int t = data[index];
		data[index] = data[--size]; // overwrite with last element
		return t;
	}
	
	/**
	 * Returns and removes the last element added for this collection.
	 * 
	 * @return
	 * 		The last element added to this collection.
	 */
	public int pop() throws ArrayIndexOutOfBoundsException {
		return data[--size];
	}

	/**
	 * Check if bag contains this element.
	 * 
	 * @param e
	 *            element to check
	 *
	 * @return {@code true} if the bag contains this element
	 */
	public boolean contains(int e) {
		for (int i = 0; size > i; i++) {
			if (e == data[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the element at the specified position in Bag.
	 * 
	 * @param index
	 *            index of the element to return
	 *
	 * @return the element at the specified position in bag
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public int get(int index) throws ArrayIndexOutOfBoundsException {
		return data[index];
	}

	/**
	 * Returns the number of elements in this bag.
	 * 
	 * @return the number of elements in this bag
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns the number of elements the bag can hold without growing.
	 * 
	 * @return the number of elements the bag can hold without growing
	 */
	public int getCapacity() {
		return data.length;
	}

	/**
	 * Checks if the internal storage supports this index.
	 * 
	 * @param index
	 *            index to check
	 *
	 * @return {@code true} if the index is within bounds
	 */
	public boolean isIndexWithinBounds(int index) {
		return index < getCapacity();
	}

	/**
	 * Returns true if this bag contains no elements.
	 * 
	 * @return {@code true} if this bag contains no elements
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Adds the specified element to the end of this bag.
	 * <p>
	 * If required, it also increases the capacity of the bag.
	 * </p>
	 * 
	 * @param e
	 *            element to be added to this list
	 */
	public void add(int e) {
		// is size greater than capacity increase capacity
		if (size == data.length)
			grow();

		data[size++] = e;
	}

	public void addAll(IntBag cont) {
		// if is size greater than capacity increase capacity
		int contSize = cont.size;
		while (size + contSize > data.length)
			grow();

		System.arraycopy(cont.getData(), 0, data, size, contSize);
		size += contSize;
	}

	/**
	 * Set element at specified index in the bag.
	 * 
	 * @param index
	 *            position of element
	 * @param e
	 *            the element
	 */
	public void set(int index, int e) {
		if (index >= data.length) {
			grow((index * 7) / 4 + 1);
		}
		size = Math.max(size, index + 1);
		data[index] = e;
	}


	/**
	 * Increase the capacity of the bag.
	 * <p>
	 * Capacity will increase by (3/2)*capacity + 1.
	 * </p>
	 */
	private void grow() {
		int newCapacity = (data.length * 7) / 4 + 1;
		grow(newCapacity);
	}

	/**
	 * Increase the capacity of the bag.
	 *
	 * @param newCapacity
	 *            new capacity to grow to
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 *             if new capacity is smaller than old
	 */
	private void grow(int newCapacity) throws ArrayIndexOutOfBoundsException {
		int[] oldData = data;
		data = new int[newCapacity];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
	}

	/**
	 * Check if an item, if added at the given item will fit into the bag.
	 * <p>
	 * If not, the bag capacity will be increased to hold an item at the index.
	 * </p>
	 *
	 * @param index
	 *            index to check
	 */
	public void ensureCapacity(int index) {
		if (index >= data.length) {
			grow(index);
		}
	}

	/**
	 * Sets size to zero, effectively clearing it.<br>
	 * 
	 * No need to clear an array of primitives to zero
	 */
	public void clear() {
		// Arrays.fill(data, 0, size, 0);
		size = 0;
	}

	/**
	 * Returns this bag's underlying array.
	 * <p>
	 * Use with care.
	 * </p>
	 * 
	 * @return the underlying array
	 *
	 * @see IntBag#size()
	 */
	public int[] getData() {
		return data;
	}
	
	public int[] copyOfData() {
		return Arrays.copyOf(data, size);
	}

	/**
	 * Set the size.
	 * <p>
	 * This will not resize the bag, nor will it clean up contents beyond the
	 * given size. Use with caution.
	 * </p>
	 *
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * returns true only if the type passed in is type of IntBag
	 * and both contain the same elements on the same places
	 */
	
	@Override
	public boolean equals(Object bag) {
		if(!(bag instanceof IntBag))
			return false;
		
		IntBag otherBag = (IntBag)bag;
		if(otherBag.size() != size)
			return false;
		
		for(int i = 0; i<size; i++) {
			if(data[i] != otherBag.get(i))
				return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bag(");
		for (int i = 0; size > i; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(data[i]);
		}
		sb.append(')');
		return sb.toString();
	}
}
