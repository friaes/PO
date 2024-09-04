package prr.tariffplan;

import java.io.Serializable;

public class NormalPlan extends BasicTariff implements Serializable{

	private static final long serialVersionUID = 202208091753L;


    public long calculateText(long units){
        if (units < 50) return 10;
        else if(units >= 50 && units < 100) return 16;
        return 2 * units;
    }

    public long calculateVideo(long units){
        return 30 * units; 
    } 

    public long calculateVoice(long units){
        return 20 * units;
    }
    
}
