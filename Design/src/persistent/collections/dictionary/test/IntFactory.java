package dictionary;

import java.nio.ByteBuffer;

public class IntFactory implements PersistentFactory<Integer> {

	@Override
	public Integer fromBuffer(ByteBuffer data) {
		return new Integer(data.getInt());
	}

	@Override
	public void toBuffer(ByteBuffer buffer, Integer obj) {
		buffer.putInt(obj);
	}

	@Override
	public int sizeInBytes() {
		return Integer.SIZE / BITS_PER_BYTE;
	}

}
