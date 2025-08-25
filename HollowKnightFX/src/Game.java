package HollowKnightFX.src;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;

public class Game extends Pane {
    private Knight knight;
    private AnimationTimer gameLoop;
    private boolean[] keysPressed;
    private double cameraX = 0;
    private double cameraY = 0;

    // Camadas de parallax
    private ParallaxLayer[] parallaxLayers;

    public Game() {
        keysPressed = new boolean[KeyCode.values().length];
        initializeGame();
    }

    private void initializeGame() {
        setStyle("-fx-background-color: #0a0a1a;");

        // Inicializar camadas de parallax
        initializeParallaxLayers();

        // Criar o cavaleiro
        knight = new Knight(400, 300);
        getChildren().add(knight);

        // Criar algumas plataformas
        createPlatforms();
    }

    private void initializeParallaxLayers() {
        parallaxLayers = new ParallaxLayer[5];

        // Camada 0: Montanhas distantes (movimento muito lento)
        parallaxLayers[0] = new ParallaxLayer(0, 0, 3000, 700,
                Color.rgb(20, 15, 30), 0.1);
        parallaxLayers[0].addMountain(500, 400, 300, 200, Color.rgb(30, 25, 40));
        parallaxLayers[0].addMountain(1200, 350, 400, 250, Color.rgb(25, 20, 35));
        parallaxLayers[0].addMountain(2000, 380, 350, 220, Color.rgb(35, 30, 45));

        // Camada 1: Montanhas médias (movimento lento)
        parallaxLayers[1] = new ParallaxLayer(0, 0, 3000, 700,
                Color.TRANSPARENT, 0.3);
        parallaxLayers[1].addMountain(300, 450, 250, 150, Color.rgb(40, 35, 55));
        parallaxLayers[1].addMountain(900, 420, 300, 180, Color.rgb(45, 40, 60));
        parallaxLayers[1].addMountain(1600, 440, 280, 160, Color.rgb(50, 45, 65));
        parallaxLayers[1].addMountain(2300, 430, 320, 170, Color.rgb(55, 50, 70));

        // Camada 2: Árvores e estruturas (movimento médio)
        parallaxLayers[2] = new ParallaxLayer(0, 0, 3000, 700,
                Color.TRANSPARENT, 0.5);
        parallaxLayers[2].addTree(200, 500, 80, 120, Color.rgb(30, 50, 20));
        parallaxLayers[2].addTree(600, 480, 70, 140, Color.rgb(25, 45, 15));
        parallaxLayers[2].addTree(1000, 520, 90, 100, Color.rgb(35, 55, 25));
        parallaxLayers[2].addTree(1400, 490, 75, 130, Color.rgb(40, 60, 30));
        parallaxLayers[2].addTree(1800, 510, 85, 110, Color.rgb(20, 40, 10));
        parallaxLayers[2].addTree(2200, 480, 65, 150, Color.rgb(45, 65, 35));
        parallaxLayers[2].addTree(2600, 500, 95, 90, Color.rgb(30, 50, 20));

        // Camada 3: Estruturas próximas (movimento rápido)
        parallaxLayers[3] = new ParallaxLayer(0, 0, 3000, 700,
                Color.TRANSPARENT, 0.8);
        parallaxLayers[3].addColumn(150, 550, 40, 150, Color.rgb(80, 70, 90));
        parallaxLayers[3].addColumn(450, 530, 35, 170, Color.rgb(85, 75, 95));
        parallaxLayers[3].addColumn(800, 560, 45, 140, Color.rgb(75, 65, 85));
        parallaxLayers[3].addColumn(1200, 540, 30, 160, Color.rgb(90, 80, 100));
        parallaxLayers[3].addColumn(1600, 550, 50, 150, Color.rgb(70, 60, 80));
        parallaxLayers[3].addColumn(2000, 520, 25, 180, Color.rgb(95, 85, 105));
        parallaxLayers[3].addColumn(2400, 530, 40, 170, Color.rgb(65, 55, 75));
        parallaxLayers[3].addColumn(2800, 560, 55, 140, Color.rgb(100, 90, 110));

        // Camada 4: Partículas flutuantes (movimento muito rápido)
        parallaxLayers[4] = new ParallaxLayer(0, 0, 3000, 700,
                Color.TRANSPARENT, 1.2);
        for (int i = 0; i < 50; i++) {
            double x = Math.random() * 3000;
            double y = 100 + Math.random() * 400;
            double size = 2 + Math.random() * 6;
            parallaxLayers[4].addParticle(x, y, size,
                    Color.rgb(150, 180, 255, 0.6 + Math.random() * 0.4));
        }

        // Adicionar todas as camadas ao painel
        for (ParallaxLayer layer : parallaxLayers) {
            getChildren().add(layer);
        }
    }

