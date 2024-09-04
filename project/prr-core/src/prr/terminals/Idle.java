package prr.terminals;

public class Idle extends Terminal.State {

    public Idle(Terminal terminal){
        terminal.super();
    }

    public void toIdle(Terminal terminal){        
    }

    public void toSilent(Terminal terminal){
        setState(new Silent(terminal));
    }
    public void toBusy(Terminal terminal){
        setState(new Busy(terminal,this));
    }

    public void toOff(Terminal terminal){
        setState(new Off(terminal));
    }


    @Override
    public String toString(){
        return "IDLE";
    }
}