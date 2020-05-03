package dev.federicocapece.jdaze;

import jdk.jshell.spi.ExecutionControl;

public class CircleCollider extends Collider {
    private float ray;

    public CircleCollider(GameObject gameObject, float ray) {
        super(gameObject);
        this.ray = ray;
    }

    public boolean checkCollision(Collider collider) throws ExecutionControl.NotImplementedException {
        if(collider.getClass() == this.getClass()){
            CircleCollider circle2 = (CircleCollider) collider;
            return (circle2.ray + ray)< this.gameObject.position.sub(collider.gameObject.position).magnitude();
        }
        //this get thrown if the other collider is not a recognized type
        throw new ExecutionControl.NotImplementedException("Circle collider doesn't have a way to check intersection with:" + collider.getClass());
    }
}
