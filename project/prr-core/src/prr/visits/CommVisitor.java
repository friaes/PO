package prr.visits;

import prr.communications.*;

public interface CommVisitor {

    public void visitTextComm(Text comm, boolean last);
    
    public void visitVoiceComm(Voice comm, boolean last);

    public void visitVideoComm(Video comm, boolean last);

}
