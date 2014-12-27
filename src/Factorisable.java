
public interface Factorisable <T> {

	/**
	 * Override that method to create instance
	 * of some class
	 * @param name - Key
	 * @return - Constructed element
	 */
	public T create(String name) throws Exception;
}
