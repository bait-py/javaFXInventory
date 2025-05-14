// CharacterController.java - WoW TBC UI avanzado con sistema de ítems reales
package org.example.wowinventory.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import org.example.wowinventory.models.Character;
import org.example.wowinventory.models.Item;
import org.example.wowinventory.utils.SoundManager;
import org.example.wowinventory.utils.StatCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CharacterController {

    @FXML
    private Label characterNameLabel, raceClassLabel, levelLabel, goldLabel;

    @FXML
    private Label strengthLabel, agilityLabel, staminaLabel, intellectLabel, spiritLabel, armorLabel;

    @FXML
    private Label damageLabel, criticalLabel, hitRatingLabel, attackPowerLabel;

    @FXML
    private GridPane inventoryGrid;

    @FXML
    private GridPane equipmentGrid;

    @FXML
    private ProgressBar healthBar, manaBar, experienceBar;

    @FXML
    private Label healthLabel, manaLabel, expLabel;

    @FXML
    private HBox quickBarContainer;

    private final int SLOT_SIZE = 74;
    private final int INVENTORY_ROWS = 4;
    private final int INVENTORY_COLS = 4;

    private Character playerCharacter;
    private Map<StackPane, Item> itemDataMap = new HashMap<>();
    private Map<Item.ItemType, StackPane> equipmentSlots = new HashMap<>();
    private SoundManager soundManager = new SoundManager();
    private Random random = new Random();

    public void initialize() {
        // Inicializar personaje
        // Inicializar personaje con Stats
        playerCharacter = new Character("Gazrok", "Orco", "Guerrero", 70, new org.example.wowinventory.models.Stats(50, 30, 45, 20, 25, 100));

        // Configurar UI con datos del personaje
        actualizarInterfazPersonaje();

        // Crear slots de inventario y equipamiento
        crearSlotsBolsasInventario();
        crearSlotsEquipamiento();

        // Cargar ítems de ejemplo en el inventario
        cargarInventario();

        // Inicializar barra rápida
        inicializarBarraRapida();

        // Inicializar barras de progreso
        inicializarBarrasProgreso();
    }

    private void actualizarInterfazPersonaje() {
        characterNameLabel.setText(playerCharacter.getName());
        raceClassLabel.setText(playerCharacter.getRace() + " " + playerCharacter.getCharacterClass());
        levelLabel.setText("Nivel " + playerCharacter.getLevel());
        goldLabel.setText(formatearOro(playerCharacter.getGold()));

        // Actualizar estadísticas base
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        // Estadísticas base más bonificaciones de equipo
        int strength = playerCharacter.getBaseStrength() + playerCharacter.getBonusStrength();
        int agility = playerCharacter.getBaseAgility() + playerCharacter.getBonusAgility();
        int stamina = playerCharacter.getBaseStamina() + playerCharacter.getBonusStamina();
        int intellect = playerCharacter.getBaseIntellect() + playerCharacter.getBonusIntellect();
        int spirit = playerCharacter.getBaseSpirit() + playerCharacter.getBonusSpirit();
        int armor = playerCharacter.getBaseArmor() + playerCharacter.getBonusArmor();

        // Actualizar etiquetas
        strengthLabel.setText(String.valueOf(strength));
        agilityLabel.setText(String.valueOf(agility));
        staminaLabel.setText(String.valueOf(stamina));
        intellectLabel.setText(String.valueOf(intellect));
        spiritLabel.setText(String.valueOf(spirit));
        armorLabel.setText(String.valueOf(armor));

        // Calcular y mostrar estadísticas derivadas
        int minDmg = StatCalculator.calcularDañoMinimo(strength, playerCharacter.getLevel());
        int maxDmg = StatCalculator.calcularDañoMaximo(strength, playerCharacter.getLevel());
        double critChance = StatCalculator.calcularProbabilidadCritico(agility, playerCharacter.getLevel());
        int attackPower = StatCalculator.calcularPoderAtaque(strength);
        double hitRating = StatCalculator.calcularPrecision(playerCharacter.getLevel());

        damageLabel.setText(minDmg + " - " + maxDmg);
        criticalLabel.setText(String.format("%.2f%%", critChance));
        hitRatingLabel.setText(String.format("%.1f%%", hitRating));
        attackPowerLabel.setText(String.valueOf(attackPower));

        // Actualizar salud/maná basado en stamina/intelecto
        int maxHealth = StatCalculator.calcularSaludMaxima(stamina, playerCharacter.getLevel());
        int maxMana = StatCalculator.calcularManaMaximo(intellect, playerCharacter.getLevel());

        playerCharacter.setMaxHealth(maxHealth);
        playerCharacter.setMaxMana(maxMana);

        // Actualizar barras de progreso
        actualizarBarrasEstado();
    }

    private void actualizarBarrasEstado() {
        double healthPercent = (double) playerCharacter.getCurrentHealth() / playerCharacter.getMaxHealth();
        double manaPercent = (double) playerCharacter.getCurrentMana() / playerCharacter.getMaxMana();
        double expPercent = (double) playerCharacter.getCurrentExp() / playerCharacter.getExpToNextLevel();

        healthBar.setProgress(healthPercent);
        manaBar.setProgress(manaPercent);
        experienceBar.setProgress(expPercent);

        healthLabel.setText(playerCharacter.getCurrentHealth() + " / " + playerCharacter.getMaxHealth());
        manaLabel.setText(playerCharacter.getCurrentMana() + " / " + playerCharacter.getMaxMana());
        expLabel.setText(playerCharacter.getCurrentExp() + " / " + playerCharacter.getExpToNextLevel());

        // Cambiar color según el porcentaje de salud
        if (healthPercent < 0.2) {
            healthBar.setStyle("-fx-accent: darkred;");
        } else if (healthPercent < 0.5) {
            healthBar.setStyle("-fx-accent: orange;");
        } else {
            healthBar.setStyle("-fx-accent: green;");
        }
    }

    private void crearSlotsBolsasInventario() {
        for (int fila = 0; fila < INVENTORY_ROWS; fila++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                StackPane slot = crearSlotVacio();
                inventoryGrid.add(slot, col, fila);
            }
        }
    }

    private void crearSlotsEquipamiento() {
        // Crear los slots de equipamiento para cada tipo de ítem
        Map<Item.ItemType, Integer[]> posicionesEquipo = new HashMap<>();

        // Definir posiciones en el grid (columna, fila)
        posicionesEquipo.put(Item.ItemType.HEAD, new Integer[]{1, 0});
        posicionesEquipo.put(Item.ItemType.NECK, new Integer[]{1, 1});
        posicionesEquipo.put(Item.ItemType.SHOULDERS, new Integer[]{2, 0});
        posicionesEquipo.put(Item.ItemType.CHEST, new Integer[]{1, 2});
        posicionesEquipo.put(Item.ItemType.WAIST, new Integer[]{1, 3});
        posicionesEquipo.put(Item.ItemType.LEGS, new Integer[]{1, 4});
        posicionesEquipo.put(Item.ItemType.FEET, new Integer[]{1, 5});
        posicionesEquipo.put(Item.ItemType.WRISTS, new Integer[]{0, 2});
        posicionesEquipo.put(Item.ItemType.HANDS, new Integer[]{2, 2});
        posicionesEquipo.put(Item.ItemType.FINGER, new Integer[]{0, 3});
        posicionesEquipo.put(Item.ItemType.TRINKET, new Integer[]{2, 3});
        posicionesEquipo.put(Item.ItemType.BACK, new Integer[]{2, 1});
        posicionesEquipo.put(Item.ItemType.WEAPON, new Integer[]{0, 4});
        posicionesEquipo.put(Item.ItemType.OFFHAND, new Integer[]{2, 4});
        posicionesEquipo.put(Item.ItemType.RANGED, new Integer[]{2, 5});

        // Crear cada slot en su posición correspondiente
        for (Item.ItemType tipo : posicionesEquipo.keySet()) {
            Integer[] posicion = posicionesEquipo.get(tipo);
            StackPane slot = crearSlotEquipo(tipo);
            equipmentGrid.add(slot, posicion[0], posicion[1]);
            equipmentSlots.put(tipo, slot);
        }
    }

    private StackPane crearSlotVacio() {
        StackPane slot = new StackPane();
        slot.setPrefSize(SLOT_SIZE, SLOT_SIZE);
        slot.getStyleClass().add("inventory-slot");

        ImageView fondo = new ImageView(new Image(getClass().getResourceAsStream("/img/slot-vacio.png")));
        fondo.setFitWidth(SLOT_SIZE);
        fondo.setFitHeight(SLOT_SIZE);

        slot.getChildren().add(fondo);

        configurarDragAndDrop(slot);

        return slot;
    }

    private StackPane crearSlotEquipo(Item.ItemType tipo) {
        StackPane slot = crearSlotVacio();
        slot.getStyleClass().add("equipment-slot");

        // Añadir icono de silueta según el tipo
        ImageView silueta = new ImageView(new Image(getClass().getResourceAsStream("/img/slot-" + tipo.name().toLowerCase() + ".png")));
        silueta.setFitWidth(SLOT_SIZE);
        silueta.setFitHeight(SLOT_SIZE);
        silueta.setOpacity(0.3);

        slot.getChildren().add(silueta);

        // Configurar tooltip con el nombre del slot
        Tooltip tooltip = new Tooltip("Slot: " + obtenerNombreLocalizado(tipo));
        Tooltip.install(slot, tooltip);

        // Validación específica para este tipo de slot
        slot.setUserData(tipo); // Guardamos el tipo en el userData

        return slot;
    }

    private void cargarInventario() {
        // Crear ítems de ejemplo más variados
        Item[] items = new Item[]{
                new Item("Casco de placas Gladiador", Item.ItemType.HEAD, "/img/items/helmet-warrior.jpg", 70, "+22 Fuerza\n+28 Aguante\n+35 Resistencia\n+18 Valor"),
                new Item("Guantes de agarre de hierro", Item.ItemType.HANDS, "/img/items/gloves-warrior.jpg", 70, "+18 Fuerza\n+20 Aguante\n+12 Golpe"),
                new Item("Anillo de poder arcano", Item.ItemType.FINGER, "/img/items/ring-mage.jpg", 70, "+15 Intelecto\n+12 Daño mágico\n+22 Espíritu"),
                new Item("Botas de velocidad", Item.ItemType.FEET, "/img/items/boots-leather.jpg", 68, "+18 Agilidad\n+12 Aguante\n+5% Velocidad de movimiento"),
                new Item("Espada de llamas eternales", Item.ItemType.WEAPON, "/img/items/sword-fire.jpg", 70, "72-135 Daño\n+3.60 Velocidad\n+35 Fuerza\nEfecto: Llamarada"),
                new Item("Hombreras del campeón", Item.ItemType.SHOULDERS, "/img/items/shoulders-plate.jpg", 70, "+15 Fuerza\n+22 Aguante\n+18 Defensa"),
                new Item("Amuleto de protección", Item.ItemType.NECK, "/img/items/amulet-defense.jpg", 67, "+12 Defensa\n+15 Aguante\n+10 Resistencia a las sombras"),
                new Item("Capa del viento veloz", Item.ItemType.BACK, "/img/items/cloak-wind.jpg", 69, "+15 Agilidad\n+12 Aguante\n+15 Resistencia a la naturaleza")
        };

        // Distribuir en slots aleatorios
        int slotCount = INVENTORY_ROWS * INVENTORY_COLS;
        boolean[] ocupados = new boolean[slotCount];

        for (Item item : items) {
            int randomSlot;
            do {
                randomSlot = random.nextInt(slotCount);
            } while (ocupados[randomSlot]);

            ocupados[randomSlot] = true;

            int fila = randomSlot / INVENTORY_COLS;
            int col = randomSlot % INVENTORY_COLS;

            StackPane slot = (StackPane) obtenerSlotEn(inventoryGrid, col, fila);
            colocarItemEnSlot(slot, item);
        }
    }

    private StackPane obtenerSlotEn(GridPane grid, int col, int fila) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == fila) {
                return (StackPane) node;
            }
        }
        return null;
    }

    private void colocarItemEnSlot(StackPane slot, Item item) {
        // Limpiar el slot primero (dejando solo el fondo)
        if (slot.getChildren().size() > 1) {
            slot.getChildren().subList(1, slot.getChildren().size()).clear();
        }

        // Cargar ícono del ítem
        ImageView icono = new ImageView(new Image(getClass().getResourceAsStream(item.getIcon())));
        icono.setFitWidth(64);
        icono.setFitHeight(64);

        // Agregar borde según rareza
        ImageView marco = new ImageView(new Image(getClass().getResourceAsStream(obtenerBordePorRareza(item))));
        marco.setFitWidth(SLOT_SIZE);
        marco.setFitHeight(SLOT_SIZE);

        // Añadir efecto brillante para ítems épicos y legendarios
        if (item.getRarity() == Item.Rarity.EPIC || item.getRarity() == Item.Rarity.LEGENDARY) {
            DropShadow glow = new DropShadow();
            glow.setColor(obtenerColorPorRareza(item));
            glow.setRadius(10);
            icono.setEffect(glow);
        }

        // Añadir indicador de nivel requerido si el jugador no cumple
        if (item.getRequiredLevel() > playerCharacter.getLevel()) {
            Label nivelReq = new Label("Niv " + item.getRequiredLevel());
            nivelReq.getStyleClass().add("level-req-label");
            nivelReq.setTextFill(Color.RED);

            slot.getChildren().addAll(icono, marco, nivelReq);
        } else {
            slot.getChildren().addAll(icono, marco);
        }

        // Crear tooltip detallado
        Tooltip tooltip = crearTooltipDetallado(item);
        Tooltip.install(slot, tooltip);

        // Guardar referencia al ítem
        itemDataMap.put(slot, item);
    }

    private Tooltip crearTooltipDetallado(Item item) {
        StringBuilder tooltipText = new StringBuilder();

        // Nombre con color según rareza
        tooltipText.append(item.getName()).append("\n");

        // Tipo de ítem y ubicación
        tooltipText.append(obtenerNombreLocalizado(item.getType())).append("\n");

        // Nivel requerido
        tooltipText.append("Nivel requerido: ").append(item.getRequiredLevel()).append("\n");

        // Estadísticas
        String[] stats = item.getStatsDescription().split("\n");
        for (String stat : stats) {
            tooltipText.append(stat).append("\n");
        }

        // Valor de venta
        tooltipText.append("\nValor: ").append(calcularPrecioVenta(item));

        Tooltip tooltip = new Tooltip(tooltipText.toString());
        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #222222; -fx-text-fill: white;");

        return tooltip;
    }

    private void configurarDragAndDrop(StackPane slot) {
        slot.setOnDragDetected(e -> {
            if (!tieneItem(slot)) return;

            Dragboard db = slot.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("drag-item");

            // Snapshot del ítem para mostrar durante el arrastre
            ImageView dragView = new ImageView(slot.getChildren().get(1).snapshot(null, null));
            dragView.setFitWidth(50);
            dragView.setFitHeight(50);
            db.setDragView(dragView.snapshot(null, null));

            db.setContent(content);
            e.consume();
        });

        slot.setOnDragOver(e -> {
            if (e.getGestureSource() != slot && e.getDragboard().hasString()) {
                StackPane source = (StackPane) e.getGestureSource();

                // Validar si es un slot de equipo con restricciones
                if (slot.getUserData() instanceof Item.ItemType) {
                    Item.ItemType tipoSlot = (Item.ItemType) slot.getUserData();
                    Item itemArrastrado = itemDataMap.get(source);

                    // Solo permitir soltar si el tipo coincide
                    if (itemArrastrado != null && itemArrastrado.getType() == tipoSlot
                            && itemArrastrado.getRequiredLevel() <= playerCharacter.getLevel()) {
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                } else {
                    // El inventario acepta cualquier ítem
                    e.acceptTransferModes(TransferMode.MOVE);
                }
            }
            e.consume();
        });

        slot.setOnDragEntered(e -> {
            if (e.getGestureSource() != slot && e.getDragboard().hasString()) {
                // Efecto visual al entrar en un slot válido
                StackPane source = (StackPane) e.getGestureSource();
                boolean valido = true;

                if (slot.getUserData() instanceof Item.ItemType) {
                    Item.ItemType tipoSlot = (Item.ItemType) slot.getUserData();
                    Item itemArrastrado = itemDataMap.get(source);

                    valido = (itemArrastrado != null && itemArrastrado.getType() == tipoSlot
                            && itemArrastrado.getRequiredLevel() <= playerCharacter.getLevel());
                }

                if (valido) {
                    slot.setStyle("-fx-background-color: rgba(100, 255, 100, 0.3);");
                } else {
                    slot.setStyle("-fx-background-color: rgba(255, 100, 100, 0.3);");
                }
            }
            e.consume();
        });

        slot.setOnDragExited(e -> {
            // Restaurar estilo
            slot.setStyle("");
            e.consume();
        });

        slot.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                StackPane sourceSlot = (StackPane) e.getGestureSource();

                // Verificar si se está equipando/desequipando (slots de equipo)
                boolean esEquipamiento = sourceSlot.getUserData() instanceof Item.ItemType ||
                        slot.getUserData() instanceof Item.ItemType;

                intercambiarContenido(sourceSlot, slot);
                success = true;

                // Reproducir sonido
                if (esEquipamiento) {
                    soundManager.reproducirSonido("equip");
                    // Recalcular estadísticas si se cambió equipamiento
                    actualizarBonificacionesPorEquipo();
                } else {
                    soundManager.reproducirSonido("move");
                }
            }

            e.setDropCompleted(success);
            e.consume();
        });

        // Doble clic para equipar/desequipar rápidamente
        slot.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && tieneItem(slot)) {
                Item item = itemDataMap.get(slot);

                // Si es un slot de inventario, intentar equipar
                if (!(slot.getUserData() instanceof Item.ItemType)) {
                    if (item.getRequiredLevel() <= playerCharacter.getLevel()) {
                        StackPane equipSlot = equipmentSlots.get(item.getType());
                        if (equipSlot != null) {
                            intercambiarContenido(slot, equipSlot);
                            soundManager.reproducirSonido("equip");
                            actualizarBonificacionesPorEquipo();
                        }
                    } else {
                        // Nivel insuficiente
                        mostrarMensajeError("¡Nivel insuficiente para equipar este objeto!");
                    }
                } else {
                    // Si es un slot de equipo, desequipar al primer slot libre
                    StackPane slotLibre = encontrarSlotLibre();
                    if (slotLibre != null) {
                        intercambiarContenido(slot, slotLibre);
                        soundManager.reproducirSonido("equip");
                        actualizarBonificacionesPorEquipo();
                    } else {
                        mostrarMensajeError("¡Inventario lleno!");
                    }
                }
            }
        });

        // Clic derecho para mostrar menú contextual
        slot.setOnContextMenuRequested(e -> {
            if (tieneItem(slot)) {
                mostrarMenuContextualItem(slot, e.getScreenX(), e.getScreenY());
            }
            e.consume();
        });
    }

    private StackPane encontrarSlotLibre() {
        for (int fila = 0; fila < INVENTORY_ROWS; fila++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                StackPane slot = (StackPane) obtenerSlotEn(inventoryGrid, col, fila);
                if (slot != null && !tieneItem(slot)) {
                    return slot;
                }
            }
        }
        return null;
    }

    private void mostrarMenuContextualItem(StackPane slot, double x, double y) {
        Item item = itemDataMap.get(slot);
        if (item == null) return;

        // Aquí se implementaría el menú contextual (con JavaFX ContextMenu)
        // Por simplicidad, solo imprimimos en consola
        System.out.println("Menú contextual para: " + item.getName());

        // Opciones: Equipar/Desequipar, Vender, Destruir, etc.
    }

    private void intercambiarContenido(StackPane origen, StackPane destino) {
        Item itemOrigen = itemDataMap.get(origen);
        Item itemDestino = itemDataMap.get(destino);

        // Si el destino ya tiene un ítem, intercambiar
        if (itemDestino != null) {
            colocarItemEnSlot(origen, itemDestino);
            colocarItemEnSlot(destino, itemOrigen);
        } else {
            // Si el destino está vacío, mover
            colocarItemEnSlot(destino, itemOrigen);
            // Limpiar origen
            limpiarSlot(origen);
            itemDataMap.remove(origen);
        }
    }

    private void limpiarSlot(StackPane slot) {
        // Dejar solo el fondo
        if (slot.getChildren().size() > 1) {
            slot.getChildren().subList(1, slot.getChildren().size()).clear();
        }
    }

    private boolean tieneItem(StackPane slot) {
        return itemDataMap.containsKey(slot) && slot.getChildren().size() > 1;
    }

    private void actualizarBonificacionesPorEquipo() {
        // Reiniciar bonificaciones
        playerCharacter.resetBonuses();

        // Calcular bonificaciones de cada ítem equipado
        for (Item.ItemType tipo : equipmentSlots.keySet()) {
            StackPane slot = equipmentSlots.get(tipo);
            Item item = itemDataMap.get(slot);

            if (item != null) {
                // Aplicar bonificaciones del ítem
                aplicarBonificacionesItem(item);
            }
        }

        // Actualizar estadísticas con nuevas bonificaciones
        actualizarEstadisticas();
    }

    private void aplicarBonificacionesItem(Item item) {
        // Parsear descripción de estadísticas y aplicar bonificaciones
        String[] statLines = item.getStatsDescription().split("\n");

        for (String line : statLines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            try {
                // Formato esperado: "+XX Estadística"
                if (line.startsWith("+")) {
                    String[] parts = line.substring(1).split(" ", 2);
                    int value = Integer.parseInt(parts[0]);
                    String stat = parts[1].toLowerCase();

                    if (stat.contains("fuerza")) {
                        playerCharacter.addBonusStrength(value);
                    } else if (stat.contains("aguante")) {
                        playerCharacter.addBonusStamina(value);
                    } else if (stat.contains("agilidad")) {
                        playerCharacter.addBonusAgility(value);
                    } else if (stat.contains("intelecto")) {
                        playerCharacter.addBonusIntellect(value);
                    } else if (stat.contains("espíritu")) {
                        playerCharacter.addBonusSpirit(value);
                    } else if (stat.contains("armadura") || stat.contains("defensa")) {
                        playerCharacter.addBonusArmor(value);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al parsear stat: " + line);
            }
        }
    }

    private String obtenerBordePorRareza(Item item) {
        // Determinar rareza basada en nivel y otras características
        Item.Rarity rareza = item.getRarity();

        switch (rareza) {
            case LEGENDARY:
                return "/img/borders/border-orange.png";
            case EPIC:
                return "/img/borders/border-purple.png";
            case RARE:
                return "/img/borders/border-blue.png";
            case UNCOMMON:
                return "/img/borders/border-green.png";
            default:
                return "/img/borders/border-gray.png";
        }
    }

    private Color obtenerColorPorRareza(Item item) {
        Item.Rarity rareza = item.getRarity();

        switch (rareza) {
            case LEGENDARY:
                return Color.ORANGE;
            case EPIC:
                return Color.PURPLE;
            case RARE:
                return Color.BLUE;
            case UNCOMMON:
                return Color.GREEN;
            default:
                return Color.GRAY;
        }
    }

    private String obtenerNombreLocalizado(Item.ItemType tipo) {
        switch (tipo) {
            case HEAD:
                return "Cabeza";
            case NECK:
                return "Cuello";
            case SHOULDERS:
                return "Hombros";
            case CHEST:
                return "Pecho";
            case WAIST:
                return "Cintura";
            case LEGS:
                return "Piernas";
            case FEET:
                return "Pies";
            case WRISTS:
                return "Muñecas";
            case HANDS:
                return "Manos";
            case FINGER:
                return "Dedo";
            case TRINKET:
                return "Abalorio";
            case BACK:
                return "Espalda";
            case WEAPON:
                return "Arma";
            case OFFHAND:
                return "Mano izquierda";
            case RANGED:
                return "A distancia";
            default:
                return tipo.name();
        }
    }

    private void inicializarBarraRapida() {
        // Crear 10 slots para la barra rápida
        for (int i = 0; i < 10; i++) {
            StackPane slot = new StackPane();
            slot.setPrefSize(SLOT_SIZE, SLOT_SIZE);
            slot.getStyleClass().add("quickbar-slot");

            ImageView fondo = new ImageView(new Image(getClass().getResourceAsStream("/img/quickbar-slot.png")));
            fondo.setFitWidth(SLOT_SIZE);
            fondo.setFitHeight(SLOT_SIZE);

            // Añadir número de atajo
            Label numLabel = new Label(String.valueOf((i + 1) % 10));
            numLabel.getStyleClass().add("quickbar-number");

            slot.getChildren().addAll(fondo, numLabel);
            quickBarContainer.getChildren().add(slot);

            configurarDragAndDrop(slot);
        }
    }
    private void inicializarBarrasProgreso() {
        // Configuración inicial de las barras de progreso
        healthBar.setProgress(1.0);
        healthBar.setStyle("-fx-accent: green;");

        manaBar.setProgress(1.0);
        manaBar.setStyle("-fx-accent: blue;");

        experienceBar.setProgress(0.0);
        experienceBar.setStyle("-fx-accent: purple;");

        // Initialize health and mana to maximum values
        if (playerCharacter.getMaxHealth() > 0) {
            try {
                playerCharacter.setCurrentHealth(playerCharacter.getMaxHealth());
                playerCharacter.setCurrentMana(playerCharacter.getMaxMana());
            } catch (Exception e) {
                System.err.println("Error setting initial health/mana: " + e.getMessage());
            }
        }

        // Actualizar etiquetas de las barras
        actualizarBarrasEstado();
    }

    private String formatearOro(int copper) {
        int gold = copper / 10000;
        copper %= 10000;
        int silver = copper / 100;
        copper %= 100;

        return String.format("%d|cffffcc00g|r %d|cffc0c0c0s|r %d|cff954f28c|r", gold, silver, copper);
    }

    private String calcularPrecioVenta(Item item) {
        // Valor base según nivel y rareza
        int baseValue = item.getRequiredLevel() * 100;
        int multiplier = 1;

        switch (item.getRarity()) {
            case LEGENDARY:
                multiplier = 1000;
                break;
            case EPIC:
                multiplier = 500;
                break;
            case RARE:
                multiplier = 100;
                break;
            case UNCOMMON:
                multiplier = 50;
                break;
            default:
                multiplier = 10;
        }

        return formatearOro(baseValue * multiplier);
    }

    private void mostrarMensajeError(String mensaje) {
        Label errorLabel = new Label(mensaje);
        errorLabel.getStyleClass().add("error-message");

        // Mostrar por 3 segundos y desvanecer
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> errorLabel.setVisible(false));
        delay.play();
    }
}