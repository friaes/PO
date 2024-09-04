package prr.app.visitors;

import prr.clients.Client;
import prr.visits.ClientVisitor;

public class RenderClients implements ClientVisitor{
    
    private String _rendering = "";

    public String renderClient(Client client){
        return client.getKey() + "|" + client.getName() + "|" 
        + client.getTaxID() + "|" + client.getLevel() + "|" +
        client.NotificationsToString() + "|" + client.getTerminalsSize() +
         "|" + client.getPayments() + "|" 
        +  client.getDebts();
    }

    @Override
    public void visitClient(Client client, boolean last){
        _rendering += Message.typeClient() + "|" + renderClient(client)
        + (last ? "" : "\n");
    }

    @Override
    public String toString(){
        return _rendering;
    }
}
