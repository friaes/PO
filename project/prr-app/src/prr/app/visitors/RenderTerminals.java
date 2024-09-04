package prr.app.visitors;

import prr.visits.TerminalVisitor;
import prr.terminals.*;

public class RenderTerminals implements TerminalVisitor {

    private String _rendering = "";

    private String renderTerminalFields(Terminal terminal){
        return terminal.getKey() + "|" + terminal.getClientKey() + "|" +
        terminal.getStateString() +  "|" + terminal.getPayments() + "|" +
        terminal.getDebts() + terminal.toStringFriends();
        }

    
    @Override
    public void visitBasicTerminal(BasicTerminal terminal, boolean last){
        _rendering += Message.typeBasic() + "|" + renderTerminalFields(terminal)
         + (last ? "" : "\n");
         }

    @Override
    public void visitFancyTerminal(FancyTerminal terminal, boolean last){
        _rendering += Message.typeFancy() + "|" + renderTerminalFields(terminal)
         + (last ? "" : "\n");
        }

    @Override
    public String toString(){
        return _rendering;
    }

    
}
