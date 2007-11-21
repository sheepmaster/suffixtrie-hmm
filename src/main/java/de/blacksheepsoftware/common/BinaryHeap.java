package de.blacksheepsoftware.common;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Binary heap implementation of a priority queue.<p>
 *
 * @author Peter Williams
 * @see <a href="http://www.cs.cmu.edu/~russells/software/discrete/codes/BinaryHeap.java">http://www.cs.cmu.edu/~russells/software/discrete/codes/BinaryHeap.java</a>
 */

public class BinaryHeap implements PriorityQueue {

    private Object[] heap;  // the heap
    private int size;           // number of items in the heap
    private Comparator comp;
  
    public static final Comparator naturalComp = new Comparator() {
    	public int compare(Object o1, Object o2) {
    		return ((Comparable)o1).compareTo(o2);
    	}
    };
    
    public static final Comparator reverseComp = new Comparator(){
    	public int compare(Object o1, Object o2) {
    		return ((Comparable)o2).compareTo(o1);
    	}
    };
    
    /**
     * Constructs the binary heap.
     */
    public BinaryHeap() {
       this(naturalComp);
    }
  
    public BinaryHeap(Comparator comp) {
    		this.comp = comp;
    		heap = new Comparable[1];
    		size = 0;
    }
    
    /**
     * Tests if the heap is empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the current size of the queue
     * @return the current size of the queue
     */
    public int size() {
        return size;
    }
  
    /**
     * Adds and item to the heap.
     * @param item the item to add.
     */
    public void add(Object item) {
        // grow the heap if necessary
        if (size == heap.length) {
        	Object[] newHeap = new Comparable[2 * heap.length];
            System.arraycopy(heap, 0, newHeap, 0, heap.length);
            heap = newHeap;
        }
        // find where to insert while rearranging the heap if necessary
        int parent, child = size++; // the next available slot in the heap
        while (child > 0 && comp.compare(heap[parent = (child - 1) / 2], item) < 0) {//SWITCHED SECOND >, USED TO BE <
            heap[child] = heap[parent];
            child = parent;
        }
        heap[child] = item;
    }
  
    /**
     * Removes an item of highest priority from the heap.
     * @return an item of highest priority.
     * @exception NoSuchElementException if the heap is empty.
     */
    public Object remove() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        Object result = heap[0];   // to be returned
        Object item = heap[--size]; // to be reinserted 
        int child, parent = 0; 
        while ((child = (2 * parent) + 1) < size) {
            // if there are two children, compare them
            if (child + 1 < size && comp.compare(heap[child], heap[child + 1]) < 0) {//SWITCHED SECOND >, USED TO BE <
                ++child;
            }
            // compare item with the larger
            if (comp.compare(item, heap[child]) < 0) {
                heap[parent] = heap[child];
                parent = child;
            } else {
                break;
            }
        }
        heap[parent] = item;
        return result;
    }

}
