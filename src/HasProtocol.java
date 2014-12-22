
public interface HasProtocol {

    /**
     * If your class implements some protocol features then implement
     * that method to return protocol name and register your class
     * in ProtocolCollection, for example receiver implements TCP/IP
     * protocol data transfer
     * @return - Name of your class's protocol, which it's implements
     */
    public String getProtocol();
}
