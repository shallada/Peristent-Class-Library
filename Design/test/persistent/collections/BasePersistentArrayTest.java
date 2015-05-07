package persistent.collections;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class BasePersistentArrayTest {

    int DEFAULT_TEST_LOOP_SIZE = 10;
    int recordSize = Integer.SIZE;
    int mdSize = 100;

    @Test
    public void deletion() {
        String pathway = "deletion.mml";
        try {
            BasePersistentArray.create(pathway, mdSize, recordSize);
            BasePersistentArray test = BasePersistentArray.open(pathway);
            for (int i = 0; i < 5; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(100);
                whatever.flip();
                test.put(test.allocate(), whatever);
            }
            test.delete(3);
            test.delete(0);

            int intAt3 = test.get(3).getInt();
            assertNotEquals(intAt3, 100);
        } catch (IOException e) {
            fail("Unsuccesful");
        }
    }

    @Test
    public void deletionAndAdding() {
        String pathway = "deletionAndAdding.mml";

        try {
            BasePersistentArray.create(pathway, mdSize, recordSize);
            BasePersistentArray test = BasePersistentArray.open(pathway);
            for (int i = 0; i < DEFAULT_TEST_LOOP_SIZE; i++) {
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
            newBuffer.flip();

            int intAt4 = test.get(4).getInt();
            int intInBuffer = newBuffer.getInt();
            assertEquals(intAt4, intInBuffer);
        } catch (IOException e) {
            fail("Unsuccesful");
        }
    }

    @Test
    public void deletingAlreadyDeleted() {
        String pathway = "alreadyDeleted.mml";
        try {
            BasePersistentArray.create(pathway, mdSize, recordSize);
            BasePersistentArray test = BasePersistentArray.open(pathway);
            for (int i = 0; i < DEFAULT_TEST_LOOP_SIZE; i++) {
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
            buffer.flip();
            assertEquals(test.get(4).getInt(), buffer.getInt());
        } catch (IOException e) {
            fail("Unsuccesful");
        }
    }
}