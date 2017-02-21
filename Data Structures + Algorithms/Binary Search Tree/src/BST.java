import java.util.*;

/**
 * A Binary Search Tree
 * @author Mani Japra
 */
public class BST<T extends Comparable<? super T>> implements BSTInterface<T> {
    private BSTNode<T> root;
    private int size;

    /**
     * A no argument constructor that should initialize an empty BST
     */
    public BST() {
        this.root = new BSTNode<T>(null);
        this.size = 0;
    }

    /**
     * Initializes the BST with the data in the collection. The data in the BST
     * should be added in the same order it is in the collection.
     *
     * @param data the data to add to the tree
     * @throws IllegalArgumentException if data or any element in data is null
     */
    public BST(Collection<T> data) {
        if (data == null || data.contains(null)) {
            throw new IllegalArgumentException(
                    "The Data entered contains a null entry."
            );
        }
        for (T e : data) {
            add(e);
        }
    }

    /**
     * Recursively adds the data in the add(T data) method.
     * @param node The Current Node in the BST
     * @param data The data being added
     */
    private void insert(BSTNode<T> node, T data) {
        if (data.compareTo(node.getData()) < 0) {
            if (node.getLeft() == null) {
                node.setLeft(new BSTNode<T>(data));
                this.size++;
            } else {
                insert(node.getLeft(), data);
            }
        } else if (data.compareTo(node.getData()) > 0) {
            if (node.getRight() == null) {
                node.setRight(new BSTNode<T>(data));
                this.size++;
            } else {
                insert(node.getRight(), data);
            }
        }
    }

    /**
     * Returns the Node that contains the data entered in the parameter.
     * @param data The data of the node being searched for
     * @param node The current node in the BST
     * @return The Node containing the data in the parameter.
     */
    private BSTNode<T> getNode(BSTNode<T> node, T data) {
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
     * Checks to see if a node is a leaf in the BST
     * @param node The node being checked
     * @return True if the node is a leaf; False Otherwise.
     */
    private boolean isLeaf(BSTNode<T> node) {
        return (node.getLeft() == null && node.getRight() == null);
    }

    /**
     * Finds successor of the current node entered
     * @param cur The current node location.
     * @return The successor of the current node.
     */
    private BSTNode<T> getMinimum(BSTNode<T> cur) {
        if (this.isLeaf(cur) || cur.getLeft() == null) {
            return cur;
        } else {
            return getMinimum(cur.getLeft());
        }
    }

    @Override
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The Data is contains null."
            );
        } else if (this.root.getData() == null) {
            this.root = new BSTNode<T>(data);
            this.size++;
        } else {
            insert(this.root, data);
        }
    }

    /**
     * Deletes node recursively
     * @param data The Data being deleted
     * @param node Node to be deleted
     * @return The Node being deleted
     */
    private BSTNode<T> delete(T data, BSTNode<T> node) {
        if (data.compareTo(node.getData()) < 0) {
            node.setLeft(delete(data, node.getLeft()));
        } else if (data.compareTo(node.getData()) > 0) {
            node.setRight(delete(data, node.getRight()));
        } else if (!isLeaf(node)) {
            node.setData(getMinimum(node.getRight()).getData());
            node.setRight(delete(node.getData(), node.getRight()));
        } else {
            node = (node.getLeft() != null) ? node.getLeft() : node.getRight();
        }
        return node;
    }

    @Override
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The Data entered is null."
            );
        }
        BSTNode<T> node = getNode(this.root, data);
        if (node == null) {
            throw new NoSuchElementException(
                    "The Data is not located in the BST."
            );
        }
        T removedData = node.getData();
        delete(data, this.root);
        size--;
        return removedData;
    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "The Data entered is null."
            );
        }

        BSTNode<T> ret = getNode(this.root, data);
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
                    "The Data entered is null."
            );
        }
        BSTNode<T> ret = getNode(this.root, data);
        return (ret != null);
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Traverses the BST recursively (Pre-Order)
     * @param node The root node
     * @return The list of data
     */
    private List<T> traversePre(BSTNode<T> node) {
        List<T> retList = new ArrayList<T>();
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
     * Traverses the BST recursively (Post-Order)
     * @param node The root node
     * @return The list of data
     */
    private List<T> traversePost(BSTNode<T> node) {
        List<T> retList = new ArrayList<T>();
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
     * Traverses the BST recursively (In-Order)
     * @param node The root node
     * @return The list of data
     */
    private List<T> traverseInOrder(BSTNode<T> node) {
        List<T> retList = new ArrayList<T>();
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
        BSTNode<T> cur = root;
        if (this.root == null) {
            return null;
        }
        LinkedList<BSTNode> que = new LinkedList<BSTNode>();
        LinkedList<T> list = new LinkedList<T>();
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
        this.root = new BSTNode<T>(null);
        this.size = 0;
    }

    /**
     * Calculates height of BST
     * @param node The current node
     * @return The height of the BST
     */
    private int findHeight(BSTNode<T> node) {
        if (node.getData() == null) {
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
     * Calculates the depth of the BST
     * @param node The current node
     * @return The depth of the BST
     */
    private int calcDepth(BSTNode<T> node) {
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
                    "The Data entered is null."
            );
        }
        BSTNode<T> node = getNode(this.root, data);
        return calcDepth(node);
    }

    /**
     * THIS METHOD IS ONLY FOR TESTING PURPOSES.
     * DO NOT USE IT IN YOUR CODE
     * DO NOT CHANGE THIS METHOD
     *
     * @return the root of the tree
     */
    public BSTNode<T> getRoot() {
        return root;
    }
}
