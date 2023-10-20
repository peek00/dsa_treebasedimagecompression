public class QuadtreeNode {
    public int x, y, size;
    public int[] color;
    public QuadtreeNode[] children;

    // Constructor for leaf nodes with color
    public QuadtreeNode(int x, int y, int size, int[] color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        this.children = null;
    }

    // Constructor for non-leaf nodes with children
    public QuadtreeNode(int x, int y, int size, QuadtreeNode topLeft, QuadtreeNode topRight, QuadtreeNode bottomLeft, QuadtreeNode bottomRight) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = null;
        this.children = new QuadtreeNode[4];
        this.children[0] = topLeft;
        this.children[1] = topRight;
        this.children[2] = bottomLeft;
        this.children[3] = bottomRight;
    }
}
