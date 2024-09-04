package prr.clients;
import prr.tariffplan.PlatPlan;

public class Platinum extends Client.Level {



    public Platinum(Client client){
        client.super(new PlatPlan());
    }

    public void resetLevel(Client client){
        setLevel(new Normal(client));
    }

    public void downGrade(Client client){
        setLevel(new Gold(client));
    }   

    public void upGrade(Client client){}


    @Override
        public String toString(){
            return "PLATINUM";
        }

}
