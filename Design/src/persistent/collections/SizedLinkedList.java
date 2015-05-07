package persistance.collections;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * A LinkedList implementation that will hold a set size.
 * Inserts above the capacity will throw
 * Created by stephen on 4/20/15.
 */
public class SizedLinkedList <E> implements List<E>, Cloneable {
    private final int CAPACITY;

    private final HashMap<Long, E> indexToItem;
    private LinkedList<E> wrappedLL;

    public SizedLinkedList( int capacity ) {
        // Code goes here
        this.CAPACITY = capacity;
        this.wrappedLL = new LinkedList<>();
        indexToItem = new HashMap<>();
    }

    public SizedLinkedList( Collection<E> collection ) {

        this.CAPACITY = collection.size();
        this.wrappedLL = new LinkedList<>();

        indexToItem = new HashMap<>();

        long i = 0;
        for ( E e : collection ) {
            indexToItem.put(i, e);
            i++;
        }
    }

    @Override
    public boolean add( E e ) {
        // Add to front of the linkedList
        wrappedLL.addFirst(e);
        // remove tail of link list
        if ( wrappedLL.size() > CAPACITY ) wrappedLL.removeLast();
        // adjust the hashmap to reflect changes
        Collection<E> values = indexToItem.values();
        indexToItem.clear();

        // insert at the top
        indexToItem.put(0L, e);
        final long[] i = { 1 };
        for ( E value : values ) {
            indexToItem.put(i[0]++, value);
        }

        return true;
    }

    /**
     * Removes an item from the cache
     *
     * @param o object we want to rewmove
     * @return success of the removal
     */
    @Override
    public boolean remove( Object o ) {
        if ( wrappedLL.remove(o) ) {
            Map.Entry<Long, E> entryToRemove = null;
            // iter through the entrySet, assigning entryToRemove if it's key == o
            for ( Map.Entry<Long, E> longEEntry : indexToItem.entrySet() ) {
                if ( longEEntry.getKey() == o ) {
                    entryToRemove = longEEntry;
                    break;
                }
            }

            if ( entryToRemove != null ) {
                return indexToItem.remove(entryToRemove.getKey()) != null;
            }

//            return indexToItem.values().remove(o);
        }
        return false;
    }

    /**
     * Checks the collection's iterator against the backed list's iterator
     *
     * @param c
     * @return if all the elements are in order in this list
     */
    @Override
    public boolean containsAll( Collection<?> c ) {

        final boolean[] containsAll = { true };

        c.forEach(item -> {
            if (!contains(item))
                containsAll[0] = false;
        });

        return containsAll[0];
    }

    public boolean containsKey( long potentialKey ) {
        return indexToItem.containsKey(potentialKey);
    }

    @Override
    public boolean addAll( Collection<? extends E> c ) {
        c.forEach(this::add);
        return this.containsAll(c);
    }

    @Override
    public boolean addAll( int index, Collection<? extends E> c ) {
        wrappedLL.addAll(index, c);
        while (wrappedLL.size() > CAPACITY) wrappedLL.removeLast();
        return this.containsAll(c);
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        throw new NotImplementedException();
    }

    @Override
    public void clear() {
        indexToItem.clear();
        wrappedLL.clear();
    }

    /**
     * Promote the value at the index to the front of the linkedList
     *
     * @param index where the value is in the linkedList
     * @return desired value
     */
    @Override
    public E get( int index ) {

        if ( index > CAPACITY ) throw new IllegalArgumentException("index");

        // get index in the wrappedLL
        E e = wrappedLL.get(index);

        // and remove it from where it was
        wrappedLL.removeFirstOccurrence(e);
        ArrayList<E> values = new ArrayList<>(indexToItem.values());


        // add to the head
        wrappedLL.addFirst(e);

//        int indexOf = wrappedLL.indexOf(e);
        // make the hashmap a parallel mapping of the new index

        indexToItem.clear();

        indexToItem.put(0L, e);

        for ( long i = 1, length = values.size(); i < length; i++ )
            indexToItem.put(i, values.get((int) ( i - 1 )));

        return e;
    }

    @Override
    public E set( int index, E element ) {
        return wrappedLL.set(index, element);
    }

    @Override
    public void add( int index, E element ) {
        if ( index >= CAPACITY ) throw new IllegalArgumentException("index");
        wrappedLL.add(index, element);
        if (wrappedLL.size() > CAPACITY) wrappedLL.removeLast();
        indexToItem.clear();
        long i = 0;
        for ( E e : wrappedLL ) {
            indexToItem.put(i++, e);
        }
    }

    @Override
    public E remove( int index ) {
        E remove = wrappedLL.remove(index);
        // remove from the hashmap of indices to items
        indexToItem.remove(Long.valueOf(index));
        return remove;
    }

    @Override
    public int indexOf( Object o ) {
        if ( indexToItem.containsValue(o) ) {
            indexToItem.values().contains(o);
        }
        return -1;
    }

    @Override
    public int lastIndexOf( Object o ) {
        return indexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator( int index ) {
        return wrappedLL.listIterator(index);
    }

    @Override
    public List<E> subList( int fromIndex, int toIndex ) {
        return wrappedLL.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return wrappedLL.size();
    }

    @Override
    public boolean isEmpty() {
        return indexToItem.isEmpty();
    }

    @Override
    public boolean contains( Object o ) {
        return wrappedLL.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return wrappedLL.iterator();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[CAPACITY];
        Iterator<E> eIterator = iterator();
        E curr = iterator().next();

        for ( int i = 0; eIterator.hasNext(); curr = eIterator.next(), i++ ) {
            result[i] = curr;
        }

        return result;
    }

    @Override
    public <T> T[] toArray( T[] a ) {
        return wrappedLL.toArray(a);
    }

    public HashMap<Long, E> getIndexToItem() {
        return indexToItem;
    }

    public int getCAPACITY() {
        return CAPACITY;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for ( E e : wrappedLL ) {
            if ( !first ) builder.append(",");

            builder.append(e);
            first = false;
        }

        builder.append("]");

        return builder.toString();
    }

    /**
     *
     * @return the indices that make up the key set
     */
    public Set<Long> getIndexSet() {
        return indexToItem.keySet();
    }

    /**
     * Returns
     * @return the values that the keys map to
     */
    public Collection<E> getValues() {
        return indexToItem.values();
    }

    /**
     * Questions for the audience:
     *
     */
}

