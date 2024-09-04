package prr.tariffplan;

public abstract class BasicTariff {

    public abstract long calculateText(long units);

    public abstract long calculateVideo(long units);
    
    public abstract long calculateVoice(long units);


}
