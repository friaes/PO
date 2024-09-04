package prr.exceptions;

public class UnknownTerminalException extends Exception{

    /** The agent's key. */
	private final String _key;

	/** @param key */
	public UnknownTerminalException(String key) {
		_key = key;
	}

	/** @return the key */
	public String getKey() {
		return _key;
	}
	
    
}
