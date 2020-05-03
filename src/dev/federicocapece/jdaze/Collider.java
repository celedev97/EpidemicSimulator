package dev.federicocapece.jdaze;

public abstract class Collider {
    protected GameObject gameObject;

    public Collider(GameObject gameObject){
        this.gameObject = gameObject;
    }

}
