package prr.exceptions;

public class InvalidTerminalException extends Exception {
  
    /** Class serial number. */
    private static final long serialVersionUID = 201109020943L;
  
    /** The agent's key. */
    private final String _key;
  
    /** @param key */
    public InvalidTerminalException(String key) {
      _key = key;
    }
  
    /** @return the key */
    public String getKey() {
      return _key;
    }
    
  }
  
