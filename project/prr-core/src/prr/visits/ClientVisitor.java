package prr.visits;

import prr.clients.Client;

public  interface ClientVisitor {
    

    void visitClient(Client client, boolean last);

    
}
