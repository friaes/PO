package prr.terminals;

public class Off extends Terminal.State {
    public Off(Terminal terminal){
        terminal.super();
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
        return "OFF";
    }
}
