import java.util.NoSuchElementException;

/**
 * @author Mani Japra
 */
public class MaxHeap<T extends Comparable<? super T>>
    implements HeapInterface<T> {

    private T[] arr;
    private int size;
    // Do not add any more instance variables

    /**
     * Creates a MaxHeap.
     */
    public MaxHeap() {
        arr = (T[]) new Comparable[STARTING_SIZE];
        arr[0] = null;
        size = 0;
    }

    /**
     * Resizes the array to double its original size.
     */
    private void resize() {
        T[] temp = (T[]) new Comparable[arr.length * 2];
        temp[0] = null;

        for (int i = 0; i < arr.length - 2; i++) {
            temp[i + 1] = arr[i + 1];
        }

        arr = temp;
    }

    /**
     * Returns parent of current location in Heap.
     * @param loc Current Location
     * @return Location of parent
     */
    private int getParent(int loc) {
        return loc / 2;
    }

    /**
     * Returns location of left child of current location in Heap.
     * @param loc Current Location
     * @return Location of left child
     */
    private int getLeft(int loc) {
        return (2 * loc);
    }

    /**
     * Returns location of right child of current location in Heap.
     * @param loc Current Location
     * @return Location of right child
     */
    private int getRight(int loc) {
        return (2 * loc) + 1;
    }

    /**
     * Returns a boolean value showing whether the current loc is a leaf.
     * @param loc Current Location
     * @return True if leaf; False otherwise
     */
    private boolean isLeaf(int loc) {
        return (loc <= size && (loc >= (size / 2)));
    }

    /**
     * Swaps the first location with the second location
     * @param first First Location
     * @param sec Secon Location
     */
    private void swap(int first, int sec) {
        T temp = arr[first];
        arr[first] = arr[sec];
        arr[sec] = temp;
    }

    @Override
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "The Item is null."
            );
        }
        if (size + 1 == arr.length) {
            resize();
        }
        size++;
        arr[size] = item;
        int curLoc = size;
        while (getParent(curLoc) != 0
                && arr[curLoc].compareTo(arr[getParent(curLoc)]) > 0) {
            swap(curLoc, getParent(curLoc));
            curLoc = getParent(curLoc);
        }
    }

    @Override
    public T remove() {
        if (isEmpty()) {
            throw new NoSuchElementException(
                    "The Heap is empty."
            );
        }

        T removed = arr[1];
        arr[1] = arr[size];
        heapify(1);
        size--;
        if (size == 2) {
            if (arr[1].compareTo(arr[size]) < 0) {
                swap(1, size);
            }
        }
        return removed;
    }

    /**
     * Re-sorts the heap.
     * @param loc The location to start the re-sort.
     */
    private void heapify(int loc) {
        if (!isLeaf(loc)) {
            if (arr[loc].compareTo(arr[getLeft(loc)]) < 0
                    || arr[loc].compareTo(arr[getRight(loc)]) < 0) {
                if (arr[getLeft(loc)].compareTo(arr[getRight(loc)]) > 0) {
                    swap(loc, getLeft(loc));
                    heapify(getLeft(loc));
                } else {
                    swap(loc, getRight(loc));
                    heapify(getRight(loc));
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        arr = (T[]) new Comparable[STARTING_SIZE];
        arr[0] = null;
        size = 0;
    }

    @Override
    public Comparable[] getBackingArray() {
        return arr;
    }
}
