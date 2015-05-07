package test;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import dictionary.Dog;
import dictionary.DogFactory;
import dictionary.IntFactory;
import dictionary.PersistentKVP;

public class TestPersistentKVP {

	@Test
	public void testPersistentKVPKVPersistentFactoryOfKPersistentFactoryOfV() {
		double dweight = 135.2211111345234581;
		int dage = 112;

		Dog key = new Dog(dweight, dage);
		DogFactory keyf = new DogFactory();
		Integer value = 1;
		IntFactory valuef = new IntFactory();
		PersistentKVP<Dog, Integer> kvp = new PersistentKVP<Dog, Integer>(key, value, keyf, valuef);
		
		
		Assert.assertTrue(kvp.getKey() == key);
		Assert.assertTrue(kvp.getValue() == value);
		
		ByteBuffer buff = kvp.toBytes();
		PersistentKVP<Dog, Integer> kvp2 = new PersistentKVP<Dog, Integer>(buff, keyf, valuef);

		Assert.assertTrue(kvp2.getKey().weight == dweight);
		Assert.assertTrue(kvp2.getKey().age == dage);
		Assert.assertTrue(kvp2.getValue().equals(value));
		
		kvp2.getKey().weight -=1;
		Assert.assertFalse(kvp2.getKey() == key);
		Assert.assertFalse(kvp2.getKey().weight == dweight);
		Assert.assertTrue(kvp2.getValue().equals(value));
	}

}
