
public interface Parser {

    /**
     * Override that method to parse received data
     * @param bytes - Bytes to parse
     * @throws Exception
     */
    public void parse(byte[] bytes) throws Exception;
}
