package persistent.collections.Transactions;

public interface Operation
{
    void execute();
    void undo();
    void setNext(long nextRef);
}
