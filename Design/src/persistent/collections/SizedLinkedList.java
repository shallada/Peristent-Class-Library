package persistance.collections;

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


    @Override
    public boolean add( E e ) {
        // Add to front of the linkedList
        wrappedLL.removeLast();
        // remove tail of link list
        wrappedLL.addFirst(e);
        // adjust the hashmap to reflect changes
        Collection<E> values = indexToItem.values();
        indexToItem.clear();

        // insert at the top
        indexToItem.put(0L, e);
        final long[] i = { 1 };
        values.forEach(x -> indexToItem.put(i[0]++, x));

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
                if (longEEntry.getKey() == o) {
                    entryToRemove = longEEntry;
                    break;
                }
            }

            if (entryToRemove != null) indexToItem.entrySet().remove(entryToRemove);

            return indexToItem.values().remove(o);
        }
        return false;
    }

    @Override
    public boolean containsAll( Collection<?> c ) {
        Iterator<?> cIter = c.iterator();
        Iterator<E> eIter = iterator();

        Object cCurr = cIter.next();
        E eCurr = eIter.next();

        for (; cIter.hasNext() && eIter.hasNext(); cCurr = cIter.next(), eCurr = eIter.next() ) {
            if ( cCurr != eCurr ) return false;
        }

        return true;
    }

    public boolean containsKey( long potentialKey ) {
        return indexToItem.containsKey(potentialKey);
    }

    @Override
    public boolean addAll( Collection<? extends E> c ) {
        return false;
    }

    @Override
    public boolean addAll( int index, Collection<? extends E> c ) {
        return false;
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        return false;
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        return false;
    }

    @Override
    public void clear() {
        indexToItem.clear();
        wrappedLL.clear();
    }

    /**
     * Promote the value at the index to the front of the linkedList
     * @param index where the value is in the linkedList
     * @return desired value
     */
    @Override
    public E get( int index ) {

        if (index > CAPACITY) throw new IllegalArgumentException("index");

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
            indexToItem.put(i, values.get((int) (i - 1)));

        return e;
    }

    @Override
    public E set( int index, E element ) {
        return wrappedLL.set(index, element);
    }

    @Override
    public void add( int index, E element ) {
        if (index >= CAPACITY) throw new IllegalArgumentException("index");
        wrappedLL.add(index, element);
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

    /**
     * Questions for the audience:
     *
     */
}

