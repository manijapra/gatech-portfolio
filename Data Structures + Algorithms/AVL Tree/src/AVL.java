import java.util.*;

/**
 * Creates an AVL Tree
 *
 * @author Mani Japra
 * @version 1.0
 */
public class AVL<T extends Comparable<? super T>> implements AVLInterface<T> {

    // Do not make any new instance variables.
    private AVLNode<T> root;
    private int size;

    /**
     * A no argument constructor that should initialize an empty BST
     */
    public AVL() {
        root = new AVLNode<>(null);
        size = 0;
    }

    /**
     * Initializes the AVL with the data in the collection. The data
     * should be added in the same order it is in the collection.
     *
     * @param data the data to add to the tree
     * @throws IllegalArgumentException if data or any element in data is null
     */
    public AVL(Collection<T> data) {
        if (data == null || data.contains(null)) {
            throw new IllegalArgumentException(
                    "The data entered contains a null entry."
            );
        }
        for (T e: data) {
            add(e);
        }
    }

    /**
     * Gets the node that has the given data. null otherwise
     * @param node The root
     * @param data The data being searched for
     * @return The node that contains the data.
     */
    private AVLNode<T> getNode(AVLNode<T> node, T data) {
        if (node.getData() == null) {
            return null;
        }
        if (data.compareTo(node.getData()) < 0) {
            if (node.getLeft() == null) {
                return null;
            } else {
                return getNode(node.getLeft(), data);
            }
        } else if (data.compareTo(node.getData()) > 0) {
            if (node.getRight() == null) {
                return null;
            } else {
                return getNode(node.getRight(), data);
            }
        } else {
            return node;
        }
    }

    /**
     * Recursively inserts a new node with the data entered
     * @param data The data of the node being entered
     * @param node The root node
     * @return The new node that was just created
     */
    private AVLNode<T> insert(T data, AVLNode<T> node) {
        if (node == null || node.getData() == null) {
            return new AVLNode<T>(data);
        }
        int compareResult = data.compareTo(node.getData());

        if (compareResult < 0) {
            node.setLeft(insert(data, node.getLeft()));
            if (findHeight(node.getLeft()) - findHeight(node.getRight()) == 2) {
                if (data.compareTo(node.getLeft().getData()) < 0) {
                    node = rotateWithLeftChild(node);
                } else {
                    node = doubleWithLeftChild(node);
                }
            }
        } else if (compareResult > 0) {
            node.setRight(insert(data, node.getRight()));
            if (findHeight(node.getRight()) - findHeight(node.getLeft()) == 2) {
                if (data.compareTo(node.getRight().getData()) > 0) {
                    node = rotateWithRightChild(node);
                } else {
                    node = doubleWithRightChild(node);
                }
            }
        }
        node.setHeight(Math.max(findHeight(node.getLeft()),
                findHeight(node.getRight())) + 1);
        return node;
    }

    /**
     * Rotates the current node with the left child
     * @param sec The current node
     * @return The node rotated with the left child
     */
    private AVLNode<T> rotateWithLeftChild(AVLNode<T> sec) {
        AVLNode<T> one = sec.getLeft();
        sec.setLeft(one.getRight());
        one.setRight(sec);
        sec.setHeight(Math.max(findHeight(sec.getLeft()),
                findHeight(sec.getRight())) + 1);
        one.setHeight(Math.max(findHeight(one.getLeft()),
                findHeight(sec)) + 1);
        return one;
    }

    /**
     * Rotates the current node with the right child
     * @param one The current node
     * @return The node rotated with the right child
     */
    private AVLNode<T> rotateWithRightChild(AVLNode<T> one) {
        AVLNode<T> sec = one.getRight();
        one.setRight(sec.getLeft());
        sec.setLeft(one);
        one.setHeight(Math.max(findHeight(one.getLeft()),
                findHeight(one.getRight())) + 1);
        sec.setHeight(Math.max(findHeight(sec.getRight()),
                findHeight(one)) + 1);
        return sec;
    }

    /**
     * Double rotates with Left child
     * @param three The current node
     * @return The current node rotated twice with left child
     */
    private AVLNode<T> doubleWithLeftChild(AVLNode<T> three) {
        three.setLeft(rotateWithRightChild(three.getLeft()));
        return rotateWithLeftChild(three);
    }

    /**
     * Double rotates with right child
     * @param one the current node
     * @return the current node rotated twice with right child
     */
    private AVLNode<T> doubleWithRightChild(AVLNode<T> one) {
        one.setRight(rotateWithLeftChild(one.getRight()));
        return rotateWithRightChild(one);
    }


    @Override
    public void add(T data) {
        this.root = insert(data, root);
        size++;
    }

    /**
     * Removes the node that currently holds the data inputed
     * @param data the data that the deleted node should contain
     * @param node the root node
     * @return the updated root node with the node deleted
     */
    private AVLNode<T> remove(T data, AVLNode<T> node) {
        if (node == null) {
            return node;
        }

        int compareResult = data.compareTo(node.getData());

        if (compareResult < 0) {
            node.setLeft(remove(data, node.getLeft()));
        } else if (compareResult > 0) {
            node.setRight(remove(data, node.getRight()));
        } else if (node.getLeft() != null && node.getRight() != null) {
            node.setData(findMin(node.getRight()).getData());
            node.setRight(remove(node.getData(), node.getRight()));
        } else {
            node = (node.getLeft() != null) ? node.getLeft() : node.getRight();
        }
        return balance(node);
    }

