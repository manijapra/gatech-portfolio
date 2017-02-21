import java.util.NoSuchElementException;

/**
 * ArrayStack
 * Implementation of a stack using
 * an array as a backing structure
 *
 * @author Mani Japra
 * @version 1.0
 */
public class ArrayStack<T> implements StackADT<T> {

    // Do not add instance variables
    private T[] backing;
    private int size;

    /**
     * Construct an ArrayStack with
     * an initial capacity of INITIAL_CAPACITY.
     *
     * Use constructor chaining.
     */
    public ArrayStack() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Construct an ArrayStack with the specified
     * initial capacity of initialCapacity
     * @param initialCapacity Initial capacity of the backing array.
     */
    public ArrayStack(int initialCapacity) {
        backing = (T[]) new Object[initialCapacity];
        size = 0;
    }

    @Override
    public void push(T data) {
        if (data == null) {
            throw new IllegalArgumentException("The data provided is null.");
        } else if (size == backing.length) {
            resizeArray();
            size++;
            backing[size - 1] = data;
        } else {
            size++;
            backing[size - 1] = data;
        }
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("The Stack is empty.");
        }
        size--;
        T retData = backing[size];
        backing[size] = null;
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
        for (int i = 0; i < backing.length; i++) {
            newArray[i] = backing[i];
        }
        backing = newArray;
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
