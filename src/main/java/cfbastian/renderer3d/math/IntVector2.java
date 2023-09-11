package cfbastian.renderer3d.math;

public class IntVector2 {
    public int x, y;

    public IntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int[] toArray()
    {
        return new int[]{x, y};
    }
}
