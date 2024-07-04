import java.awt.*;

public class PowerUp {
    private String type;
    private int x, y, width, height;
    private Color color;
    private int speedX;

    public PowerUp(String type, int x, int y, int width, int height, Color color) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        speedX = -1; // Geschwindigkeit mit der die PowerUps auf den Spieler zu fliegen
    }

    public void moveLeft() {
        x += speedX;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }

    public void setY(int y) {
        this.y = y;
    }
}
