
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Random;

public class RPGJavaFX extends Application {

    private Stage primaryStage;
    private Scene menuScene, classSelectionScene, explorationScene, battleScene;

    // Dados do jogador
    private String playerClass;
    private int playerHP = 100;
    private int playerMaxHP = 100;
    private int playerMP = 50;
    private int playerMaxMP = 50;
    private int playerLevel = 1;
    private int playerXP = 0;
    private int playerX = 400;
    private int playerY = 300;

    // Dados da batalha
    private String enemyType;
    private int enemyHP;
    private int enemyMaxHP;
    private boolean inBattle = false;
    private boolean playerTurn = true;

    // Controles
    private HashMap<KeyCode, Boolean> keys = new HashMap<>();
    private AnimationTimer gameLoop;

    // Sistema de tiles (simplificado)
    private int[][] mapTiles;
    private final int TILE_SIZE = 40;
    private final int MAP_WIDTH = 20;
    private final int MAP_HEIGHT = 14;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inicializar mapa
        inicializarMapa();

        criarCenaMenu();
        criarCenaSelecaoClasse();

        primaryStage.setTitle("RPG JavaFX");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    private void inicializarMapa() {
        mapTiles = new int[MAP_HEIGHT][MAP_WIDTH];
        Random rand = new Random();

        // Preencher mapa com tiles (0 = grama, 1 = água, 2 = árvore)
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) {
                    mapTiles[y][x] = 2; // Árvores nas bordas
                } else {
                    mapTiles[y][x] = rand.nextInt(10) == 0 ? 1 : 0; // 10% de chance de água
                }
            }
        }
    }

    private void criarCenaMenu() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #2b2b2b, #1c1c1c);");

        Label titulo = new Label("Meu RPG Épico");
        titulo.setFont(Font.font("Arial", 36));
        titulo.setTextFill(Color.GOLD);

        Button btnNovoJogo = new Button("Novo Jogo");
        Button btnCarregar = new Button("Carregar");
        Button btnSair = new Button("Sair");

        // Estilização dos botões
        String estiloBotao = "-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 40px;";
        String estiloBotaoHover = "-fx-background-color: #4e5254; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 40px;";

        btnNovoJogo.setStyle(estiloBotao);
        btnCarregar.setStyle(estiloBotao);
        btnSair.setStyle(estiloBotao);

        btnNovoJogo.setOnMouseEntered(e -> btnNovoJogo.setStyle(estiloBotaoHover));
        btnNovoJogo.setOnMouseExited(e -> btnNovoJogo.setStyle(estiloBotao));
        btnCarregar.setOnMouseEntered(e -> btnCarregar.setStyle(estiloBotaoHover));
        btnCarregar.setOnMouseExited(e -> btnCarregar.setStyle(estiloBotao));
        btnSair.setOnMouseEntered(e -> btnSair.setStyle(estiloBotaoHover));
        btnSair.setOnMouseExited(e -> btnSair.setStyle(estiloBotao));

        btnNovoJogo.setOnAction(e -> primaryStage.setScene(classSelectionScene));
        btnSair.setOnAction(e -> primaryStage.close());

        layout.getChildren().addAll(titulo, btnNovoJogo, btnCarregar, btnSair);
        menuScene = new Scene(layout, 800, 600);
    }

    private void criarCenaSelecaoClasse() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: linear-gradient(to bottom, #2b2b2b, #1c1c1c);");

        Label titulo = new Label("Selecione sua classe:");
        titulo.setFont(Font.font("Arial", 28));
        titulo.setTextFill(Color.WHITE);
        grid.add(titulo, 0, 0, 4, 1);

        // Classes disponíveis
        String[] classes = { "Guerreiro", "Mago", "Arqueiro", "Clérigo" };
        String[] descricoes = {
                "Especialista em combate corpo a corpo com alta resistência. \n\nVantagens: +20% HP, +10% Defesa",
                "Usa magias poderosas para destruir os inimigos. \n\nVantagens: +30% MP, +15% Dano Mágico",
                "Ataca à distância com precisão mortal. \n\nVantagens: +15% Precisão, +10% Velocidade",
                "Cura aliados e usa magias divinas. \n\nVantagens: +20% Cura, +10% Resistência a Magia"
        };

        for (int i = 0; i < classes.length; i++) {
            VBox card = criarCardClasse(classes[i], descricoes[i]);
            int finalI = i;
            card.setOnMouseClicked(e -> {
                playerClass = classes[finalI];
                aplicarBonusClasse();
                iniciarExploracao();
            });
            grid.add(card, i % 2, 1 + i / 2);
        }

        classSelectionScene = new Scene(grid, 800, 600);
    }

    private void aplicarBonusClasse() {
        switch (playerClass) {
            case "Guerreiro":
                playerMaxHP = (int) (playerMaxHP * 1.2);
                playerHP = playerMaxHP;
                break;
            case "Mago":
                playerMaxMP = (int) (playerMaxMP * 1.3);
                playerMP = playerMaxMP;
                break;
            case "Arqueiro":
                // Bônus implementados no cálculo de dano
                break;
            case "Clérigo":
                // Bônus implementados no cálculo de cura
                break;
        }
    }

    private VBox criarCardClasse(String classe, String descricao) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: #3c3f41; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #555; -fx-cursor: hand;");
        card.setPrefWidth(350);

        Label nomeClasse = new Label(classe);
        nomeClasse.setFont(Font.font("Arial", 20));
        nomeClasse.setTextFill(Color.GOLD);

        TextArea desc = new TextArea(descricao);
        desc.setEditable(false);
        desc.setWrapText(true);
        desc.setPrefRowCount(5);
        desc.setPrefHeight(150);
        desc.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-border-color: #555;");

        card.getChildren().addAll(nomeClasse, desc);

        // Efeito hover
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #4e5254; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #777; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #3c3f41; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #555; -fx-cursor: hand;"));

        return card;
    }

    private void iniciarExploracao() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1c1c1c;");

        // Canvas para o mapa
        Canvas canvas = new Canvas(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // HUD inferior
        HBox hud = new HBox(10);
        hud.setPadding(new Insets(10));
        hud.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-width: 2 0 0 0;");

        Label info = new Label("Classe: " + playerClass + " | Nível: " + playerLevel +
                " | HP: " + playerHP + "/" + playerMaxHP +
                " | MP: " + playerMP + "/" + playerMaxMP);
        info.setTextFill(Color.WHITE);
        info.setFont(Font.font(14));

        hud.getChildren().add(info);

        root.setCenter(canvas);
        root.setBottom(hud);

        explorationScene = new Scene(root, MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE + 40);

        // Controles de movimento
        explorationScene.setOnKeyPressed(e -> {
            keys.put(e.getCode(), true);

            // Tecla E para simular encontro com inimigo
            if (e.getCode() == KeyCode.E) {
                iniciarBatalha();
            }
        });

        explorationScene.setOnKeyReleased(e -> {
            keys.put(e.getCode(), false);
        });

        primaryStage.setScene(explorationScene);

        // Loop de animação
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!inBattle) {
                    processarEntrada();
                    desenharMapa(gc);
                }
            }
        };
        gameLoop.start();
    }

    private void processarEntrada() {
        int moveSpeed = 3;

        if (keys.getOrDefault(KeyCode.UP, false)) {
            if (podeMover(playerX, playerY - moveSpeed))
                playerY -= moveSpeed;
        }
        if (keys.getOrDefault(KeyCode.DOWN, false)) {
            if (podeMover(playerX, playerY + moveSpeed))
                playerY += moveSpeed;
        }
        if (keys.getOrDefault(KeyCode.LEFT, false)) {
            if (podeMover(playerX - moveSpeed, playerY))
                playerX -= moveSpeed;
        }
        if (keys.getOrDefault(KeyCode.RIGHT, false)) {
            if (podeMover(playerX + moveSpeed, playerY))
                playerX += moveSpeed;
        }

        // Chance aleatória de encontro com inimigo ao se mover
        if ((keys.getOrDefault(KeyCode.UP, false) ||
                keys.getOrDefault(KeyCode.DOWN, false) ||
                keys.getOrDefault(KeyCode.LEFT, false) ||
                keys.getOrDefault(KeyCode.RIGHT, false)) &&
                new Random().nextInt(100) < 2) { // 2% de chance por frame
            iniciarBatalha();
        }
    }

    private boolean podeMover(int x, int y) {
        // Verificar colisão com bordas
        if (x < 0 || x >= MAP_WIDTH * TILE_SIZE || y < 0 || y >= MAP_HEIGHT * TILE_SIZE) {
            return false;
        }

        // Verificar colisão com tiles (convertendo coordenadas para tiles)
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;

        // Água e árvores são obstáculos
        return mapTiles[tileY][tileX] == 0;
    }

    private void desenharMapa(GraphicsContext gc) {
        // Limpar canvas
        gc.clearRect(0, 0, MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);

        // Desenhar tiles
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                switch (mapTiles[y][x]) {
                    case 0: // Grama
                        gc.setFill(Color.FORESTGREEN);
                        break;
                    case 1: // Água
                        gc.setFill(Color.DODGERBLUE);
                        break;
                    case 2: // Árvore
                        gc.setFill(Color.DARKGREEN);
                        break;
                }
                gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Bordas dos tiles
                gc.setStroke(Color.BLACK);
                gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Desenhar player (círculo com cor baseada na classe)
        switch (playerClass) {
            case "Guerreiro":
                gc.setFill(Color.RED);
                break;
            case "Mago":
                gc.setFill(Color.BLUE);
                break;
            case "Arqueiro":
                gc.setFill(Color.GREEN);
                break;
            case "Clérigo":
                gc.setFill(Color.GOLD);
                break;
        }
        gc.fillOval(playerX - 10, playerY - 10, 20, 20);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(playerX - 10, playerY - 10, 20, 20);
    }

    private void iniciarBatalha() {
        inBattle = true;
        gameLoop.stop();

        // Selecionar inimigo aleatório
        String[] inimigos = { "Slime", "Goblin", "Lobo", "Esqueleto" };
        enemyType = inimigos[new Random().nextInt(inimigos.length)];

        // Definir HP baseado no nível do jogador
        enemyMaxHP = 20 + (playerLevel * 10);
        enemyHP = enemyMaxHP;

        criarCenaBatalha();
        primaryStage.setScene(battleScene);
    }

    private void criarCenaBatalha() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1c1c1c, #2b2b2b);");

        // Área dos inimigos (topo)
        VBox areaInimigos = new VBox(10);
        areaInimigos.setAlignment(Pos.CENTER);
        areaInimigos.setPadding(new Insets(20));
        areaInimigos.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-width: 0 0 2 0;");

        Label inimigoLabel = new Label(enemyType + " Nv." + playerLevel);
        inimigoLabel.setTextFill(Color.WHITE);
        inimigoLabel.setFont(Font.font("Arial", 20));

        ProgressBar hpInimigo = new ProgressBar(1.0);
        hpInimigo.setPrefWidth(300);
        hpInimigo.setStyle("-fx-accent: red;");

        Label hpInimigoTexto = new Label("HP: " + enemyHP + "/" + enemyMaxHP);
        hpInimigoTexto.setTextFill(Color.WHITE);

        areaInimigos.getChildren().addAll(inimigoLabel, hpInimigo, hpInimigoTexto);

        // Área de mensagens (centro)
        TextArea mensagens = new TextArea("Um " + enemyType + " selvagem apareceu!");
        mensagens.setEditable(false);
        mensagens.setPrefRowCount(5);
        mensagens.setStyle(
                "-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-border-color: #555; -fx-font-size: 14px;");

        // Área de ações (base)
        HBox areaAcoes = new HBox(10);
        areaAcoes.setAlignment(Pos.CENTER);
        areaAcoes.setPadding(new Insets(10));
        areaAcoes.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-width: 2 0 0 0;");

        Button btnAtacar = new Button("Atacar");
        Button btnMagia = new Button("Magia");
        Button btnDefender = new Button("Defender");
        Button btnItem = new Button("Item");
        Button btnFugir = new Button("Fugir");

        // Estilizar botões
        String estiloBotaoBatalha = "-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 80px;";
        String estiloBotaoBatalhaHover = "-fx-background-color: #4e5254; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 80px;";

        btnAtacar.setStyle(estiloBotaoBatalha);
        btnMagia.setStyle(estiloBotaoBatalha);
        btnDefender.setStyle(estiloBotaoBatalha);
        btnItem.setStyle(estiloBotaoBatalha);
        btnFugir.setStyle(estiloBotaoBatalha);

        btnAtacar.setOnMouseEntered(e -> btnAtacar.setStyle(estiloBotaoBatalhaHover));
        btnAtacar.setOnMouseExited(e -> btnAtacar.setStyle(estiloBotaoBatalha));
        btnMagia.setOnMouseEntered(e -> btnMagia.setStyle(estiloBotaoBatalhaHover));
        btnMagia.setOnMouseExited(e -> btnMagia.setStyle(estiloBotaoBatalha));
        btnDefender.setOnMouseEntered(e -> btnDefender.setStyle(estiloBotaoBatalhaHover));
        btnDefender.setOnMouseExited(e -> btnDefender.setStyle(estiloBotaoBatalha));
        btnItem.setOnMouseEntered(e -> btnItem.setStyle(estiloBotaoBatalhaHover));
        btnItem.setOnMouseExited(e -> btnItem.setStyle(estiloBotaoBatalha));
        btnFugir.setOnMouseEntered(e -> btnFugir.setStyle(estiloBotaoBatalhaHover));
        btnFugir.setOnMouseExited(e -> btnFugir.setStyle(estiloBotaoBatalha));

        // Ações dos botões
        btnAtacar.setOnAction(e -> {
            if (playerTurn) {
                int dano = calcularDano();
                enemyHP -= dano;
                mensagens.appendText("\nVocê atacou e causou " + dano + " de dano!");

                atualizarHUDInimigo(hpInimigo, hpInimigoTexto);
                verificarFimBatalha(mensagens);
                playerTurn = false;
                turnoInimigo(mensagens);
            }
        });

        btnMagia.setOnAction(e -> {
            if (playerTurn && playerMP >= 10) {
                playerMP -= 10;
                int dano = calcularDanoMagico();
                enemyHP -= dano;
                mensagens.appendText("\nVocê usou magia e causou " + dano + " de dano!");

                atualizarHUDInimigo(hpInimigo, hpInimigoTexto);
                verificarFimBatalha(mensagens);
                playerTurn = false;
                turnoInimigo(mensagens);
            } else if (playerMP < 10) {
                mensagens.appendText("\nMP insuficiente para usar magia!");
            }
        });

        btnDefender.setOnAction(e -> {
            if (playerTurn) {
                // Recuperar um pouco de HP ao defender
                int cura = 5 + (playerLevel * 2);
                playerHP = Math.min(playerHP + cura, playerMaxHP);
                mensagens.appendText("\nVocê se defendeu e recuperou " + cura + " de HP!");

                atualizarHUDJogador();
                playerTurn = false;
                turnoInimigo(mensagens);
            }
        });

        btnFugir.setOnAction(e -> {
            if (new Random().nextInt(100) < 50) { // 50% de chance de fugir
                mensagens.appendText("\nVocê fugiu da batalha!");
                terminarBatalha(false);
            } else {
                mensagens.appendText("\nFalha ao tentar fugir!");
                playerTurn = false;
                turnoInimigo(mensagens);
            }
        });

        areaAcoes.getChildren().addAll(btnAtacar, btnMagia, btnDefender, btnItem, btnFugir);

        // HUD do jogador
        VBox hudJogador = new VBox(5);
        hudJogador.setPadding(new Insets(10));
        hudJogador.setStyle("-fx-background-color: #3c3f41; -fx-border-color: #555; -fx-border-width: 2 0 0 0;");

        Label nomeJogador = new Label(playerClass + " Nv." + playerLevel);
        nomeJogador.setTextFill(Color.WHITE);
        nomeJogador.setFont(Font.font("Arial", 16));

        ProgressBar hpJogador = new ProgressBar((double) playerHP / playerMaxHP);
        hpJogador.setPrefWidth(250);
        hpJogador.setStyle("-fx-accent: green;");

        Label hpLabel = new Label("HP: " + playerHP + "/" + playerMaxHP);
        hpLabel.setTextFill(Color.WHITE);

        ProgressBar mpJogador = new ProgressBar((double) playerMP / playerMaxMP);
        mpJogador.setPrefWidth(250);
        mpJogador.setStyle("-fx-accent: blue;");

        Label mpLabel = new Label("MP: " + playerMP + "/" + playerMaxMP);
        mpLabel.setTextFill(Color.WHITE);

        hudJogador.getChildren().addAll(nomeJogador, hpJogador, hpLabel, mpJogador, mpLabel);

        // Montar layout
        root.setTop(areaInimigos);
        root.setCenter(mensagens);
        root.setBottom(hudJogador);
        root.setRight(areaAcoes);

        battleScene = new Scene(root, 800, 600);
    }

    private int calcularDano() {
        Random rand = new Random();
        int danoBase = 5 + (playerLevel * 2);

        // Bônus de classe
        if (playerClass.equals("Guerreiro"))
            danoBase = (int) (danoBase * 1.2);
        if (playerClass.equals("Arqueiro"))
            danoBase = (int) (danoBase * 1.1);

        // Chance de crítico (15%)
        if (rand.nextInt(100) < 15) {
            danoBase *= 2;
        }

        return danoBase + rand.nextInt(5);
    }

    private int calcularDanoMagico() {
        Random rand = new Random();
        int danoBase = 8 + (playerLevel * 3);

        // Bônus de classe
        if (playerClass.equals("Mago"))
            danoBase = (int) (danoBase * 1.15);

        return danoBase + rand.nextInt(7);
    }

    private void turnoInimigo(TextArea mensagens) {
        if (enemyHP > 0) {
            Random rand = new Random();
            int danoInimigo = 3 + (playerLevel * 2) + rand.nextInt(4);

            // Dano variado por tipo de inimigo
            switch (enemyType) {
                case "Goblin":
                    danoInimigo = (int) (danoInimigo * 1.2);
                    break;
                case "Lobo":
                    danoInimigo = (int) (danoInimigo * 0.9);
                    break;
                case "Esqueleto":
                    danoInimigo = (int) (danoInimigo * 1.1);
                    break;
            }

            playerHP -= danoInimigo;
            mensagens.appendText("\nO " + enemyType + " atacou e causou " + danoInimigo + " de dano!");

            atualizarHUDJogador();

            if (playerHP <= 0) {
                mensagens.appendText("\nVocê foi derrotado!");
                Button btn = new Button("Voltar ao Menu");
                btn.setOnAction(e -> primaryStage.setScene(menuScene));

                VBox gameOver = new VBox(10);
                gameOver.setAlignment(Pos.CENTER);
                gameOver.getChildren().addAll(new Label("Game Over"), btn);

                BorderPane root = (BorderPane) battleScene.getRoot();
                root.setCenter(gameOver);
            } else {
                playerTurn = true;
            }
        }
    }

    private void atualizarHUDInimigo(ProgressBar hpInimigo, Label hpInimigoTexto) {
        hpInimigo.setProgress((double) enemyHP / enemyMaxHP);
        hpInimigoTexto.setText("HP: " + enemyHP + "/" + enemyMaxHP);
    }

    private void atualizarHUDJogador() {
        // Esta seria chamada se tivéssemos uma referência aos componentes HUD
        // Na implementação real, precisaríamos de referências para atualizar
    }

    private void verificarFimBatalha(TextArea mensagens) {
        if (enemyHP <= 0) {
            mensagens.appendText("\nVocê derrotou o " + enemyType + "!");

            // Ganhar XP
            int xpGanho = 20 + (playerLevel * 10);
            playerXP += xpGanho;
            mensagens.appendText("\nGanhou " + xpGanho + " pontos de experiência!");

            // Verificar se subiu de nível
            int xpNecessario = playerLevel * 50;
            if (playerXP >= xpNecessario) {
                playerLevel++;
                playerXP -= xpNecessario;
                playerMaxHP += 10;
                playerHP = playerMaxHP;
                playerMaxMP += 5;
                playerMP = playerMaxMP;
                mensagens.appendText("\nVocê subiu para o nível " + playerLevel + "!");
            }

            terminarBatalha(true);
        }
    }

    private void terminarBatalha(boolean vitoria) {
        inBattle = false;

        Button btnContinuar = new Button("Continuar Explorando");
        btnContinuar.setOnAction(e -> {
            primaryStage.setScene(explorationScene);
            gameLoop.start();
        });

        VBox resultado = new VBox(10);
        resultado.setAlignment(Pos.CENTER);
        resultado.getChildren().addAll(
                new Label(vitoria ? "Vitória!" : "Fuga bem-sucedida!"),
                btnContinuar);

        BorderPane root = (BorderPane) battleScene.getRoot();
        root.setCenter(resultado);
    }

    public static void main(String[] args) {
        launch(args);
    }
}