    private void createPlatforms() {
        // Chão principal
        Rectangle ground = new Rectangle(0, 600, 3000, 100);
        ground.setFill(createRockTexture(Color.rgb(60, 50, 70), Color.rgb(40, 30, 50)));
        getChildren().add(ground);

        // Plataformas em diferentes níveis
        createPlatform(400, 500, 200, 20, Color.rgb(80, 60, 90));
        createPlatform(200, 450, 150, 15, Color.rgb(70, 50, 80));
        createPlatform(600, 400, 250, 15, Color.rgb(90, 70, 100));
        createPlatform(100, 350, 100, 12, Color.rgb(75, 55, 85));
        createPlatform(700, 300, 180, 12, Color.rgb(85, 65, 95));
        createPlatform(300, 250, 120, 10, Color.rgb(65, 45, 75));
        createPlatform(800, 200, 200, 10, Color.rgb(95, 75, 105));

        // Adicionar estalactites
        for (int i = 0; i < 20; i++) {
            createStalactite(100 + i * 150, 0);
        }

        // Adicionar cristais (decoração)
        for (int i = 0; i < 15; i++) {
            createCrystal(200 + i * 200, 580 - (i % 3) * 30);
        }

        // Adicionar pedras e detritos
        for (int i = 0; i < 30; i++) {
            createRock(50 + i * 100, 590);
        }
    }

    private Color createRockTexture(Color base, Color highlight) {
        Lighting lighting = new Lighting();
        lighting.setLight(new Light.Distant(45, 45, Color.WHITE));
        return base;
    }

    private void createPlatform(double x, double y, double width, double height, Color color) {
        Rectangle platform = new Rectangle(x, y, width, height);
        platform.setFill(createRockTexture(color, color.brighter()));
        platform.setStroke(color.darker());
        platform.setStrokeWidth(2);
        getChildren().add(platform);
    }

    private void createStalactite(double x, double y) {
        Rectangle stalactite = new Rectangle(x, y, 10 + Math.random() * 10, 40 + Math.random() * 30);
        stalactite.setFill(Color.rgb(100, 90, 120));
        stalactite.setArcWidth(10);
        stalactite.setArcHeight(10);
        getChildren().add(stalactite);
    }

    private void createCrystal(double x, double y) {
        Rectangle crystal = new Rectangle(x, y, 8, 20);
        crystal.setFill(Color.rgb(150, 180, 255, 0.7));
        crystal.setStroke(Color.rgb(200, 220, 255));
        crystal.setStrokeWidth(1.5);
        getChildren().add(crystal);
    }

    private void createRock(double x, double y) {
        Rectangle rock = new Rectangle(x, y, 15 + Math.random() * 10, 10 + Math.random() * 5);
        rock.setFill(Color.rgb(70, 60, 80));
        rock.setArcWidth(8);
        rock.setArcHeight(8);
        getChildren().add(rock);
    }

    public void handleKeyPress(KeyEvent event) {
        keysPressed[event.getCode().ordinal()] = true;
    }

    public void handleKeyRelease(KeyEvent event) {
        keysPressed[event.getCode().ordinal()] = false;
    }

    public void startGame() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

    public void update() {
        handleInput();
        knight.update();
        updateCamera();
        checkCollisions();
        updateParallax();
    }

    private void handleInput() {
        if (keysPressed[KeyCode.W.ordinal()] || keysPressed[KeyCode.UP.ordinal()]) {
            knight.jump();
        }
        if (keysPressed[KeyCode.A.ordinal()] || keysPressed[KeyCode.LEFT.ordinal()]) {
            knight.moveLeft();
        } else if (keysPressed[KeyCode.D.ordinal()] || keysPressed[KeyCode.RIGHT.ordinal()]) {
            knight.moveRight();
        } else {
            knight.stop();
        }

        if (keysPressed[KeyCode.SPACE.ordinal()]) {
            knight.attack();
        }
    }

    private void updateCamera() {
        double targetX = knight.getTranslateX() - 400;
        double targetY = knight.getTranslateY() - 300;

        cameraX += (targetX - cameraX) * 0.1;
        cameraY += (targetY - cameraY) * 0.1;

        if (cameraX < 0)
            cameraX = 0;
        if (cameraY < 0)
            cameraY = 0;
        if (cameraX > 2500)
            cameraX = 2500;
        if (cameraY > 300)
            cameraY = 300;

        setTranslateX(-cameraX);
        setTranslateY(-cameraY);
    }

    private void updateParallax() {
        for (ParallaxLayer layer : parallaxLayers) {
            layer.update(cameraX, cameraY);
        }
    }

    private void checkCollisions() {
        boolean onGround = false;

        for (javafx.scene.Node node : getChildren()) {
            if (node instanceof Rectangle && node != knight && !(node.getParent() instanceof ParallaxLayer)) {
                Rectangle platform = (Rectangle) node;
                if (knight.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    handlePlatformCollision(platform);
                    onGround = true;
                }
            }
        }

        knight.setOnGround(onGround);
    }

