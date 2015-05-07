package persistent.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;

public class InMemoryPData implements PData {
    private ByteBuffer data;
    private ByteBuffer metadata;
    private String path;
    private int mdSize;

    private static Map<String, InMemoryPData> created = new HashMap<>();
    private boolean HasAnOpenCopy = false;

    private InMemoryPData(String path, int mdSize) {
        this.path = path;
        this.mdSize = mdSize;

        int testFileSize = 1024 * 1024 * 5;//
        data = ByteBuffer.allocate(testFileSize);
        metadata = ByteBuffer.allocate(mdSize);
    }

    public static void create(String path, int mdSize) throws IOException {
        if (exists(path))
            throw new FileAlreadyExistsException(path);

        created.put(path, new InMemoryPData(path, mdSize));
    }

    public static boolean exists(String path) {
        return created.containsKey(path);
    }

    public static InMemoryPData open(String path) throws IOException {
        if (!exists(path))
            throw new FileNotFoundException(path);

        InMemoryPData pdata = created.get(path);
        if (pdata.HasAnOpenCopy)
            throw new IOException("Concurrent access.");

        pdata.HasAnOpenCopy = true;
        return pdata;
    }

    @Override
    public void close() throws IOException {
        HasAnOpenCopy = false;
    }

    @Override
    public int read(ByteBuffer buffer) {
        int readBytes = buffer.remaining();
        ByteBuffer temp = data.duplicate();
        temp.limit(temp.position() + readBytes);
        buffer.put(temp);
        return readBytes;
    }

    @Override
    public void seek(long position) {
        int positionAsInt = (int) position;
        if (positionAsInt != position)
            throw new UnsupportedOperationException("We cannot seek that far, bad design");

        data.position(positionAsInt);
    }

    @Override
    public void write(ByteBuffer buffer) {
        data.put(buffer);
    }

    @Override
    public ByteBuffer getMetadata() throws IOException {
        metadata.position(0);
        return metadata.slice();
    }

    @Override
    public void persistMetadata() throws IOException {
        metadata.position(0);
    }
}
