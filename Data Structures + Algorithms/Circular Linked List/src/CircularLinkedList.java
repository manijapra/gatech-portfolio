/**
 * CircularLinkedList implementation
 * @author Maninder Japra
 * @version 1.0
 */

public class CircularLinkedList<T> implements LinkedListInterface<T> {

    // Do not add new instance variables.
    private LinkedListNode<T> head;
    private int size;

    @Override
    public void addAtIndex(int index, T data) {
        if (index < size - 1 || index < 0) {
            throw new IndexOutOfBoundsException();
        } else if (this.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            LinkedListNode<T> nextNode = this.getNodeAt(index);
            LinkedListNode<T> prevNode = nextNode.getPrevious();
            LinkedListNode<T> newNode =
                    new LinkedListNode<T>(data, prevNode, nextNode);
            nextNode.setPrevious(newNode);
            prevNode.setNext(newNode);
            size++;
        }
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        LinkedListNode<T> retN = head;
        for (int i = 0; i < index; i++) {
            retN = retN.getNext();
        }
        return retN.getData();
    }

    @Override
    public T removeAtIndex(int index) {
        if (index < size - 1 || index < 0) {
            throw new IndexOutOfBoundsException();
        } else {
            LinkedListNode<T> delNode = this.getNodeAt(index);
            LinkedListNode<T> prevNode = delNode.getPrevious();
            LinkedListNode<T> nexNode = delNode.getNext();
            prevNode.setNext(nexNode);
            nexNode.setPrevious(prevNode);
            return delNode.getData();
        }
    }

    @Override
    public void addToFront(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else if (isEmpty()) {
            head = new LinkedListNode<T>(data);
            head.setNext(head);
            head.setPrevious(head);
            size++;
        } else {
            LinkedListNode<T> newHead =
                    new LinkedListNode<T>(data, this.getNodeAt(size - 1), head);
            head.setPrevious(newHead);
            this.getNodeAt(size - 1).setNext(newHead);
            head = newHead;
            size++;
        }
    }

    @Override
    public void addToBack(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else if (isEmpty()) {
            head = new LinkedListNode<T>(data);
            head.setNext(head);
            head.setPrevious(head);
            size++;
        } else {
            LinkedListNode<T> curTail = this.getNodeAt(size - 1);
            LinkedListNode<T> newTail =
                    new LinkedListNode<T>(data, curTail, head);
            curTail.setNext(newTail);
            head.setPrevious(newTail);
            size++;
        }
    }

    @Override
    public T removeFromFront() {
        if (size == 1) {
            LinkedListNode<T> deadTail = head;
            this.clear();
            return deadTail.getData();
        }
        LinkedListNode<T> deadNode = head;
        head = deadNode.getNext();
        LinkedListNode<T> lastNode = deadNode.getPrevious();
        head.setPrevious(lastNode);
        lastNode.setNext(head);
        size--;
        return deadNode.getData();

    }

    @Override
    public T removeFromBack() {
        if (isEmpty()) {
            return null;
        } else if (size == 1) {
            LinkedListNode<T> deadTail = head;
            this.clear();
            return deadTail.getData();
        }
        LinkedListNode<T> deadTail = head.getPrevious();
        LinkedListNode<T> newTail = deadTail.getPrevious();
        newTail.setNext(head);
        head.setPrevious(newTail);
        size--;
        return deadTail.getData();
    }

    @Override
    public Object[] toArray() {
        LinkedListNode<T> curNode = head;
        Object[] array = new Object[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = curNode;
            curNode = curNode.getNext();
        }
        return array;
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
        head = null;
        size = 0;
    }

    @Override
    public LinkedListNode<T> getHead() {
        return head;
    }

    /**
     * Returns a LinkedListNode to access its Next and Previous attributes
     * @param index The index of the element
     * @return The LinkedListNode stored at that index
     */
    private LinkedListNode<T> getNodeAt(int index) {
        if (this.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (index < size - 1 || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        LinkedListNode<T> retN = head;
        for (int i = 0; i < index; i++) {
            retN = retN.getNext();
        }
        return retN;
    }
}