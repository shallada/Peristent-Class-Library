package persistent.data;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MdFileTemp {

    public static void create(String path, int mdSize) {}
    public static boolean exists(String path){ return true; }
    public static MdFileTemp open(String path) { return null; }

    public void close() throws IOException {}
    public int read(ByteBuffer buffer) {return 0;}
    public void seek(long position) {}
    public void write(ByteBuffer buffer) {}
    public ByteBuffer getMetadata() throws IOException{return ByteBuffer.allocate(1024);}
    public void persistMetadata() throws IOException {}
}
