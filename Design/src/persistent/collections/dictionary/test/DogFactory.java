package dictionary;

import java.nio.ByteBuffer;

public class DogFactory implements PersistentFactory<Dog> {

	@Override
	public Dog fromBuffer(ByteBuffer data) {
		Dog d = new Dog();
		d.weight = data.getDouble();
		d.age = data.getInt();
		return d;
	}

	@Override
	public void toBuffer(ByteBuffer buffer, Dog obj) {
		buffer.putDouble(obj.weight);
		buffer.putInt(obj.age);
	}

	@Override
	public int sizeInBytes() {
		return (Double.SIZE + Integer.SIZE) / BITS_PER_BYTE;
	}

}
