package prr.tariffplan;

import java.io.Serializable;

public class PlatPlan extends BasicTariff implements Serializable{

	private static final long serialVersionUID = 202208091753L;

    public long calculateText(long units){
        if (units < 50) return 0;
        return 4;
    }

    public long calculateVideo(long units){
        return 10 * units; 
    } 

    public long calculateVoice(long units){
        return 10 * units;
    }

    
    
}
