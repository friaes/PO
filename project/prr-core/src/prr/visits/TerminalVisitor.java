package prr.visits;

import prr.terminals.*;

public interface TerminalVisitor {

    void visitBasicTerminal(BasicTerminal terminal, boolean last);
    
    void visitFancyTerminal(FancyTerminal terminal, boolean last);
    
}
