import java.util.*;

/**
 * A HashMap implementation.
 * @author Mani Japra
 */
public class HashMap<K, V> implements HashMapInterface<K, V> {

    // Do not make any new instance variables.
    private MapEntry<K, V>[] table;
    private int size;

    /**
     * Create a hash map with no entries.
     */
    public HashMap() {
        this.table = (MapEntry<K, V>[]) new MapEntry[STARTING_SIZE];
        size = 0;
    }

    /**
     * Resizes the array to a new size of 2n + 1,
     * where n was the original size of the array.
     */
    private void resize() {
        MapEntry<K, V>[] newTable =
                (MapEntry<K, V>[]) new MapEntry[(table.length * 2) + 1];
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                newTable = resizeHash(newTable, table[i]);
            }
        }
        table = newTable;
    }

    /**
     * Finds the next location to add a MapEntry.
     * If the table doesn't have an available location.
     * The table is resized and returns the
     * next location available location to insert the MapEntry.
     * @param hash The hash of the key
     * @param key The key
     * @return The next available location to add a MapEntry
     */
    private int findNextLoc(int hash, K key) {
        int quadIter = 1;
        int next = hash;
        if (table[hash] == null || table[hash].getKey().equals(key)) {
            return next;
        } else {
            while (next < table.length) {
                if (table[next] == null || table[next].getKey().equals(key)
                        || table[next].isRemoved()) {
                    return next;
                }
                next = (hash + (quadIter * quadIter)) % table.length;
                quadIter++;
            }
            resize();
            return next;
        }
    }

    /**
     * Finds the next location to add a MapEntry in a table
     * that isn't the backing table.
     * @param newTable A different table that isn't the backing table.
     * @param hash The hash of the key
     * @param key The key
     * @return The next available location to add a
     *         MapEntry in the table provided in the parameter
     */
    private int findNextResized(MapEntry<K, V>[] newTable, int hash, K key) {
        int quadIter = 1;
        int next = hash;
        if (newTable[hash] == null || newTable[hash].getKey().equals(key)) {
            return next;
        } else {
            while (next < newTable.length) {
                if (newTable[next] == null
                        || newTable[next].getKey().equals(key)) {
                    return next;
                }
                next = (hash + (quadIter * quadIter)) % newTable.length;
                quadIter++;
            }
            resize();
            return next;
        }
    }

    /**
     * Hash method made for the resize() function
     * to avoid returning a value entry.
     * @param newTable The table that the new entry is being hashed into
     * @param newEntry The MapEntry being added to the new table.
     * @return A new array that has the new entry stored in it.
     */
    private MapEntry<K, V>[] resizeHash(MapEntry<K, V>[] newTable,
                                        MapEntry<K, V> newEntry) {
        int hash = Math.abs(newEntry.getKey().hashCode()) % newTable.length;
        int nextAvailLoc = findNextResized(newTable, hash, newEntry.getKey());
        if (newTable[nextAvailLoc] != null) {
            newTable[nextAvailLoc].setValue(newEntry.getValue());
        } else {
            newTable[nextAvailLoc] = newEntry;
        }
        return newTable;
    }

    /**
     * Hashes an entry into the Table
     * @param newEntry The entry being hashed into the table
     * @return The value that was previously stored under the same entry.
     *         Null otherwise.
     */
    private V hashEntry(MapEntry<K, V> newEntry) {
        int hash = Math.abs(newEntry.getKey().hashCode()) % table.length;
        int nextAvailLoc = findNextLoc(hash, newEntry.getKey());
        V oldVal = null;
        if (table[nextAvailLoc] != null) {
            if (table[nextAvailLoc].isRemoved()) {
                table[nextAvailLoc] = newEntry;
            }
            oldVal = table[nextAvailLoc].getValue();
            table[nextAvailLoc].setValue(newEntry.getValue());
            size--;
        } else {
            table[nextAvailLoc] = newEntry;
        }
        return oldVal;
    }

    @Override
    public V add(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException(
                    "The key/value provided contains a null."
            );
        }
        double loadFactorCheck = (double) (size + 1) / table.length;
        if (loadFactorCheck > MAX_LOAD_FACTOR) {
            resize();
        }
        MapEntry<K, V> newEntry = new MapEntry<>(key, value);
        V oldVal = hashEntry(newEntry);
        size++;
        return oldVal;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "The key provided is null."
            );
        }
        int removalLoc = getMapEntryLoc(key);
        if (table[removalLoc] == null) {
            throw new NoSuchElementException(
                    "The MapEntry with the specified key does not exist."
            );
        }
        V removedVal = table[removalLoc].getValue();
        table[removalLoc].setRemoved(true);
        size--;
        return removedVal;
    }

    /**
     * Finds the location of a key in the table
     * @param hash The first hash location of the key
     * @param key The key being located
     * @return The location of the key in the table
     */
    private int findLoc(int hash, K key) {
        int quadIter = 1;
        int next = hash;
        if (table[hash] == null || table[hash].getKey().equals(key)) {
            return next;
        } else {
            while (next < table.length) {
                if (table[next] == null || table[hash].getKey().equals(key)) {
                    return next;
                }
                quadIter++;
                next = (hash + (quadIter * quadIter)) % table.length;
            }
            return next;
        }
    }

    /**
     * Returns the location of a MapEntry with the
     * key provided in the table array
     * @param key The key being found in the table
     * @return The location of where the MapEntry with the
     *         key provided is in the table array
     */
    private int getMapEntryLoc(K key) {
        int keyHash = Math.abs(key.hashCode()) % table.length;
        int nextLoc = findLoc(keyHash, key);
        return nextLoc;
    }

    /**
     * Returns the MapEntry with the key provided
     * @param key The key of the MapEntry being located
     * @return The MapEntry with the key the user asked for
     */
    private MapEntry<K, V> getMapEntry(K key) {
        int keyHash = Math.abs(key.hashCode()) % table.length;
        int nextLoc = findLoc(keyHash, key);
        return table[nextLoc];
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "The key provided is null."
            );
        }
        MapEntry<K, V> returnEntry = getMapEntry(key);
        if (returnEntry == null) {
            throw new NoSuchElementException(
                    "The MapEntry with the specified key does not exist."
            );
        }
        return returnEntry.getValue();
    }

    @Override
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "The key provided is null."
            );
        }
        int loc = getMapEntryLoc(key);
        return (table[loc].getKey().equals(key));
    }

    @Override
    public void clear() {
        this.table = (MapEntry<K, V>[]) new MapEntry[STARTING_SIZE];
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public MapEntry<K, V>[] toArray() {
        return this.table;
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keys = new HashSet<>(size);
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && !(table[i].isRemoved())) {
                keys.add(table[i].getKey());
            }
        }
        return keys;
    }

    @Override
    public List<V> values() {
        ArrayList<V> values = new ArrayList<>(size);
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && !(table[i].isRemoved())) {
                values.add(table[i].getValue());
            }
        }
        return values;
    }
}
