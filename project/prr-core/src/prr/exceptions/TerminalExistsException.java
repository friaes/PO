package prr.exceptions;

public class TerminalExistsException extends Exception{

    private static final long serialVersionUID = 202208091753L;

    private final String _key;

  /** @param key */
  public TerminalExistsException(String key) {
    _key = key;
  }

  /** @return the key */
  public String getKey() {
    return _key;
  }

}
