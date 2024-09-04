package prr.communications;

import prr.terminals.Terminal;
import prr.visits.CommVisitor;


public class Video extends Communication {

    public Video(Terminal sender, Terminal receiver, long key) {
        super(sender, receiver, key);
    }

    public void accept(CommVisitor visitor, boolean last) {
        visitor.visitVideoComm(this, last);
    }
    
    public long units(){
        return getUnits();
    }

    @Override
    public String toString() {
        return "VIDEO";
    }

}

