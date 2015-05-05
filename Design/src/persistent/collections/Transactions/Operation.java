package persistent.collections.Transactions;

import java.io.IOException;

public interface Operation
{
    void execute() throws IOException;
    void undo() throws IOException, RollbackInterruptedException;
    void setNext(long nextRef);
	long getNext();
}