    private void handlePlatformCollision(Rectangle platform) {
        if (knight.getTranslateY() + knight.getHeight() > platform.getY() &&
                knight.getTranslateY() < platform.getY()) {
            knight.setTranslateY(platform.getY() - knight.getHeight());
            knight.setVelocityY(0);
        }
    }
}

class ParallaxLayer extends Pane {
    private double parallaxFactor;

    public ParallaxLayer(double x, double y, double width, double height, Color color, double parallaxFactor) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefSize(width, height);
        if (!color.equals(Color.TRANSPARENT)) {
            setStyle("-fx-background-color: #" + color.toString().substring(2, 8) + ";");
        }
        this.parallaxFactor = parallaxFactor;
    }

    public void addMountain(double x, double y, double width, double height, Color color) {
        Rectangle mountain = new Rectangle(x, y, width, height);
        mountain.setFill(color);
        mountain.setArcWidth(30);
        mountain.setArcHeight(30);
        getChildren().add(mountain);
    }

    public void addTree(double x, double y, double width, double height, Color color) {
        // Tronco
        Rectangle trunk = new Rectangle(x + width / 2 - 5, y + height / 2, 10, height / 2);
        trunk.setFill(Color.rgb(60, 40, 20));

        // Copa
        Rectangle canopy = new Rectangle(x, y, width, height / 2);
        canopy.setFill(color);
        canopy.setArcWidth(20);
        canopy.setArcHeight(20);

        getChildren().addAll(trunk, canopy);
    }

    public void addColumn(double x, double y, double width, double height, Color color) {
        Rectangle column = new Rectangle(x, y, width, height);
        column.setFill(color);

        // Detalhes na coluna
        for (int i = 0; i < 5; i++) {
            Rectangle detail = new Rectangle(x, y + i * (height / 5), width, 3);
            detail.setFill(color.brighter());
            getChildren().add(detail);
        }

        getChildren().add(column);
    }

    public void addParticle(double x, double y, double size, Color color) {
        Rectangle particle = new Rectangle(x, y, size, size);
        particle.setFill(color);
        particle.setArcWidth(size);
        particle.setArcHeight(size);
        getChildren().add(particle);
    }

    public void update(double cameraX, double cameraY) {
        setTranslateX(-cameraX * parallaxFactor);
        setTranslateY(-cameraY * parallaxFactor * 0.5); // Movimento vertical mais suave
    }
}

class Knight extends ImageView {
    private double velocityX = 0;
    private double velocityY = 0;
    private double gravity = 0.5;
    private double jumpForce = -12;
    private double speed = 5;
    private boolean onGround = false;
    private boolean isAttacking = false;
    private int attackCooldown = 0;
    private boolean facingRight = true;

    public Knight(double x, double y) {
        setFitWidth(40);
        setFitHeight(60);
        setTranslateX(x);
        setTranslateY(y);

        try {
            setImage(new Image("https://via.placeholder.com/40x60/3498db/ffffff?text=Knight"));
        } catch (Exception e) {
            setStyle("-fx-background-color: blue; -fx-background-radius: 10;");
        }
    }

    public void update() {
        if (!onGround)
            velocityY += gravity;
        if (velocityY > 15)
            velocityY = 15;

        setTranslateX(getTranslateX() + velocityX);
        setTranslateY(getTranslateY() + velocityY);

        // Limitar movimento dentro dos limites do mundo
        if (getTranslateX() < 0)
            setTranslateX(0);
        if (getTranslateX() > 2960)
            setTranslateX(2960);
        if (getTranslateY() > 540) {
            setTranslateY(540);
            onGround = true;
            velocityY = 0;
        }

        if (isAttacking) {
            attackCooldown--;
            if (attackCooldown <= 0)
                isAttacking = false;
        }

        if (velocityX > 0 && !facingRight)
            flipRight();
        else if (velocityX < 0 && facingRight)
            flipLeft();
    }

    private void flipRight() {
        facingRight = true;
        setScaleX(1);
    }

    private void flipLeft() {
        facingRight = false;
        setScaleX(-1);
    }

    public void moveLeft() {
        velocityX = -speed;
    }

    public void moveRight() {
        velocityX = speed;
    }

    public void stop() {
        velocityX *= 0.8;
        if (Math.abs(velocityX) < 0.5)
            velocityX = 0;
    }

    public void jump() {
        if (onGround) {
            velocityY = jumpForce;
            onGround = false;
        }
    }

    public void attack() {
        if (!isAttacking) {
            isAttacking = true;
            attackCooldown = 20;
            System.out.println("Ataque!");
        }
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getHeight() {
        return getFitHeight();
    }
}