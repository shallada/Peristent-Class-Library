package Design.src.persistent.data.proxy.commands;

import java.io.IOException;
import java.io.Serializable;

import persistent.collections.PersistentArray;

public interface Command extends Serializable
{
	void execute(PersistentArray persistentArray) throws IOException;
	boolean isReturnable();
}
