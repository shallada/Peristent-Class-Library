package persistent.collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import persistent.data.BaseBlockFile;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class BasePersistentArrayTest {


    int recordSize = Integer.SIZE;
    int mdsize = 100;

    @Test
    public void deletion()
    {
        String pathway = "deletion.mml";
        try
        {
            BasePersistentArray.create(pathway, mdsize, recordSize);
            BasePersistentArray test = BasePersistentArray.open(pathway);
            for (int i = 0; i < 5; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(100);
                whatever.flip();
                test.put(test.allocate(), whatever);
            }
            test.delete(3);
            test.delete(0);
            assertNotEquals(test.get(3).getInt(), 100);
        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }
    }

    @Test
    public void deletionAndAdding()
    {
        String pathway = "deletionAndAdding.mml";

        try
        {
            BasePersistentArray.create(pathway, mdsize, recordSize);
            BasePersistentArray test = BasePersistentArray.open(pathway);
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(i);
                whatever.flip();
                test.put(test.allocate(), whatever);
            }

            ByteBuffer newBuffer = ByteBuffer.allocate(recordSize);
            newBuffer.putInt(405);
            newBuffer.flip();
            test.delete(4);
            test.put(test.allocate(), newBuffer);
            assertEquals(test.get(4).getInt(), newBuffer.getInt());
        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }
    }

    @Test
    public void deletingAlreadyDeleted()
    {
        String pathway = "alreadyDeleted.mml";
        try
        {
            BasePersistentArray.create(pathway, mdsize, recordSize);
            BasePersistentArray test = BasePersistentArray.open(pathway);
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(i);
                whatever.flip();
                test.put(test.allocate(), whatever);
            }

            ByteBuffer buffer = ByteBuffer.allocate(recordSize);
            buffer.putInt(500);
            buffer.flip();
            test.delete(4);
            test.delete(4);
            test.put(test.allocate(), buffer);
            assertEquals(test.get(4).getInt(), buffer.getInt());
        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }
    }
}