package prr.app.visitors;

import prr.visits.CommVisitor;
import prr.communications.*;

public class RenderComms implements CommVisitor{

    private String _rendering = "";

    private String renderCommFields(Communication communication){
        return communication.getKey() + "|" + communication.getSenderKey() + "|" +
        communication.getReceiverKey() +  "|" + communication.getUnits() + "|" +
        communication.getPrice() + "|" + communication.getStatusString();
        }

    
    @Override
    public void visitTextComm(Text comm, boolean last){
        _rendering += Message.typeText() + "|" + renderCommFields(comm)
         + (last ? "" : "\n");
         }

    @Override
    public void visitVoiceComm(Voice comm, boolean last){
        _rendering += Message.typeVoice() + "|" + renderCommFields(comm)
         + (last ? "" : "\n");
        }

    @Override
    public void visitVideoComm(Video comm, boolean last){
        _rendering += Message.typeVideo() + "|" + renderCommFields(comm)
            + (last ? "" : "\n");
        }

    @Override
    public String toString(){
        return _rendering;
    }
    
}
