/**
 * @author Long Pan
 * 
 * This class is for partial sorting. While total sorting is 
 * the problem of returning a list of items such that 
 * its elements all appear in order, partial sorting is 
 * returning a list of the k smallest (or k largest) elements in order.
 * 
 * The algorithm is based on heap. 
 * Heaps admit a simple single-pass partial sort when k is fixed: 
 * insert the first k elements of the input into a max-heap. 
 * Then make one pass over the remaining elements, 
 * add each to the heap in turn, and remove the largest element. 
 * Each insertion operation takes O(log k) time, 
 * resulting in O(n log k) time overall. 
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;


public class PartialSort<E> {
	private final PriorityQueue<E> heap;
	private final Comparator<E> comparator;
	private final int topK;
	private int count;	
	
	PartialSort(int topK, Comparator<E> comparator) {
		this.topK = topK;
		this.comparator = comparator;
		Comparator<E> inverseComparator = new Comparator<E>(){
			@Override
			public int compare(E o1, E o2) {				
				return comparator.compare(o2, o1);
			}			
		};
		
		heap = new PriorityQueue<E>(topK, inverseComparator);
		count = 0;
	}
	
	public void add(E item) {
		if (item == null) {
			return;
		}		
		
		if (count < topK) {
			heap.offer(item);
			count++;
		} else {
			if (comparator.compare(heap.peek(), item) > 0) {
				heap.poll();
				heap.offer(item);
			}			
		}
	}
	
	public ArrayList<E> getSortedTopK() {
		ArrayList<E> res = new ArrayList<E>();
		while (!heap.isEmpty()) {
			res.add(heap.poll());			
		}
		
		Collections.reverse(res);
		return res;
	}

}
