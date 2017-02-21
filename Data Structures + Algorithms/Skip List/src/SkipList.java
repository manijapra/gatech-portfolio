import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Mani Japra
 */
public class SkipList<T extends Comparable<? super T>>
    implements SkipListInterface<T> {
    // Do not add any additional instance variables
    private CoinFlipper coinFlipper;
    private int size;
    private Node<T> head;

    /**
     * Constructs a SkipList object that stores data in ascending order.
     * When an item is inserted, the flipper is called until it returns a tails.
     * If, for an item, the flipper returns n heads, the corresponding node has
     * n + 1 levels.
     *
     * @param coinFlipper the source of randomness
     */
    public SkipList(CoinFlipper coinFlipper) {
        this.coinFlipper = coinFlipper;
        head = new Node<>(null, 0);
        size = 0;
    }

    @Override
    public T first() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "The Skip List is empty."
            );
        }
        Node<T> cur = head;
        while (cur.getDown() != null) {
            cur = cur.getDown();
        }
        return cur.getNext().getData();
    }

    @Override
    public T last() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "The Skip List is empty."
            );
        }
        Node<T> cur = head;
        while (cur.getNext() != null || cur.getDown() != null) {
            while (cur.getNext() != null) {
                cur = cur.getNext();
            }
            if (cur.getDown() != null) {
                cur = cur.getDown();
            }
        }
        return cur.getData();
    }

    @Override
    public void put(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The parameter was null."
            );
        }

        Node<T> p = skipSearch(data);
        Node<T> q = new Node<T>(data, p.getLevel(), p, p.getNext(), null, null);
        if (p.getNext() != null) {
            p.getNext().setPrev(q);
        }
        p.setNext(q);
        int initialFlip = coinFlipper.getNumFlips();
        coinFlipper.flipCoin();
        int numLoop = coinFlipper.getNumFlips() - initialFlip;
        int h = getHeight();
        for (int i = 0; i < numLoop; i++) {
            if (i >= h) {
                h++;
                Node<T> newHead = new Node<>(null, 0);
                newHead.setDown(head);
                head.setUp(newHead);
                Node<T> cur = head;
                cur.setLevel(cur.getLevel() + 1);
                Node<T> next = cur.getNext();
                while (next != null) {
                    next.setLevel(cur.getLevel());
                    next = next.getNext();
                }
                Node<T> bot = cur.getDown();
                while (bot != null) {
                    bot.setLevel(bot.getLevel() + 1);
                    Node<T> botNext = bot.getNext();
                    while (botNext != null) {
                        botNext.setLevel(bot.getLevel());
                        botNext = botNext.getNext();
                    }
                    bot = bot.getDown();
                }
                head = newHead;
                while (p.getUp() == null) {
                    p = p.getPrev();
                }
                p = p.getUp();
                q = insertAfterAbove(p, q, data);
            }
        }
        size++;
    }

    /**
     * Returns height of skip list
     * @return height of skip list
     */
    private int getHeight() {
        int h = 0;
        Node<T> cur = head;
        while (cur.getDown() != null) {
            h++;
            cur = cur.getDown();
        }
        return h;
    }

    /**
     * Algorithm that inserts a position storing the entry data after
     * position p and above q, returning the new position r
     * @param p Node that comes before the data insertion point
     * @param q Node that comes above the data insertion point
     * @param data The data being inserted
     * @return The new position
     */
    private Node<T> insertAfterAbove(Node<T> p, Node<T> q, T data) {
        Node<T> temp = new Node<T>(data, p.getLevel(), p, p.getNext(), null, q);
        if (p.getNext() != null) {
            p.getNext().setPrev(temp);
        }
        p.setNext(temp);
        q.setUp(temp);
        return temp;
    }

    @Override
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The parameter is null."
            );
        }
        if (!contains(data)) {
            throw new NoSuchElementException(
                    "The item is not in the skip list."
            );
        }

        Node<T> p = skipSearch(data);
        Node<T> cur = p;
        while (cur.getUp() != null) {
            cur = p.getUp();
            cur.getPrev().setNext(p.getNext());
            if (cur.getNext() != null) {
                cur.getNext().setPrev(p.getPrev());
            }
            cur.setUp(null);
        }
        p.getPrev().setNext(p.getNext());
        if (p.getNext() != null) {
            p.getNext().setPrev(p.getPrev());
        }
        removeEmptyRows();
        size--;
        return p.getData();
    }

    /**
     * Removes any empty rows after removing a node.
     */
    private void removeEmptyRows() {
        if (head.getNext() == null && head.getDown() != null) {
            head = head.getDown();
            head.setUp(null);
        }
        Node<T> cur = head;
        while (cur.getDown() != null) {
            if (cur.getNext() == null) {
                cur.getUp().setDown(cur.getDown());
                cur.getDown().setUp((cur.getUp()));
                cur.setUp(null);
                cur.setDown(null);
            }
            cur = cur.getDown();
        }
    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The parameter is null."
            );
        }
        Node<T> probe = skipSearch(data);
        T pData = probe.getData();

        return (!(pData == null) && pData.compareTo(data) == 0);
    }

    /**
     * Algorithm to search a skip list for a node
     * @param data The node with this data is being searched for
     * @return Node in the bottom list with the largest data
     *         which is equal to or less than the data entered.
     */
    private Node<T> skipSearch(T data) {
        Node<T> p = head;
        while (p.getDown() != null) {
            p = p.getDown();
            while (p.getNext() != null
                    && data.compareTo(p.getNext().getData()) >= 0) {
                p  = p.getNext();
            }
        }
        if (p.getNext() != null) {
            while (p.getNext() != null
                    && data.compareTo(p.getNext().getData()) >= 0) {
                p  = p.getNext();
            }
        }
        return p;
    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The parameter is null."
            );
        }
        if (!contains(data)) {
            throw new NoSuchElementException(
                    "The item is not in the skip list."
            );
        }
        Node<T> probe = skipSearch(data);
        return probe.getData();
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
    public Set<T> dataSet() {
        LinkedHashSet<T> ret = new LinkedHashSet<>();
        if (size == 0) {
            return ret;
        }

        Node<T> cur = skipSearch(first());
        while (cur.getNext() != null) {
            ret.add(cur.getData());
            cur = cur.getNext();
        }

        return ret;
    }

    @Override
    public Node<T> getHead() {
        return head;
    }

}
