package persistent;

import java.nio.ByteBuffer;

public interface Persistable
{
    //Parameterless constructor are needed to instantiate an object, but the discussion is ongoing in class how to deal with generics

    /**
     * Creates a ByteBuffer the correct size to store the object (size is calculated from the getSize() method)
     * @return a new ByteBuffer the size of the object
     */
    default ByteBuffer allocate(){
        return ByteBuffer.allocate(getSize());
    }

    /**
     * Returns the number of bytes it will take to persist the object
     * @return the number of bytes it will take to persist the object
     */
    int getSize();

    /**
     * Loads the data in the buffer into the object
     * @param buffer the buffer containing the persisted data
     */
    void load(ByteBuffer buffer);

    /**
     * Writes the persisted bytes of the object into the buffer
     * @param buffer the buffer to be filled
     */
    void serialize(ByteBuffer buffer);
}
