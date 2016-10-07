package lazarusgame;

import gamengine.game.GameClock;
import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import gamengine.game.Background;
import gamengine.game.BackgroundObject;
import gamengine.game.GameObject;
import gamengine.game.IGameWorld;
import gamengine.game.ObjectManager;
import gamengine.game.SchedulingObject;
import gamengine.game.Wall;
import gamengine.modifiers.AbstractGameModifier;
import gamengine.modifiers.motions.AbsInputController;
import gamengine.utils.Direction;
import gamengine.utils.SoundPlayer;
import gamengine.utils.Utils;
import java.lang.reflect.Method;
import javax.swing.JPanel;
import lazarusgame.game.Box;
import lazarusgame.game.EndButton;
import lazarusgame.game.Lazarus;

// extending JPanel to hopefully integrate this into an applet
// but I want to separate out the Applet and Application implementations
public final class LazarusWorld extends JPanel implements Runnable, Observer, IGameWorld {

    private Thread thread;

    // GameWorld is a singleton class!
    private static final LazarusWorld game = new LazarusWorld();
//    public static final GameSounds sound = new GameSounds();
    public static final GameClock clock = new GameClock();

    private BufferedImage bimg, minimap;
    int score = 0, life = 4;
    Point noSpeed = new Point(0, 0);
    Random generator = new Random();
    int sizeX, sizeY;

    /*Some ArrayLists to keep track of game things*/
    private ArrayList<BackgroundObject> background;
    private Lazarus lazarus;
//    private ArrayList<InterfaceObject> ui;
    private ArrayList<Box> boxes;

    public static HashMap<String, Image> sprites;
    boolean gameOver, gameWon, gameFinished;
    ImageObserver observer;
    private ObjectManager objectManager;
    private int[] level;            //array that will keep track of highes level per column (highest box level)
    private Box mNextBox;
    private int[] lr;
    private boolean[] possibleMovesDst;
    private int mLevel;
    private Box mCurrentBox;
    private SoundPlayer sound;
    private SoundPlayer backgroundSound;

    // initializes a bunch of ArrayLists

    private LazarusWorld() {
        this.setFocusable(true);
        lr = new int[2];
        mLevel = 1;
        background = new ArrayList<BackgroundObject>();
//        bullets = new ArrayList<Bullet>();
//        lazarus = new Lazarus();
//        ui = new ArrayList<InterfaceObject>();
        boxes = new ArrayList<Box>();

        sprites = new HashMap<String, Image>();
    }

    /* This returns a reference to the currently running game*/
    public static LazarusWorld getInstance() {
        return game;
    }

    /*Game Initialization*/
    public void init() {
        setBackground(Color.white);
        loadSprites();
        sound = new SoundPlayer(2);
        gameOver = false;
        observer = this;
        objectManager = new ObjectManager(this);
        addClockObserver(objectManager);
        startGame(1);
    }

