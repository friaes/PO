package prr.terminals;

import prr.clients.Client;
import prr.visits.TerminalVisitor;

public class FancyTerminal extends Terminal{
    
	private static final long serialVersionUID = 202208091753L;

    public FancyTerminal(String key, Client client, String state){
        super(key, client, state);
    }


    @Override
    public void accept(TerminalVisitor visitor, boolean last) {
        visitor.visitFancyTerminal(this, last);        
    }    

    @Override
    public String toString(){
        return "FANCY";
    }

}
