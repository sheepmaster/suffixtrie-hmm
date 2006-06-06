/**
 * 
 */
package de.blacksheepsoftware.t9;

import java.util.LinkedList;

abstract class CompList extends LinkedList implements Comparable {
	public abstract int compareTo(Object o);
}