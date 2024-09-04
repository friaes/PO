package prr.communications;

import prr.terminals.Terminal;
import prr.visits.CommVisitor;

public class Text extends Communication {

    private String _textMessage;

    public Text(Terminal sender, Terminal receiver, String text, long key) {
        super(sender, receiver, key);
        _textMessage = text;
    }

    public void accept(CommVisitor visitor, boolean last) {
        visitor.visitTextComm(this, last);
    }

    public long units(){
        long units = 0;
        for(int i = 0; i < _textMessage.length(); i++) {    
            if(_textMessage.charAt(i) != ' ')    
                units++;
        }
        setUnits(units); 
        return units;       
    }

    @Override
    public String toString() {
        return "TEXT";
    }
}
