package persistent.data;

        import java.io.IOException;
        import java.nio.ByteBuffer;

public interface PData {
    void close() throws IOException;

    int read(ByteBuffer buffer);

    void seek(long position);

    void write(ByteBuffer buffer);

    ByteBuffer getMetadata() throws IOException;

    void persistMetadata() throws IOException;
}
