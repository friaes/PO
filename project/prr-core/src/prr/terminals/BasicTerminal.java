package prr.terminals;

import prr.clients.Client;
import prr.visits.TerminalVisitor;

public class BasicTerminal extends Terminal{

	private static final long serialVersionUID = 202208091753L;

    public BasicTerminal(String key, Client client, String state){
        super(key, client, state);
    }


    @Override
    public void accept(TerminalVisitor visitor, boolean last) {
        visitor.visitBasicTerminal(this, last);        
    }

    @Override
    public String toString(){
        return "BASIC";
    }
}