    /**
     *
     * Balances the AVL Tree
     * @param node The root node
     * @return The updated root node after being balanced
     */
    private AVLNode<T> balance(AVLNode<T> node) {
        if (node == null) {
            return node;
        }
        node.setBalanceFactor(Math.abs(findHeight(node.getLeft())
                - findHeight(node.getRight())));
        if (findHeight(node.getLeft())
                - findHeight(node.getRight()) > 2) {
            if (findHeight(node.getLeft().getLeft())
                    >= findHeight(node.getLeft().getRight())) {
                node = rotateWithLeftChild(node);
            } else {
                node = doubleWithLeftChild(node);
            }
        } else if (findHeight(node.getRight())
                - findHeight(node.getLeft()) > 2) {
            if (findHeight(node.getRight().getRight())
                    >= findHeight(node.getRight().getLeft())) {
                node = rotateWithRightChild(node);
            } else {
                node = doubleWithRightChild(node);
            }
        }
        node.setHeight(Math.max(findHeight(node.getLeft()),
                findHeight(node.getRight())) + 1);
        return node;
    }

    /**
     * Finds the minimum child
     * @param node The current node
     * @return The node that is the minimum child
     */
    private AVLNode<T> findMin(AVLNode<T> node) {
        if (node == null) {
            return node;
        }

        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    @Override
    public T remove(T data) {
        root = remove(data, root);
        size--;
        return data;
    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The data entered is null."
            );
        }
        AVLNode<T> ret = getNode(this.root, data);
        if (ret == null) {
            throw new NoSuchElementException(
                    "The data was not found."
            );
        }
        return ret.getData();

    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The data entered is null."
            );
        }
        AVLNode<T> ret = getNode(this.root, data);
        return (ret != null);
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Recursively traverses the tree in a pre-order method
     * @param node The root node
     * @return The List containing the data in pre-order
     */
    private List<T> traversePre(AVLNode<T> node) {
        List<T> retList = new ArrayList<>();
        if (node == null) {
            return retList;
        }
        retList.add(node.getData());
        retList.addAll(traversePre(node.getLeft()));
        retList.addAll(traversePre(node.getRight()));
        return retList;
    }

    @Override
    public List<T> preorder() {
        return traversePre(this.root);
    }

    /**
     * Recursively traverses the tree in a post-order method
     * @param node The root node
     * @return The list containing the data in post-order
     */
    private List<T> traversePost(AVLNode<T> node) {
        List<T> retList = new ArrayList<>();
        if (node == null) {
            return retList;
        }
        retList.addAll(traversePost(node.getLeft()));
        retList.addAll(traversePost(node.getRight()));
        retList.add(node.getData());
        return retList;
    }

    @Override
    public List<T> postorder() {
        return traversePost(this.root);
    }

    /**
     * Recursively traverses the tree in an in-order method
     * @param node The root node
     * @return The list containing the data in-order
     */
    private List<T> traverseInOrder(AVLNode<T> node) {
        List<T> retList = new ArrayList<>();
        if (node == null) {
            return retList;
        }
        retList.addAll(traverseInOrder(node.getLeft()));
        retList.add(node.getData());
        retList.addAll(traverseInOrder(node.getRight()));
        return retList;
    }

    @Override
    public List<T> inorder() {
        return traverseInOrder(this.root);
    }

    @Override
    public List<T> levelorder() {
        AVLNode<T> cur = root;
        if (this.root == null) {
            return null;
        }
        LinkedList<AVLNode> que = new LinkedList<>();
        LinkedList<T> list = new LinkedList<>();
        que.push(cur);
        while (!que.isEmpty()) {
            cur = que.removeFirst();
            list.add(cur.getData());
            if (cur.getLeft() != null) {
                que.add(cur.getLeft());
            }
            if (cur.getRight() != null) {
                que.add(cur.getRight());
            }
        }
        return list;
    }

    @Override
    public void clear() {
        this.root = new AVLNode<T>(null);
        this.size = 0;
    }

    /**
     * Finds the height recursively
     * @param node The current node
     * @return The height of the node
     */
    private int findHeight(AVLNode<T> node) {
        if (node == null) {
            return -1;
        }

        int lefth = findHeight(node.getLeft());
        int righth = findHeight(node.getRight());

        if (lefth > righth) {
            return lefth + 1;
        } else {
            return righth + 1;
        }
    }

    @Override
    public int height() {
        return findHeight(this.root);
    }

    /**
     * Recursively calculates the depth of a node
     * @param node The current node
     * @return The Depth of the node
     */
    private int calcDepth(AVLNode<T> node) {
        if (node == null) {
            return 0;
        }

        int left = calcDepth(node.getLeft());
        int right = calcDepth(node.getRight());

        int depth = (left > right) ? left + 1 : right + 1;
        return depth;
    }

    @Override
    public int depth(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The data entered is null."
            );
        }
        AVLNode<T> node = getNode(this.root, data);
        if (node.equals(null)) {
            throw new NoSuchElementException(
                    "The data is not in the tree."
            );
        }
        return calcDepth(node);
    }

    /**
     * THIS METHOD IS ONLY FOR TESTING PURPOSES.
     * DO NOT USE IT IN YOUR CODE
     * DO NOT CHANGE THIS METHOD
     *
     * @return the root of the tree
     */
    public AVLNode<T> getRoot() {
        return root;
    }
}