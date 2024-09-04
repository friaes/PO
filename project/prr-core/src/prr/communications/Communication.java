package prr.communications;

import prr.visits.CommVisitor;

import prr.terminals.*;
import java.io.Serializable;

public abstract class Communication implements Serializable{

    private static final long serialVersionUID = 202208091753L;

    private boolean _onGoing = true;
    private boolean _paid = false;
    private long _key; 

    private Terminal _sender;
    private Terminal _receiver;

    private long _units = 0;
    private long _price = 0;

    public Communication(Terminal sender, Terminal receiver, long key){
        _key = key;
        _sender = sender;
        _receiver = receiver;
    }

    public abstract long units();

    public abstract String toString();

    public abstract void accept(CommVisitor visitor, boolean last);
    
    public long getKey() {
        return _key;
    }

    public Terminal getSender(){
        return _sender;
    }

    public Terminal getReceiver(){
        return _receiver;
    }

    public String getSenderKey(){
        return _sender.getKey();
    }

    public String getReceiverKey(){
        return _receiver.getKey();
    }

    public boolean getStatus(){
        return _onGoing;
    }

    public String getStatusString(){
        if(_onGoing) return "ONGOING";
        return "FINISHED";
    }

    public long getUnits() {
        return _units;
    }

    public long getPrice() {
        return _price;
    }

    public boolean paidComm(){
        return _paid; 
    }

    public boolean isOnGoing(){
        return _onGoing;
    }


    public void endComm(){
        _onGoing = false;
    }

    public void setUnits(long units) {
        _units = units;
    }

    public void setPrice(long price) {
        _price = price;
    }

    public void payComm(){
        _paid = true;
    }

}

