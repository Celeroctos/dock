package Core;

import java.nio.ByteBuffer;

public interface Receivable {

    public ByteBuffer receive() throws Exception;
}
