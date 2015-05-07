package persistent.collections.jbopa;
import java.io.IOException;


public class OutOfStorageException extends IOException {

	public OutOfStorageException(String message){
		super(message);
	}
}
