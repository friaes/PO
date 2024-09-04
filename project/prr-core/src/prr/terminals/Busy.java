package prr.terminals;

public class Busy extends Terminal.State {
    Terminal.State _originState;
    public Busy(Terminal terminal,Terminal.State originState){
        terminal.super();
        _originState = originState;
    }

    public void toIdle(Terminal terminal){
        setState(new Idle(terminal));
    }
    public void toSilent(Terminal terminal){
        setState(new Silent(terminal));
    }
    public void toBusy(Terminal terminal){}

    public void toOff(Terminal terminal){}

    @Override
    public String toString(){
        return "BUSY";
    }
}
