package dev.federicocapece.jdaze;

public class Vector {
    float x, y;

    public static Vector ZERO () { return new Vector( 0,  0);}
    public static Vector UP   () { return new Vector( 0, -1);}
    public static Vector DOWN () { return new Vector( 0,  1);}
    public static Vector LEFT () { return new Vector(-1,  0);}
    public static Vector RIGHT() { return new Vector(+1,  0);}

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector vectorToCopy) {
        this.x = vectorToCopy.x;
        this.y = vectorToCopy.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector duplicate() {
        return new Vector(this.x, this.y);
    }


    public Vector(Vector vectorToCopy) {
        this.x = vectorToCopy.x;
        this.y = vectorToCopy.y;
    }

    public Vector sum(Vector vector){
        return new Vector(x + vector.x, y + vector.y);
    }

    public Vector sum(float x, float y) {
        return new Vector(this.x + x, this.y + y);
    }

    public void sumUpdate(Vector vector){
        x += vector.x;
        y += vector.y;
    }

    public void sumUpdate(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public Vector sub(Vector vector){
        return new Vector(x - vector.x, y - vector.y);
    }

    public Vector sub(float x, float y) {
        return new Vector(this.x - x, this.y - y);
    }

    public void subUpdate(Vector vector){
        x -= vector.x;
        y -= vector.y;
    }

    public void subUpdate(float x, float y) {
        this.x -= x;
        this.y -= y;
    }

    public Vector multiply(float multiplier){
        return new Vector(this.x * multiplier, this.y * multiplier);
    }

    public void multiplyUpdate(float multiplier){
        this.x *= multiplier;
        this.y *= multiplier;
    }

    public Vector divide(Vector vector){
        return new Vector(x / vector.x, y / vector.y);
    }

    public Vector divide(float x, float y) {
        return new Vector(this.x / x, this.y / y);
    }

    public void divideUpdate(Vector vector){
        x /= vector.x;
        y /= vector.y;
    }

    public void divideUpdate(float x, float y) {
        this.x /= x;
        this.y /= y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != this.getClass()) return false;
        return this.x == ((Vector)obj).x && this.y == ((Vector)obj).y;
    }

    @Override
    public int hashCode() {
        return Float.valueOf(x).hashCode() + Float.valueOf(y).hashCode();
    }

    public float magnitude() {
        return (float)Math.sqrt(x*x+y*y);
    }
}
