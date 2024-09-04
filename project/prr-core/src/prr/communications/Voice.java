package prr.communications;

import prr.terminals.Terminal;
import prr.visits.CommVisitor;


public class Voice extends Communication {


    public Voice(Terminal sender, Terminal receiver, long key) {
        super(sender, receiver, key);
    }

    public void accept(CommVisitor visitor, boolean last) {
        visitor.visitVoiceComm(this, last);
    }

    public long units(){
        return getUnits();
    }

    @Override
    public String toString() {
        return "VOICE";
    }
}
