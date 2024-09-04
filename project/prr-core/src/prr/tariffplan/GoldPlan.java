package prr.tariffplan;

import java.io.Serializable;

public class GoldPlan extends BasicTariff implements Serializable{

	private static final long serialVersionUID = 202208091753L;

    public long calculateText(long units){
        if (units < 100) return 10;
        return 2 * units;
    }

    public long calculateVideo(long units){
        return 20 * units; 
    } 

    public long calculateVoice(long units){
        return 10 * units;
    }
    
}
