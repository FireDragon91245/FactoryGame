package GameUtils;

import org.jetbrains.annotations.Contract;

public class Vec2i {
    public int x;
    public int y;

    public Vec2i() {
        y = 0;
        x = 0;
    }

    public Vec2i(int x, int y) {
        this.y = y;
        this.x = x;
    }

    public double dist(Vec2i point2){
        return Math.sqrt((point2.y - y) * (point2.y - y) + (point2.x - x) * (point2.x - x));
    }

    public boolean compareVec(Vec2i vec2){
        return x == vec2.x && y == vec2.y;
    }

    @Override
    public String toString() {
        return String.format("[x: %s, y: %s]", x, y);
    }

    @Override
    @Contract("null -> false")
    public boolean equals(Object obj) {
        if(obj.getClass() != Vec2i.class){
            return false;
        }
        return x == ((Vec2i) obj).x && y == ((Vec2i) obj).y;
    }

    public Vec2i add(int x, int y) {
        return new Vec2i(this.x + x, this.y + y);
    }

    public Vec2i add(Vec2i pos2) {
        return new Vec2i(this.x + pos2.x, this.y + pos2.y);
    }
}
