package test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import dictionary.Dog;
import dictionary.DogFactory;
import dictionary.IntFactory;
import dictionary.PersistentArray;
import dictionary.PersistentDictionary;
import dictionary.PersistentKVP;

public class TestPersistentDictionary {
	private PersistentArray pa;
	private DogFactory dg;
	IntFactory itf;

	@Test
	public void testPersistentDictionaryPersistentArrayPersistentFactoryOfKPersistentFactoryOfVLong() throws IOException {
		dg = new DogFactory();
		itf = new IntFactory();
		
		int recordSize = dg.sizeInBytes() + itf.sizeInBytes() + PersistentDictionary.additionalSizeInBytes();
		int metaSize = PersistentDictionary.metadataSizeInBytes();
		
		//create PA with recordSize and metaSize
		pa = new InMemoryPersistentArray("test", recordSize, metaSize); 
		
		Map<Dog, Integer> dict = new PersistentDictionary<Dog, Integer>(pa, dg, itf, 10000);
		Assert.assertTrue(pa.getRecordCount() == 10000);
		Assert.assertTrue(dict.size() == 0);
		Assert.assertTrue(true);
		
		double dweight = 135.2211111345234581;
		int dage = 112;

		Dog key = new Dog(dweight, dage);
		Integer value = 1;
		
		dict.put(key, value);
		Integer valueBack = dict.get(key);
		
		Assert.assertTrue(value.intValue() == valueBack.intValue());
		valueBack = dict.get(key);
		valueBack = dict.get(key);
		valueBack = dict.get(key);
		valueBack = dict.get(key);
		Assert.assertTrue(value.equals(valueBack));
		Assert.assertTrue(value.intValue() != 5);
		
		Assert.assertTrue(dict.size() == 1);
		Map<Dog, Integer> dict2 = new PersistentDictionary<Dog, Integer>(pa, dg, itf);
		Assert.assertTrue(dict2.size() == 1);
		
		Dog key2 = new Dog(5, 5);
		Integer value2 = 5;
		dict.put(key2, value);
		dict.put(key2, value2);
		dict.put(key2, value);
		dict.put(key2, value2);
		dict.put(key2, value);
		dict.put(key2, value2);
		Assert.assertTrue(dict.size() == 2);
		Assert.assertTrue(dict2.size() == 1);
		dict2.put(key2, value2);
		Assert.assertTrue(dict.size() == 2);
		
		valueBack = dict2.get(key);
		Assert.assertTrue(value.intValue() == valueBack.intValue());
		valueBack = dict2.get(key);
		valueBack = dict2.get(key);
		valueBack = dict2.get(key);
		valueBack = dict2.get(key);
		Assert.assertTrue(value.equals(valueBack));
		Assert.assertTrue(value.intValue() != 5);
		
		Assert.assertTrue(dict2.get(key).equals(dict.get(key)));
		Assert.assertTrue(dict2.get(key2).equals(dict.get(key2)));
		Assert.assertTrue(!dict2.get(key).equals(dict.get(key2)));
		Assert.assertTrue(!dict2.get(key2).equals(dict.get(key)));
		
		for(PersistentKVP<Dog, Integer> kvp : (Iterable<PersistentKVP<Dog, Integer>>)dict) {
//			System.out.println(kvp.getValue());
		}
	}

	@Test
	public void testPersistentDictionaryPersistentArrayPersistentFactoryOfKPersistentFactoryOfV() throws IOException {
		dg = new DogFactory();
		itf = new IntFactory();
		
		int recordSize = dg.sizeInBytes() + itf.sizeInBytes() + PersistentDictionary.additionalSizeInBytes();
		int metaSize = PersistentDictionary.metadataSizeInBytes();
		
		//create PA with recordSize and metaSize
		pa = new InMemoryPersistentArray("test", recordSize, metaSize); 
		Map<Dog, Integer> dict = new PersistentDictionary<Dog, Integer>(pa, dg, itf);
	}

	@Test
	public void testRemove() throws IOException {
		dg = new DogFactory();
		itf = new IntFactory();
		
		int recordSize = dg.sizeInBytes() + itf.sizeInBytes() + PersistentDictionary.additionalSizeInBytes();
		int metaSize = PersistentDictionary.metadataSizeInBytes();
		
		//create PA with recordSize and metaSize
		pa = new InMemoryPersistentArray("test", recordSize, metaSize); 
		Map<Dog, Integer> dict = new PersistentDictionary<Dog, Integer>(pa, dg, itf, 100);
		Map<Dog, Integer> realDictionary = new HashMap<Dog, Integer>();
		for(int i = 0; i < 1000; i++) {
			Dog d = new Dog(i, i);
			Integer in = i;
			realDictionary.put(d, in);
			
			Assert.assertFalse(dict.containsKey(d));
			dict.put(d, in);
			Assert.assertTrue(dict.containsKey(d));
			Assert.assertTrue(dict.size() == i + 1);
		}
		
		int count = 1;
		for(PersistentKVP<Dog, Integer> kvp : (Iterable<PersistentKVP<Dog, Integer>>)dict) {
			Assert.assertTrue(realDictionary.get(kvp.getKey()).equals(kvp.getValue()));
			count++;
		}
		
		Assert.assertTrue(count == 1000);
		for(Dog d : realDictionary.keySet()) {
			Assert.assertTrue(dict.get(d).equals(realDictionary.get(d)));
		}
		
		for(Dog d : realDictionary.keySet()) {
			Assert.assertTrue(dict.containsKey(d));
			Integer retVal = dict.remove(d);
			Assert.assertNotNull(retVal);
			Assert.assertFalse(dict.containsKey(d));
			Assert.assertEquals(--count, dict.size());
			retVal = dict.remove(d);
			Assert.assertNull(retVal);
		}
		
		for(PersistentKVP<Dog, Integer> kvp : (Iterable<PersistentKVP<Dog, Integer>>)dict) {
			Assert.fail();
		}
		
		for(int i = 0; i < 1000; i++) {
			Dog d = new Dog(i, i);
			Integer in = i;
			realDictionary.put(d, in);
			dict.put(d, in);
			Assert.assertTrue(dict.size() == i + 1);
		}
	}

//	@Test
//	public void testContainsKey() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSize() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEmpty() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAdditionalSizeInBytes() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMetadataSizeInBytes() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIterator() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testPutAll() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testClear() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testKeySet() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testValues() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContainsValue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEntrySet() {
//		fail("Not yet implemented");
//	}

}
