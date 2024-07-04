import java.awt.*;
import java.util.ArrayList;

public class Player {
    private int x, y, width, height;
    private int speedY;
    private int jumpForce;
    private boolean jumping;
    private final int GRAVITY = 1;
    private final int MAX_SPEEDY = 11;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        width = 50;
        height = 50;
        speedY = 0;
        jumpForce = -15; // Normale Sprungkraft
        jumping = false;
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        speedY = 0;
        jumping = false;
    }

    public void jump() {
        if (!jumping) {
            speedY = jumpForce;
            jumping = true;
        }
    }

    public void update(ArrayList<Platform> platforms) {
        if (speedY < MAX_SPEEDY) {
            speedY += GRAVITY;
        }

        y += speedY;

        for (Platform platform : platforms) {
            if (getBounds().intersects(platform.getBounds())) {
                speedY = 0;
                y = platform.getY() - height;
                jumping = false;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSpeed(int speed) {
        this.speedY = speed;
    }

    public void setJumpForce(int jumpForce) {
        this.jumpForce = jumpForce;
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
}
