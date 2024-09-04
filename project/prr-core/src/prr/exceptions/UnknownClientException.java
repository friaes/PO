package prr.exceptions;

public class UnknownClientException extends Exception{



    private final String _key;

	/** @param key */
	public UnknownClientException(String key) {
		_key = key;
	}

	/** @return the key */
	public String getKey() {
		return _key;
	}
	

   
}
