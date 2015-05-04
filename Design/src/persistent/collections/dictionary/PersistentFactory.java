package dictionary;

import java.nio.ByteBuffer;

public interface PersistentFactory<T> {
	static int BITS_PER_BYTE = 8;

	T fromBuffer(ByteBuffer data);
	void toBuffer(ByteBuffer buffer, T obj);
	int sizeInBytes();
}
