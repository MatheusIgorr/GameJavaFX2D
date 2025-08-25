package HollowKnightFX.src;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;

public class Knight extends Group {
    private double velocityX = 0;
    private double velocityY = 0;
    private double gravity = 0.5;
    private double jumpForce = -12;
    private double speed = 5;
    private boolean onGround = false;
    private boolean isAttacking = false;
    private int attackCooldown = 0;
    private boolean facingRight = true;

    // Partes do corpo do Knight
    private Circle head;
    private Ellipse eyeLeft, eyeRight;
    private Rectangle hornLeft, hornRight;
    private Rectangle body;
    private Rectangle cloak;
    private Rectangle armLeft, armRight;
    private Rectangle legLeft, legRight;
    private Rectangle nail; // Espada (Nail)

    public Knight(double x, double y) {
        createKnightVisual();
        setTranslateX(x);
        setTranslateY(y);
    }

    private void createKnightVisual() {
        // Cabeça (branca)
        head = new Circle(0, -25, 12);
        head.setFill(Color.WHITE);
        head.setStroke(Color.BLACK);
        head.setStrokeWidth(1);

        // Chifres (pretos curvos)
        hornLeft = new Rectangle(-15, -35, 8, 20);
        hornLeft.setFill(Color.BLACK);
        hornLeft.setRotate(-30);
        hornLeft.setArcWidth(10);
        hornLeft.setArcHeight(10);

        hornRight = new Rectangle(7, -35, 8, 20);
        hornRight.setFill(Color.BLACK);
        hornRight.setRotate(30);
        hornRight.setArcWidth(10);
        hornRight.setArcHeight(10);

        // Olhos (ovalados pretos)
        eyeLeft = new Ellipse(-5, -25, 3, 5);
        eyeLeft.setFill(Color.BLACK);

        eyeRight = new Ellipse(5, -25, 3, 5);
        eyeRight.setFill(Color.BLACK);

        // Corpo (preto)
        body = new Rectangle(-10, -10, 20, 30);
        body.setFill(Color.BLACK);
        body.setArcWidth(10);
        body.setArcHeight(10);

        // Manto/capa (vermelho escuro)
        cloak = new Rectangle(-15, -5, 30, 40);
        cloak.setFill(Color.rgb(80, 0, 0)); // Vermelho escuro
        cloak.setArcWidth(15);
        cloak.setArcHeight(15);
        cloak.setStroke(Color.rgb(60, 0, 0));
        cloak.setStrokeWidth(2);

        // Braços
        armLeft = new Rectangle(-18, 0, 8, 20);
        armLeft.setFill(Color.BLACK);
        armLeft.setArcWidth(5);
        armLeft.setArcHeight(5);

        armRight = new Rectangle(10, 0, 8, 20);
        armRight.setFill(Color.BLACK);
        armRight.setArcWidth(5);
        armRight.setArcHeight(5);

        // Pernas
        legLeft = new Rectangle(-12, 25, 10, 20);
        legLeft.setFill(Color.BLACK);
        legLeft.setArcWidth(5);
        legLeft.setArcHeight(5);

        legRight = new Rectangle(2, 25, 10, 20);
        legRight.setFill(Color.BLACK);
        legRight.setArcWidth(5);
        legRight.setArcHeight(5);

        // Espada (Nail) - inicialmente invisível
        nail = new Rectangle(15, -5, 25, 5);
        nail.setFill(Color.rgb(200, 200, 200)); // Cinza prateado
        nail.setStroke(Color.rgb(150, 150, 150));
        nail.setStrokeWidth(1);
        nail.setVisible(false);

        // Efeito de sombra
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        this.setEffect(shadow);

        // Adicionar todas as partes ao grupo
        getChildren().addAll(
                cloak, body, armLeft, armRight,
                legLeft, legRight, head, hornLeft, hornRight,
                eyeLeft, eyeRight, nail);

        // Configurar tamanho do grupo
        setScaleX(0.8);
        setScaleY(0.8);
    }

    public void update() {
        // Aplicar gravidade
        if (!onGround) {
            velocityY += gravity;
        }

        // Atualizar posição
        setTranslateX(getTranslateX() + velocityX);
        setTranslateY(getTranslateY() + velocityY);

        // Limitar movimento horizontal
        if (getTranslateX() < 20)
            setTranslateX(20);
        if (getTranslateX() > 2960)
            setTranslateX(2960);

        // Limitar movimento vertical
        if (getTranslateY() > 540) {
            setTranslateY(540);
            onGround = true;
            velocityY = 0;
        }

        // Gerenciar cooldown do ataque
        if (isAttacking) {
            attackCooldown--;
            if (attackCooldown <= 0) {
                isAttacking = false;
                nail.setVisible(false);
            } else if (attackCooldown > 15) {
                // Animação do ataque
                nail.setVisible(true);
                if (facingRight) {
                    nail.setTranslateX(15);
                    nail.setRotate(45);
                } else {
                    nail.setTranslateX(-15);
                    nail.setRotate(-45);
                }
            }
        }

        // Atualizar direção do personagem
        if (velocityX > 0 && !facingRight) {
            flipRight();
        } else if (velocityX < 0 && facingRight) {
            flipLeft();
        }

        // Animação de pulo
        if (!onGround) {
            // Braços para cima ao pular
            armLeft.setRotate(-20);
            armRight.setRotate(-20);
        } else {
            // Braços normais
            armLeft.setRotate(0);
            armRight.setRotate(0);
        }

        // Resetar onGround para falso a cada frame
        onGround = false;
    }

    private void flipRight() {
        facingRight = true;
        setScaleX(0.8); // Normal
        nail.setTranslateX(15);
    }

    private void flipLeft() {
        facingRight = false;
        setScaleX(-0.8); // Espelhado
        nail.setTranslateX(-15);
    }

    public void moveLeft() {
        velocityX = -speed;
    }

    public void moveRight() {
        velocityX = speed;
    }

    public void stop() {
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
            System.out.println("Ataque executado!");
        }
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        if (onGround) {
            velocityY = 0;
        }
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getHeight() {
        return 60; // Altura aproximada do personagem
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    // Método para obter os limites de colisão
    public javafx.geometry.Bounds getCollisionBounds() {
        return body.getBoundsInParent();
    }
}