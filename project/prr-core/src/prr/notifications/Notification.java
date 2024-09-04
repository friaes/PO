package prr.notifications;

import java.io.Serializable;

public class Notification implements Serializable {

	private static final long serialVersionUID = 202208091753L;

    private String _message;
    private String _terminalKey;

    public Notification(String message, String terminalKey){
        _message = message;
        _terminalKey = terminalKey;
    }

    public String getTerminalKey() {
        return _terminalKey;
    }

    public String getMessage() {
        return _message;
    }

    
}
