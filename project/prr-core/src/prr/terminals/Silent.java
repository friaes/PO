package prr.terminals;

public class Silent extends Terminal.State{
    public Silent(Terminal terminal){
        terminal.super();
    }

    public void toIdle(Terminal terminal){
        setState(new Idle(terminal));
        
    }
    public void toSilent(Terminal terminal){}

    public void toBusy(Terminal terminal){
        setState(new Busy(terminal,this));
    }

    public void toOff(Terminal terminal){
        setState(new Off(terminal));
    }


    @Override
    public String toString(){
        return "SILENCE";
    }
}

