package prr.clients;

import prr.tariffplan.GoldPlan;

public class Gold extends Client.Level {
    public Gold(Client client){
        client.super(new GoldPlan());
    }

    public void resetLevel(Client client){
        setLevel(new Normal(client));
    }

    public void downGrade(Client client){}

    public void upGrade(Client client){
        setLevel(new Platinum(client));
    }

    @Override
        public String toString(){
            return "GOLD";
        }



}
