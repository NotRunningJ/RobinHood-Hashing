package lvc.cds;

import java.lang.reflect.Array;

/**
 * my implementation of applying robinhood hashing to a hashtable
 * (using offsets and moving the entries around so that entries
 * don't get moved too far from the location they hash to)
 * the goal of this is to see how effective this is compared to 
 * a regular linear hashTable
 */
public class RobinHoodHashTable<K, V> implements Map<K, V> {

    private static final int DEFAULT_CAPACITY = 8;
    private static final double DEFAULT_LOADFACTOR = 0.5;

    private Entry[] table;
    private int size;
    private double loadFactor;


    public RobinHoodHashTable() {
        this(DEFAULT_CAPACITY, DEFAULT_LOADFACTOR);
    }

    public RobinHoodHashTable(int initCap) {
        this(initCap, DEFAULT_LOADFACTOR);
    }

    public RobinHoodHashTable(double lf) {
        this(DEFAULT_CAPACITY, lf);
    }

    public RobinHoodHashTable(int initCap, double lf) {
        clear(initCap);
        loadFactor = lf;
    }

    /**
     * calculates the offset of an entry as it adds it in
     */
    @Override
    public V put(K key, V value) {
        Entry temp = new Entry(key, value);
        int idx = hash(key);
        while(table[idx] != null) {
            if ( temp.offset > table[idx].offset) {
                // current offset is more than one we are looking at
                // the entries swap places, temp is now the entry we were looking at
                // increment the offset of the thing we are now searching with and idx
                Entry garbage = table[idx];
                table[idx] = temp;
                temp = garbage;
                idx = increment(idx);
                temp.offset ++;
            } else if (temp.offset == table[idx].offset) {
                // current offset is the same as we are looking at
                // check if the keys are the same, if they are swap the values
                // if not, increment idx & offset and move on
                if (table[idx].key.equals(key)) {
                    // found a duplicate. Update the value and return the original
                    V oldVal = table[idx].value;
                    table[idx].value = value;
                    return oldVal;
                } else {
                    idx = increment(idx);
                    temp.offset ++;
                }
            } else {
                // current offset is less than what we are looking at
                // we increment idx & offset and continue
                idx = increment(idx);
                temp.offset ++;
            }
        }
        // we have reach the idx where null is, put new/moved entree here
        table[idx] = temp;
        size++;
        // If we exceed the loadfactor, double the size.
        if (size >= loadFactor * table.length) {
            rehash(table.length * 2);
        }
        return null;
    }

    /**
     * utilize the offsets for faster removal
     */
    @Override
    public V remove(K key) {
        int idx = hash(key);
        int offset = 0;
        while (table[idx] != null) {
            if (offset > table[idx].offset) {
                // current offset less than what key's offset would be, quit out
                return null;
            } else if (offset == table[idx].offset) {
                // current offset is same as key's offset would be, compare them
                if (table[idx].key.equals(key)) {
                    // found out value, remove it and start moving things left
                    V itemVal = table[idx].value;
                    table[idx] = null;
                    size--;
                    idx = increment(idx);
                    // idx is the 'next' entry and tempIDX is the null value from the remove
                    while (table[idx] != null && table[idx].offset != 0) {
                        // while we have not reached null and offset is not 0
                        // move entry to the left and the null to the right
                        int tempidx = decrement(idx);
                        table[tempidx] = table[idx];
                        table[tempidx].offset = table[idx].offset - 1;
                        table[idx] = null;
                        idx = increment(idx);
                    }
                    return itemVal;
                } else {
                    // values were not the same, keep going
                    offset ++;
                    idx = increment(idx);
                }
            } else {
                // current offset is more than key's would be, keep going
                offset ++;
                idx = increment(idx);
            }
        }
        return null;
    }

    /**
     * utilize the offsets to be able to quickly quit out or quickly find
     */
    @Override
    public V get(K key) {
        int offset = 0;
        int idx = hash(key);
        while (table[idx] != null) {
            if (offset > table[idx].offset) {
                // current offset more than offset we are looking at, quit out
                return null;
            } else if (offset == table[idx].offset) {
                // current offset is same as key's offset would be, compare them
                // if they are the same, return the value, if not, keep going
                if (table[idx].key.equals(key)) {
                    return table[idx].value;
                } else {
                    offset ++;
                    idx = increment(idx);
                }
            } else {
                // current offset is less than the one we are looking at, keep going
                offset ++;
                idx = increment(idx);
            }
        }
        return null;
    }


    /**
     * counts up the amount of times the while loops is run during a call to get.
     * (allows for clear testing)
     */
    public int getProbed(K key) {
        int count = 0;
        int offset = 0;
        int idx = hash(key);
        while (table[idx] != null) {
            count ++;
            if (offset > table[idx].offset) {
                // current offset more than offset we are looking at, quit out
                return count;
            } else if (offset == table[idx].offset) {
                // current offset is same as key's offset would be, compare them
                // if they are the same, return the value, if not, keep going
                if (table[idx].key.equals(key)) {
                    return count;
                } else {
                    offset ++;
                    idx = increment(idx);
                }
            } else {
                // current offset less than offset we are looking at, keep going
                offset ++;
                idx = increment(idx);
            }
        }
        return count;
    }


    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        clear(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    private void clear(int cap) {
        table = (Entry[]) Array.newInstance(Entry.class, cap);
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size ==0;
    }

    @Override
    public int size() {
        return size;
    }


    private int hash(K key) {
        return Math.abs(key.hashCode() % table.length);
    }


    private int increment(int idx) {
        idx++;
        return idx == table.length ? 0 : idx;
    }

    private int decrement(int idx) {
        idx--;
        return idx == -1 ? table.length-1 : idx;
    }


    private void rehash(int newCap) {
        // make a new table of the new size, then iterate over the old
        // table and reinsert each entry.
        var oldTable = table;
        clear(newCap);
        for (var e : oldTable) {
            // skip nulls
            if (e != null) {
                this.put(e.key, e.value);
            }
        }
    }

    public void printStats() {
        System.out.println("RobinHood:");
        System.out.println("Size: " + size);
        System.out.println("Capacity: " + table.length);
    }


    /**
     * An entry in our table. Note that we'll use an entry with key==null to
     * indicate a deleted entry. These entry's store an offset as well.
     */
    private class Entry {
        K key;
        V value;
        int offset;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
            offset = 0;
        }
    }
    
}
