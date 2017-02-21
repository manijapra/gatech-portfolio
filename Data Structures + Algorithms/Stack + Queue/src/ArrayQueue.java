import java.util.NoSuchElementException;

/**
 * ArrayQueue
 * Implementation of a queue using
 * an array as the backing structure
 *
 * @author Mani Japra
 * @version 1.0
 */
public class ArrayQueue<T> implements QueueADT<T> {

    // Do not add instance variables
    private T[] backing;
    private int size;
    private int front;
    private int back;

    /**
     * Construct an ArrayQueue with an
     * initial capacity of INITIAL_CAPACITY
     *
     * Use Constructor Chaining
     */
    public ArrayQueue() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Construct an ArrayQueue with the specified
     * initial capacity of initialCapacity
     * @param initialCapacity Initial capacity of the backing array.
     */
    public ArrayQueue(int initialCapacity) {
        backing = (T[]) new Object[initialCapacity];
        size = 0;
        front = 0;
        back = 0;
    }

    @Override
    public void enqueue(T data) {
        if (data == null) {
            throw new IllegalArgumentException("The data provided is null!");
        } else if (size == backing.length) {
            resizeArray();
            front = 0;
            back = size;

            size++;
            backing[back] = data;
            back++;
        } else {
            if (back == backing.length) {
                back = 0;
                backing[back] = data;
                size++;
                back++;
            } else {
                back++;
                size++;
                backing[back - 1] = data;
            }
        }
    }

    @Override
    public T dequeue() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("This Queue is Empty.");
        }
        T retData = backing[front];
        backing[front] = null;
        if (front == backing.length - 1) {
            front = 0;
        } else {
            front++;
            size--;
        }
        return retData;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Resizes the Backing Array to twice its current size.
     */
    private void resizeArray() {
        T[] newArray = (T[]) new Object[size * 2];
        T[] oldArray = getResizedBackingArray();
        for (int i = 0; i < backing.length; i++) {
            newArray[i] = oldArray[i];
        }
        backing = newArray;
    }

    /**
     * Returns the backing array as a concatenated array without null spaces
     * @return Concatenated Array
     */
    private T[] getResizedBackingArray() {
        T[] returnArray = (T[]) new Object[size];
        if (back <= front) {
            int curIndex = 0;
            for (int i = 0, j = front; j < backing.length; i++, j++) {
                returnArray[i] = backing[j];
                curIndex++;
            }
            for (int i = curIndex, j = 0; j < back; i++, j++) {
                returnArray[i] = backing[j];
            }
        } else {
            for (int i = 0, j = front; j < back; i++, j++) {
                returnArray[i] = backing[j];
            }
        }
        return returnArray;
    }

    /**
     * Returns the backing array for your queue.
     * This is for grading purposes only. DO NOT EDIT THIS METHOD.
     *
     * @return the backing array
     */
    public Object[] getBackingArray() {
        return backing;
    }
}