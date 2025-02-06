import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U'; // U D L R
        int velocityX = 0, velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            this.direction = direction;
            updateVelocity();
        }

        void updateVelocity() {
            switch (this.direction) {
                case 'U': velocityX = 0; velocityY = -tileSize / 4; break;
                case 'D': velocityX = 0; velocityY = tileSize / 4; break;
                case 'L': velocityX = -tileSize / 4; velocityY = 0; break;
                case 'R': velocityX = tileSize / 4; velocityY = 0; break;
            }
        }

        void move() {
            int newX = this.x + this.velocityX;
            int newY = this.y + this.velocityY;
            if (!isWallCollision(newX, newY)) {
                this.x = newX;
                this.y = newY;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private final int rowCount = 21, columnCount = 19, tileSize = 32;
    private final int boardWidth = columnCount * tileSize, boardHeight = rowCount * tileSize;

    private Image wallImage, pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private final String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "X  X  P       X  X",
        "X XXXXXXXXXXXXX X X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    Block pacman;
    Timer gameLoop;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        loadImages();
        loadMap();

        gameLoop = new Timer(50, this); // 20 FPS
        gameLoop.start();
    }

    private void loadImages() {
        wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("/pacmanRight.png")).getImage();
    }

    private void loadMap() {
        walls = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                int x = c * tileSize, y = r * tileSize;
                switch (tileMap[r].charAt(c)) {
                    case 'X': walls.add(new Block(wallImage, x, y, tileSize, tileSize)); break;
                    case 'P': pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize); break;
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block wall : walls) g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString(gameOver ? "Game Over" : "Pac-Man Game", tileSize / 2, tileSize / 2);
    }

    private boolean isWallCollision(int x, int y) {
        for (Block wall : walls) {
            if (x < wall.x + wall.width && x + tileSize > wall.x && y < wall.y + wall.height && y + tileSize > wall.y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            gameLoop.stop();
            return;
        }
        pacman.move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: pacman.updateDirection('U'); pacman.image = pacmanUpImage; break;
            case KeyEvent.VK_DOWN: pacman.updateDirection('D'); pacman.image = pacmanDownImage; break;
            case KeyEvent.VK_LEFT: pacman.updateDirection('L'); pacman.image = pacmanLeftImage; break;
            case KeyEvent.VK_RIGHT: pacman.updateDirection('R'); pacman.image = pacmanRightImage; break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // **MAIN METHOD**
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pac-Man");
            PacMan game = new PacMan();
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
