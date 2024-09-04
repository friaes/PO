package prr.visits;

public interface Selector<Entity> {

    default boolean ok(Entity entity){
        return true;
    }
    
}
