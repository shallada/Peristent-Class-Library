package persistent.collctions.graph;

import java.nio.ByteBuffer;


public interface PersistentFactory<E> {
	
	E deserialize(ByteBuffer buffer);
	void serialize(ByteBuffer buffer, E value);
	int getSize();

}
