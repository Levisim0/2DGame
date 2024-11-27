import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private ArrayList<Platform> platforms;
    private ArrayList<PowerUp> powerUps;
    private boolean gameOver;
    private int score;
    private int highscore;
    private Random random;
    private int countdown;
    private boolean gameStarted;
    private boolean paused;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.CYAN);
        setFocusable(true);
        addKeyListener(this);

        player = new Player(400, 460);
        platforms = new ArrayList<>();
        powerUps = new ArrayList<>();
        random = new Random();
        score = 0;
        highscore = 0;
        countdown = 180;
        gameStarted = false;
        paused = false;

        initializeGame();
    }

    private void initializeGame() {
        player.reset(400, 460);
        platforms.clear();
        powerUps.clear();

        platforms.add(new Platform(300, 500, 200, 20));
        platforms.add(new Platform(550, 400, 200, 20));

        gameOver = false;
        score = 0;
        countdown = 180;

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(16, this);
        timer.start();
    }

    public void startGame() {
        initializeGame();
        gameStarted = false;
        paused = false;
        countdown = 180;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (countdown > 0) {
            countdown--;
        } else {
            gameStarted = true;
        }

        if (!paused && gameStarted && !gameOver) {
            updatePlatforms();
            updatePowerUps();
            player.update(platforms);

            checkCollision();
            checkPowerUpCollisions();

            score++;
        }
        repaint();
    }

    private void updatePlatforms() {
        Iterator<Platform> it = platforms.iterator();
        while (it.hasNext()) {
            Platform platform = it.next();
            platform.moveLeft();

            if (platform.getX() + platform.getWidth() < 0) {
                it.remove();
            }
        }

        if (platforms.size() < 5) {
            Platform lastPlatform = platforms.get(platforms.size() - 1);
            int maxGap = 150;
            int minGap = 100;
            int maxYChange = 100;
            int minY = 300;
            int maxY = 500;

            int gap = minGap + random.nextInt(maxGap - minGap);
            int width = 150 + random.nextInt(100);
            int newY = lastPlatform.getY() + random.nextInt(maxYChange * 2) - maxYChange;
            newY = Math.max(minY, Math.min(maxY, newY));

            int newX = lastPlatform.getX() + lastPlatform.getWidth() + gap;
            platforms.add(new Platform(newX, newY, width, 20));
        }
    }

    private void updatePowerUps() {
        Iterator<PowerUp> it = powerUps.iterator();
        while (it.hasNext()) {
            PowerUp powerUp = it.next();
            powerUp.moveLeft(); // Linksbewegung

            // Überprüfung, ob das PowerUp auf einer Plattform is
            boolean onPlatform = false;
            for (Platform platform : platforms) {
                if (powerUp.getBounds().intersects(platform.getBounds())) {
                    powerUp.setY(platform.getY() - powerUp.getHeight()); // Adjust position
                    onPlatform = true;
                    break;
                }
            }

            if (!onPlatform) {
                it.remove(); // Entfernung der PowerUps, wenn sie nicht auf der Plattform sind
            }
        }

        // Zufällige Erstellung neuer PowerUps
        if (random.nextInt(500) < 2) { // Einstellung der Spawn Rate
            int type = random.nextInt(3); // 0: Speed, 1: Super Sprung, 2: Double Score
            int y = 250 + random.nextInt(200);

            // Zufällige Erstellung neuer PowerUps auf den Plattformen
            for (Platform platform : platforms) {
                if (platform.getY() < y && y < platform.getY() + platform.getHeight()) {
                    PowerUp newPowerUp = null;
                    switch (type) {
                        case 0:
                            newPowerUp = new PowerUp("Speed", 800, platform.getY() - 20, 20, 20, Color.BLUE);
                            break;
                        case 1:
                            newPowerUp = new PowerUp("SuperJump", 800, platform.getY() - 20, 20, 20, Color.GREEN);
                            break;
                        case 2:
                            newPowerUp = new PowerUp("DoubleScore", 800, platform.getY() - 20, 20, 20, Color.YELLOW);
                            break;
                    }
                    if (newPowerUp != null) {
                        powerUps.add(newPowerUp);
                    }
                    break;
                }
            }
        }
    }


    private void checkCollision() {
        for (Platform platform : platforms) {
            if (player.getBounds().intersects(platform.getBounds())) {
                player.setY(platform.getY() - player.getHeight());
                return;
            }
        }
        if (player.getY() > getHeight()) {
            gameOver = true;
            if (score > highscore) {
                highscore = score;
            }
        }
    }

    private void checkPowerUpCollisions() {
        Iterator<PowerUp> it = powerUps.iterator();
        while (it.hasNext()) {
            PowerUp powerUp = it.next();
            if (player.getBounds().intersects(powerUp.getBounds())) {
                applyPowerUpEffect(powerUp);
                it.remove();
            }
        }
    }

    private void applyPowerUpEffect(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case "Speed":
                player.setSpeed(2); // Erhöhund des Spieler speed
                Timer speedTimer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        player.setSpeed(1); // Nach 5 Sekunden zurück auf Normal
                    }
                });
                speedTimer.setRepeats(false);
                speedTimer.start();
                break;
            case "SuperJump":
                player.setJumpForce(-20); // Einstellung der Sprungkraft
                Timer superJumpTimer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        player.setJumpForce(-15); // Nach 5 Sekunden zurück auf Normal
                    }
                });
                superJumpTimer.setRepeats(false);
                superJumpTimer.start();
                break;
            case "DoubleScore":
                score *= 2;
                Timer doubleScoreTimer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        score /= 2;
                    }
                });
                doubleScoreTimer.setRepeats(false);
                doubleScoreTimer.start();
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Game Over", getWidth() / 2 - 150, getHeight() / 2);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Score: " + score, getWidth() / 2 - 50, getHeight() / 2 + 50);
            g.drawString("Highscore: " + highscore, getWidth() / 2 - 80, getHeight() / 2 + 100);
            g.drawString("Press SPACE for Restart", getWidth() / 2 - 150, getHeight() / 2 + 150);
        } else {
            player.draw(g);
            for (Platform platform : platforms) {
                platform.draw(g);
            }
            for (PowerUp powerUp : powerUps) {
                powerUp.draw(g);
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Score: " + score, 10, 30);
            g.drawString("Highscore: " + highscore, 10, 60);

            if (countdown > 0) {
                int countdownDisplay = countdown / 60 + 1;
                String countdownText = (countdownDisplay > 1) ? String.valueOf(countdownDisplay - 1) : "Los!";
                g.setFont(new Font("Arial", Font.BOLD, 48));
                g.drawString(countdownText, getWidth() / 2 - 50, getHeight() / 2);
            }

            if (paused) {
                g.setFont(new Font("Arial", Font.BOLD, 48));
                g.drawString("PAUSE", getWidth() / 2 - 100, getHeight() / 2);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (gameOver) {
            if (keyCode == KeyEvent.VK_SPACE) {
                startGame();
            }
        } else if (gameStarted && !paused) {
            if (keyCode == KeyEvent.VK_SPACE) {
                player.jump();
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                paused = true;
            }
        } else if (paused) {
            if (keyCode == KeyEvent.VK_ESCAPE) {
                paused = false;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
