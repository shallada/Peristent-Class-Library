package test.persistance.collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import persistance.collections.SizedLinkedList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

/**
 * SizedLinkedList Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>May 5, 2015</pre>
 */
public class SizedLinkedListTest {

    SizedLinkedList<Integer> sizedLinkedList = new SizedLinkedList<>(7);
    ArrayList<Integer> items = new ArrayList<Integer>() {
        {
            add(16);
            add(15);
            add(14);
            add(13);
        }
    };

    @Before
    public void before() throws Exception {
        sizedLinkedList.add(0);
        sizedLinkedList.add(1);
        sizedLinkedList.add(2);
        sizedLinkedList.add(3);
        sizedLinkedList.add(4);
        sizedLinkedList.add(5);
        sizedLinkedList.add(6);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: add(E e)
     */
    @Test
    public void testAddE() throws Exception {
//TODO: Test goes here...
        System.out.println("\nAddE");
        System.out.println(sizedLinkedList);
        Assert.assertTrue("First element should be 0", sizedLinkedList.get(0) == 6);
        sizedLinkedList.add(3);
        Assert.assertTrue("First element should be 3", sizedLinkedList.get(0) == 3);
        System.out.println(sizedLinkedList);
    }

    /**
     * Method: remove(Object o)
     */
    @Test
    public void testRemoveO() throws Exception {
//TODO: Test goes here...

        System.out.println("\nRemove0");
        System.out.println(sizedLinkedList);

        Assert.assertTrue("This should be 5", sizedLinkedList.remove(3) == 3);

        System.out.println(sizedLinkedList);

    }

    /**
     * Method: containsAll(Collection<?> c)
     */
    @Test
    public void testContainsAll() throws Exception {
//TODO: Test goes here...
        System.out.println("\nContainsAll");

        System.out.println("Items: " + items);
        sizedLinkedList.addAll(items);

        Assert.assertTrue("These items are in there, in order. This should work", sizedLinkedList.containsAll(items));

        System.out.println(sizedLinkedList);
    }

    /**
     * Method: containsKey(long potentialKey)
     */
    @Test
    public void testContainsKey() throws Exception {
//TODO: Test goes here...
        System.out.println("\nContainsKey:");
        Assert.assertTrue(sizedLinkedList.containsKey(0));
        System.out.println(sizedLinkedList.getIndexSet());
    }

    /**
     * Method: addAll(Collection<? extends E> c)
     */
    @Test
    public void testAddAllC() throws Exception {
//TODO: Test goes here...

        System.out.println("AddAllC");
        Assert.assertFalse("We haven't added the items yet", sizedLinkedList.containsAll(items));
        System.out.println(sizedLinkedList);

        sizedLinkedList.addAll(items);

        Assert.assertTrue("Because we check in sequence, containsAll should be false", sizedLinkedList.containsAll(items));
        System.out.println(sizedLinkedList);
    }

    /**
     * Method: addAll(int index, Collection<? extends E> c)
     */
    @Test
    public void testAddAllForIndexC() throws Exception {
//TODO: Test goes here...
        System.out.println("AddAllAtIndex");
        Assert.assertFalse("We don't have items yet", sizedLinkedList.containsAll(items));

        sizedLinkedList.addAll(1, items);
        System.out.println(sizedLinkedList);

    }

    /**
     * Method: removeAll(Collection<?> c)
     */
    @Test(expected = NotImplementedException.class)
    public void testRemoveAll() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: retainAll(Collection<?> c)
     */
    @Test(expected = NotImplementedException.class)
    public void testRetainAll() throws Exception {
//TODO: Test goes here...
    }

    /**
     * Method: clear()
     */
    @Test
    public void testClear() throws Exception {
//TODO: Test goes here...
        System.out.println("\nTestClear");
        Assert.assertFalse(sizedLinkedList.size() > 0);
        System.out.println(sizedLinkedList);
        System.out.println("Clearing");
        Assert.assertTrue(sizedLinkedList.size() == 0);
        System.out.println(sizedLinkedList);
    }

    /**
     * Method: get(int index)
     */
    @Test
    public void testGet() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: set(int index, E element)
     */
    @Test
    public void testSet() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: add(int index, E element)
     */
    @Test
    public void testAddForIndexElement() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: remove(int index)
     */
    @Test
    public void testRemoveIndex() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: indexOf(Object o)
     */
    @Test
    public void testIndexOf() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: lastIndexOf(Object o)
     */
    @Test
    public void testLastIndexOf() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: listIterator()
     */
    @Test
    public void testListIterator() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: listIterator(int index)
     */
    @Test
    public void testListIteratorIndex() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: subList(int fromIndex, int toIndex)
     */
    @Test
    public void testSubList() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: size()
     */
    @Test
    public void testSize() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: isEmpty()
     */
    @Test
    public void testIsEmpty() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: contains(Object o)
     */
    @Test
    public void testContains() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: iterator()
     */
    @Test
    public void testIterator() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: toArray()
     */
    @Test
    public void testToArray() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: toArray(T[] a)
     */
    @Test
    public void testToArrayA() throws Exception {
//TODO: Test goes here... 
    }


} 