    private void startGame(int level) {
        try {
            initBattleField("resources/lazarus_lev" + level + ".config");
            mNextBox = getRandomBox();
            Box initialBox = getRandomBox();
            initialBox.setLocation(new Point(lazarus.getX(), -40));
            Method m = LazarusWorld.class.getMethod("addBox", Box.class);
            SchedulingObject so = new SchedulingObject(initialBox, m, 50);
            objectManager.addObject(so);
            URL resource = getClass().getResource("resources/Music.wav");
            backgroundSound = new SoundPlayer(1, resource);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(LazarusWorld.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LazarusWorld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*Functions for loading image resources*/
    private void loadSprites() {
        sprites.put("wall", getSprite("resources/Wall.gif"));
        sprites.put("mesh", getSprite("resources/Mesh.gif"));

        sprites.put("button", getSprite("resources/Button.gif"));

        sprites.put("cardbox", getSprite("resources/CardBox.gif"));
        sprites.put("metalbox", getSprite("resources/MetalBox.gif"));
        sprites.put("stonebox", getSprite("resources/StoneBox.gif"));
        sprites.put("woodbox", getSprite("resources/WoodBox.gif"));
        sprites.put("rockbox", getSprite("resources/Rock.gif"));

        sprites.put("lazarus", getSprite("resources/Lazarus_stand.gif"));

        sprites.put("background", getSprite("resources/background.jpg"));

        sprites.put("Lazarus_afraid10", getSprite("resources/Lazarus_afraid10.png"));
        sprites.put("Lazarus_jump_left7", getSprite("resources/Lazarus_jump_left7.png"));
        sprites.put("Lazarus_jump_right7", getSprite("resources/Lazarus_jump_right7.png"));
        sprites.put("Lazarus_left7", getSprite("resources/Lazarus_left7.png"));
        sprites.put("Lazarus_right7", getSprite("resources/Lazarus_right7.png"));
        sprites.put("Lazarus_squished11", getSprite("resources/Lazarus_squished11.png"));
    }

    public Image getSprite(String name) {
        URL url = LazarusWorld.class.getResource(name);
        Image img = java.awt.Toolkit.getDefaultToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }

    /**
     * ******************************
     * These functions GET things	* from the game world
     *
     *
     * ****************************** @return
     */
    @Override
    public int getFrameNumber() {
        return clock.getFrame();
    }

    public int getTime() {
        return clock.getTime();
    }

    @Override
    public void removeClockObserver(Observer theObject) {
        clock.deleteObserver(theObject);
    }

    public ListIterator<BackgroundObject> getBackgroundObjects() {
        return background.listIterator();
    }
//    

    public void addBackground(BackgroundObject... newObjects) {
        for (BackgroundObject object : newObjects) {
            background.add(object);
        }
    }

    public void addRandomBox() {
        Point p = new Point(lazarus.getX(), -40);
        mNextBox.setLocation(p);
        addBox(mNextBox);
        mNextBox = getRandomBox();
    }

    public void addBox(Box b) {
        mCurrentBox = mNextBox;
        boxes.add(b);
        b.start();
//        level[b.getX()/40]++;   //increment number of boxes for this column
    }

    public int[] getLeftRightColLevels(int col) {
        final int lc = col - 1;
        final int rc = col + 1;
        lr[0] = lc < 0 || lc > level.length - 1 ? 50 : level[lc];
        lr[1] = rc < 0 || rc > level.length - 1 ? 50 : level[rc];
        return lr;
    }

    private Box getRandomBox() {
        Point p = new Point(0, sizeY - 40);
        Box b;
//        BufferedImage biPickupImage = GameObject.toBufferedImage(sprites.get("pickups"));
        int pickupIndex = generator.nextInt(5);
        Image pickupImage;
        switch (pickupIndex) {
            case 0:
                pickupImage = sprites.get("cardbox");
                break;
            case 1:
                pickupImage = sprites.get("woodbox");
                break;
            case 2:
                pickupImage = sprites.get("metalbox");
                break;
            case 3:
                pickupImage = sprites.get("rockbox");
                break;
            case 4:
                pickupImage = sprites.get("stonebox");

                break;
            default:
                pickupImage = sprites.get("rockbox");
        }
        b = new Box(p, pickupImage, pickupIndex);
        return b;
    }

    @Override
    public void addClockObserver(Observer theObject) {
        clock.addObserver(theObject);
    }

    // this is the main function where game stuff happens!
    // each frame is also drawn here
    public void drawFrame(int w, int h, Graphics2D g2) {
        ListIterator<?> iterator = getBackgroundObjects();
        while (iterator.hasNext()) {
            BackgroundObject obj = (BackgroundObject) iterator.next();
            obj.update(w, h);
            if (obj.getY() > h || !obj.show) {
                iterator.remove();
            }
            obj.draw(g2, this);
        }

        if (!gameFinished) {
            if (mNextBox != null) {
                mNextBox.draw(g2, this);
            }
            iterator = boxes.listIterator();
            boolean addNewBox = false;
            while (iterator.hasNext()) {
                Box box = (Box) iterator.next();
                if (box.isStatic()) {
                    if (box.show) {
                        box.draw(g2, this);
                    } else {
                        iterator.remove();
                    }
                    continue;
                }
                Box collidedBox = box.getCollidedBox(boxes.listIterator());
                if (addNewBox = box.checkForCollision(getBackgroundObjects())) {
                    box.stop();
                    level[getBoxCol(box)] = getBoxLevel(box);
                } else if (addNewBox = collidedBox != null) {
                    if (collidedBox.getStrength() < box.getStrength()) {
                        collidedBox.die();
                        URL url = LazarusWorld.class.getResource("resources/Crush.wav");
                        sound.setUrl(url);
                        sound.play();
                    } else {
                        box.stop();
                        level[getBoxCol(box)] = getBoxLevel(box);
//                        printLevel(box);
                    }
                }
                box.draw(g2, this);

            }

            if (addNewBox) {

                addRandomBox();
            }

            if (lazarus != null) {

//                lazarus.initPossibleMoves();
                possibleMovesDst = new boolean[]{true, true, true, true};
                GameObject go = new GameObject();
                boolean[] possibleMoves
                        = lazarus.checkForCollision(background.listIterator(), go);
                lazarus.mergePossibleMoves(possibleMovesDst, possibleMoves);
                possibleMoves
                        = lazarus.checkForBoxCollision(boxes.listIterator(), go);
                lazarus.mergePossibleMoves(possibleMovesDst, possibleMoves);
                lazarus.setPosibleMoves(possibleMovesDst);
                if (!lazarus.canLazarusMove(Direction.UP)) {
                    lazarus.die();
                }
                if (!lazarus.canLazarusMove(Direction.LEFT)
                        && !lazarus.canLazarusMove(Direction.RIGHT)) {
                    if (!lazarus.isAffraid()) {
                        lazarus.affraid();
                    }
                } else {
                    lazarus.update(w, h);
                    lazarus.draw(g2, this);
                }

            }

        } // end game stuff
        else {
            resetGame();
            startGame(mLevel);
        }

    }

    void resetGame() {
        backgroundSound.stop();
        boxes.clear();
        gameFinished = false;
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    /* paint each frame */
    @Override
    public void paint(Graphics g) {
//        if (lazarus.show) {
        clock.tick();
//        }
        Dimension windowSize = getWindowSize();
//        Graphics2D g2 = createGraphics2D(windowSize.width, windowSize.height);
        Graphics2D g2 = createGraphics2D(sizeX, sizeY);
        drawFrame(sizeX, sizeY, g2);
        g2.dispose();
        g.drawImage(bimg, 0, 0, this);
    }

    @Override
    public Dimension getSize() {
//        return super.getSize(); //To change body of generated methods, choose Tools | Templates.
        Dimension d = new Dimension(sizeX, sizeY);
        return d;
    }

    public Dimension getWindowSize() {
        return super.getSize();
    }

    /* start the game thread*/
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /* run the game */
    public void run() {

        Thread me = Thread.currentThread();
        while (thread == me) {
            this.requestFocusInWindow();
            repaint();

            try {
                thread.sleep(15); // pause a little to slow things down
            } catch (InterruptedException e) {
                break;
            }

        }
    }

    /* End the game, and signal either a win or loss */
    public void endGame(boolean win) {
        this.gameOver = true;
        this.gameWon = win;
    }

//    public boolean isGameOver() {
//        return gameOver;
//    }
    // signal that we can stop entering the game loop
    public void finishGame() {
        gameFinished = true;
    }

    public void nextLevel() {
        if (mLevel < 2) {
            mLevel++;
        }
        gameFinished = true;
    }


    /*I use the 'read' function to have observables act on their observers.
     */
    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    private void initBattleField(String battlefieldconfig) {

        HashMap<String, ArrayList<Integer[]>> configFile = Utils.getBattlefildDataFromConfigFile(battlefieldconfig, getClass());
        if (configFile.isEmpty()) {
            return;
        }

        Image button = sprites.get("button");
        sizeX = configFile.get("bf").get(0)[0];
        sizeY = configFile.get("bf").get(0)[1];
        int tileSize = configFile.get("bf").get(0)[2];
        addBackground(new Background(sizeX, sizeY, noSpeed, sprites.get("background")));

        level = new int[sizeX / tileSize];
        addWall(configFile, tileSize, "wd", Direction.DOWN);
        addWall(configFile, tileSize, "wl", Direction.LEFT);
        addWall(configFile, tileSize, "wr", Direction.RIGHT);

        for (Integer[] vals : configFile.get("b")) {
            addBackground(new EndButton(vals[0], vals[1], new Point(vals[2] * tileSize, vals[3] * tileSize), button));
        }
        Integer[] vals = configFile.get("l").get(0);
        lazarus = new Lazarus(new Point(vals[2] * tileSize, vals[3] * tileSize - 70));

    }

    private void addWall(HashMap<String, ArrayList<Integer[]>> configFile, int tileSize, String wallConfigName, Direction position) {
        Image wall = sprites.get("wall");
        Integer[] values = configFile.get(wallConfigName).get(0);
        Wall w = new Wall(values[0], values[1], new Point(values[2] * tileSize, values[3] * tileSize), wall, false);
        w.setWallPosition(position);
        addBackground(w);
        Rectangle lp = w.getLocation();
        int zeroLevel = (sizeY - lp.y + 10) / tileSize;
        for (int i = lp.x / tileSize; i < (lp.x + lp.width) / tileSize; i++) {
            level[i] = zeroLevel;
        }
    }

    @Override
    public void setKeyListener(AbsInputController aThis) {
        this.addKeyListener(aThis);
    }

    private void printLevel(Box box) {
        System.out.println("level[" + getBoxCol(box) + "] = " + level[getBoxCol(box)]);
    }

    int getBoxCol(Box box) {
        if (box != null) {
            return (box.getX() + 10) / 40;
        } else {
            return 50;
        }
    }

    int getBoxLevel(Box box) {
        if (box != null) {
            return (sizeY - box.getY() + 10) / 40;
        } else {
            return 50;
        }
    }

    public Lazarus getLazarus() {
        return lazarus;
    }
}
