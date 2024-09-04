package prr.clients;

import prr.tariffplan.NormalPlan;

public class Normal extends Client.Level {
    public Normal(Client client){
        client.super(new NormalPlan());
    }

    public void resetLevel(Client client){}

    public void upGrade(Client client){
            setLevel(new Gold(client));
    }

    public void downGrade(Client client){}


    @Override
        public String toString(){
            return "NORMAL";
        }

}
