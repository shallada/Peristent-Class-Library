package persistent.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class BaseBlockFileTest {


    int recordSize = Integer.SIZE;
    int mdsize = 100;

    @Test
    public void creationAndOpen()
    {
        String pathway = "creation.mml";

        try
        {
            BaseBlockFile.create(pathway, mdsize, recordSize);
        }
        catch (IOException e) { fail("Unsuccesful Creation"); }

        try
        {
            BaseBlockFile test = BaseBlockFile.open(pathway);
            test.close();
        }
        catch(IOException e) { fail("Unsuccesful Opening"); }
    }

    @Test
    public void adding()
    {
        String pathway = "simpleAdding.mml";

        try
        {
            BaseBlockFile.create(pathway, mdsize, recordSize);
            BaseBlockFile test = BaseBlockFile.open(pathway);
            for (int i = 0; i < 10; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(i);
                whatever.flip();
                test.put(test.allocate(), whatever);
            }
        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }

    }

    @Test
    public void counting()
    {
        String pathway = "recordCount.mml";

        try
        {
            BaseBlockFile.create(pathway, mdsize, recordSize);
            BaseBlockFile test = BaseBlockFile.open(pathway);
            for (int i = 0; i < 10; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(i);
                whatever.flip();
                test.put(test.allocate(), whatever);
                assertEquals(i + 1, test.getRecordCount());
            }



        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }
    }

    @Test
    public void getting()
    {
        String pathway = "getting.mml";
        try
        {
            BaseBlockFile.create(pathway, mdsize, recordSize);
            BaseBlockFile test = BaseBlockFile.open(pathway);
            for (int i = 0; i < 10; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(i);
                whatever.flip();
                test.put(test.allocate(), whatever);
                assertEquals(i, test.get(i).getInt());
            }
        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }

    }

    @Test
    public void gettingThatDoesntExist()
    {
        String pathway = "gettingDoesntExist.mml";
        try
        {
            BaseBlockFile.create(pathway, mdsize, recordSize);
            BaseBlockFile test = BaseBlockFile.open(pathway);
            for (int i = 0; i < 10; i++) {
                ByteBuffer whatever = ByteBuffer.allocate(recordSize);
                whatever.putInt(i);
                whatever.flip();
                test.put(test.allocate(), whatever);
            }
            test.get(40);
            fail("Should have thrown an exception");
        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }
        catch(IndexOutOfBoundsException e)
        {

        }
    }

    @Test
    public void allocation()
    {
        String pathway = "allocate.mml";
        try
        {
            BaseBlockFile.create(pathway, mdsize, recordSize);
            BaseBlockFile test = BaseBlockFile.open(pathway);

            // Lets go for 10, and not Integer.Max
            for (int i = 0; i < 10; i++) {
                assertEquals(test.allocate(), i);
            }
        }
        catch(IOException e)
        {
            fail("Unsuccesful");
        }
    }
}