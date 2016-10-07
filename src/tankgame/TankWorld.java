package tankgame;

import gamengine.game.GameClock;
import java.awt.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import gamengine.game.Background;
import gamengine.game.BackgroundObject;
import tankgame.game.Bullet;
import gamengine.game.IGameWorld;
import gamengine.game.ObjectManager;
import gamengine.game.SchedulingObject;
import tankgame.game.PlayerShip;
import tankgame.game.PowerUp;
import tankgame.game.Ship;
import tankgame.game.SmallExplosion;
import gamengine.game.Wall;
import gamengine.modifiers.AbstractGameModifier;
import gamengine.modifiers.motions.AbsInputController;
import gamengine.modifiers.weapons.AbstractWeapon;
import gamengine.utils.ImageUtils;
import gamengine.utils.SoundPlayer;
import gamengine.utils.Utils;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import tankgame.weapons.SimpleWeapon;
import tankgame.weapons.WeaponBounceMissile;
import tankgame.weapons.WeaponRocket;
import tankgame.weapons.WeaponShield;
import tankgame.ui.InfoBar;
import tankgame.ui.InterfaceObject;
import tankgame.ui.ScoreInfo;

// extending JPanel to hopefully integrate this into an applet
// but I want to separate out the Applet and Application implementations
public final class TankWorld extends JPanel implements Runnable, Observer, IGameWorld {

    private Thread thread;

    // GameWorld is a singleton class!
    private static final TankWorld game = new TankWorld();
//    public static final GameSounds sound = new GameSounds();
    public static final GameClock clock = new GameClock();
//    GameMenu menu;
//    public Level level;

    private BufferedImage bimg, minimap;
    int score = 0, life = 4;
//    Point speed = new Point(0,1);
    Point noSpeed = new Point(0, 0);
    Random generator = new Random();
    int sizeX, sizeY;

    /*Some ArrayLists to keep track of game things*/
    private ArrayList<BackgroundObject> background;
    private ArrayList<Bullet> bullets;
    private ArrayList<PlayerShip> mPlayers, playersInPlay;
    private ArrayList<InterfaceObject> ui;
    private ArrayList<Ship> powerups;

    public static HashMap<String, Image> sprites;
//    public static HashMap<String, MotionController> motions = new HashMap<String, MotionController>();

    // is player still playing, did they win, and should we exit
    boolean gameOver, gameWon, gameFinished;
    ImageObserver observer;
    private ObjectManager objectManager;
//    private final int mWindowWidth;
//    private final int mWindowHeight;
    private ScoreInfo mScoreInfo;

    // constructors makes sure the game is focusable, then
    // initializes a bunch of ArrayLists
    private TankWorld() {
        this.setFocusable(true);
        background = new ArrayList<BackgroundObject>();
        bullets = new ArrayList<Bullet>();
        mPlayers = new ArrayList<PlayerShip>();
        playersInPlay = new ArrayList<PlayerShip>();
        ui = new ArrayList<InterfaceObject>();
        powerups = new ArrayList<Ship>();

        sprites = new HashMap<String, Image>();
    }

    /* This returns a reference to the currently running game*/
    public static TankWorld getInstance() {
        return game;
    }

    /*Game Initialization*/
    public void init() {
        setBackground(Color.white);
        loadSprites();

//        level = new Level(sizeX,sizeY);
//        clock.addObserver(level);
//        level.addObserver(this);
        gameOver = false;
        observer = this;

        objectManager = new ObjectManager(this);
        addClockObserver(objectManager);
        initBattleField("ResourcesTank/battlefield.config");
        URL resource = getClass().getResource("ResourcesTank/Music.wav");
        new SoundPlayer(1, resource);

//        menu = new GameMenu();
//        menu.applySelection();
        int[] controls = {KeyEvent.VK_A, KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_SPACE};
        PlayerShip[] players = new PlayerShip[2];
        players[0] = new PlayerShip(new Point(32, 32), new Point(6, 6), TankWorld.sprites.get("player1"), controls, Color.RED);
        controls = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER};
        players[1] = new PlayerShip(new Point(getSize().width - 32 - 64, getSize().height - 3 * 32), new Point(6, 6), TankWorld.sprites.get("player2"), controls, Color.BLUE);
        players[1].setHeading(Math.PI);
        addPlayer(players);
    }

    /*Functions for loading image resources*/
    private void loadSprites() {
        sprites.put("wall1", getSprite("ResourcesTank/Wall1.gif"));
        sprites.put("wall2", getSprite("ResourcesTank/Wall2.gif"));
        sprites.put("Shield1", getSprite("ResourcesTank/Shield1.gif"));
        sprites.put("Shield2", getSprite("ResourcesTank/Shield2.gif"));
        sprites.put("background", getSprite("ResourcesTank/background.jpg"));

        sprites.put("bullet", getSprite("ResourcesTank/bullet_tank.png"));
        sprites.put("Explosion_small_strip6", getSprite("ResourcesTank/Explosion_small_strip6.png"));
        sprites.put("Explosion_large_strip7", getSprite("ResourcesTank/Explosion_large_strip7.png"));

        sprites.put("player1", getSprite("ResourcesTank/tank1.png"));
        sprites.put("player2", getSprite("ResourcesTank/tank2.png"));
        int i = 0;
        for (i = 1; i <= 6; i++) {
            sprites.put("explosion1_" + i, getSprite("ResourcesTank/explosion_small" + (i - 1) + ".png"));
        }

        for (i = 1; i <= 7; i++) {
            sprites.put("explosion2_" + i, getSprite("ResourcesTank/explosion_large" + (i - 1) + ".png"));
        }

        sprites.put("weapons", getSprite("ResourcesTank/Weapon_strip3.png"));
        sprites.put("pickups", getSprite("ResourcesTank/Pickup_strip4.png"));
        sprites.put("rocket", getSprite("ResourcesTank/Rocket_strip60.png"));
        sprites.put("powerup", getSprite("ResourcesTank/powerup.png"));
        sprites.put("youwon", getSprite("ResourcesTank/youWin.png"));
    }

    public Image getSprite(String name) {
        URL url = TankWorld.class.getResource(name);
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
     * These functions GET things	* from the game world	*
     * ******************************
     */
    @Override
    public int getFrameNumber() {
        return clock.getFrame();
    }

    public int getTime() {
        return clock.getTime();
    }

    /**
     *
     * @param theObject
     */
    public void removeClockObserver(Observer theObject) {
        clock.deleteObserver(theObject);
    }

    public ListIterator<BackgroundObject> getBackgroundObjects() {
        return background.listIterator();
    }
//    

    public ListIterator<PlayerShip> getPlayers() {
        return playersInPlay.listIterator();
    }
//    

    public ListIterator<Bullet> getBullets() {
        return bullets.listIterator();
    }

    public int countPlayers() {
        return mPlayers.size();
    }

//    public void setDimensions(int w, int h) {
//        this.sizeX = w;
//        this.sizeY = h;
//    }
    /**
     * ******************************
     * These functions ADD things	* to the game world	*
     * ******************************
     */
    public void addBullet(Bullet... newObjects) {
        for (Bullet bullet : newObjects) {
            bullets.add(bullet);
        }
    }
//    

    public void addPlayer(PlayerShip... newObjects) {
        for (PlayerShip player : newObjects) {
            mPlayers.add(player);
            playersInPlay.add(player);
            ui.add(new InfoBar(player, mPlayers.size()));
        }
        mScoreInfo = new ScoreInfo(newObjects);
    }

    // add background items (islands)
    public void addBackground(BackgroundObject newObjects) {
//        for (BackgroundObject object : newObjects) {
        background.add(newObjects);
//        }
    }

//    // add power ups to the game world
    public void addPowerUp(Ship powerup) {
        powerups.add(powerup);
    }
//    

    public void addRandomPowerUp() {
        if (generator.nextInt(10) % 2 == 0) {
            Point p = new Point();
            PowerUp pu;
            BufferedImage biPickupImage = ImageUtils.toBufferedImage(sprites.get("pickups"));
            int pickupIndex = generator.nextInt(4);
            Image pickupImage = biPickupImage.getSubimage(pickupIndex * 32, 0, 32, 32);

            while (true) {
                p.x = generator.nextInt(sizeX);
                p.y = generator.nextInt(sizeY);

                pu = new PowerUp(p, 1, pickupImage);
                if (pu.checkForCollision(background)) {
                    continue;
                }
                if (pu.checkForCollision(mPlayers)) {
                    continue;
                }
                if (pu.checkForCollision(powerups)) {
                    continue;
                }
                break;
            }

            switch (pickupIndex) {
                case 2:
                    pu.setWeapon(new WeaponShield(10, 10));
                    break;
                case 1:
                    pu.setWeapon(new WeaponBounceMissile(10, 10));
                    break;
                case 0:
                    pu.setWeapon(new WeaponRocket(20, 10));
                    break;
                default:
                    pu.setWeapon(new SimpleWeapon(10, 10));
            }

            powerups.add(pu);
        }
    }

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

//        if (menu.isWaiting()) {
//            menu.draw(g2);
//        } else 
        if (!gameFinished) {
            // remove stray enemy bullets and draw
            Bullet[] collidingBullets = new Bullet[2];
            iterator = getBullets();
            while (iterator.hasNext()) {
                Bullet bullet = (Bullet) iterator.next();
                ListIterator<PlayerShip> players2 = getPlayers();
                boolean collision = false;
                while (players2.hasNext()) {
                    PlayerShip player = players2.next();
                    if (collision = bullet.collision(player) && bullet.getOwner() != player) {
                        player.damage(bullet.getStrength());
                        if (!player.show) {
                            bullet.getOwner().incrementScore(1);
                            player.show = true;
                        }
                        addBackground(new SmallExplosion(bullet.getLocationPoint()));
                        iterator.remove();
                        if (player.isDead()) {
                            players2.remove();
                            if (playersInPlay.size() == 0) {
                                gameOver = true;
                            }
                        }
                        break;
                    }
                }
                if (collision) {
                    break;
                }
                ListIterator<BackgroundObject> backgroundObjects = getBackgroundObjects();
                while (backgroundObjects.hasNext()) {
                    Object bkg = backgroundObjects.next();
                    Wall wall;
                    if (bkg instanceof Wall) {
                        wall = (Wall) bkg;
                    } else {
                        continue;
                    }
                    if (collision = bullet.collision(wall)) {
                        addBackground(new SmallExplosion(bullet.getLocationPoint()));
                        iterator.remove();
                        if (wall.isBreakable()) {
                            try {
                                wall.show = false;
                                Method m = TankWorld.class.getMethod("addBackground", BackgroundObject.class);
                                SchedulingObject so = new SchedulingObject(wall, m, getFrameNumber() + 600);
                                objectManager.addObject(so);
                            } catch (NoSuchMethodException ex) {
                                Logger.getLogger(TankWorld.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SecurityException ex) {
                                Logger.getLogger(TankWorld.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    }
                }
                if (collision) {
                    break;
                }

                ListIterator<Bullet> bullets = getBullets();
                while (bullets.hasNext()) {
                    Bullet b = bullets.next();
                    if (collision = b.collision(bullet) && b != bullet) {
                        addBackground(new SmallExplosion(bullet.getLocationPoint()));
                        collidingBullets[0] = b;
                        collidingBullets[1] = bullet;
                        break;
                    }
                }
                if (collision) {
//                    iterator.remove();
                    break;
                }

                if (bullet.getY() > h + 10 || bullet.getY() < -10) {
                    iterator.remove();
                }
                bullet.update(w, h);
                bullet.draw(g2, this);
            }
            for (Bullet collidingBullet : collidingBullets) {
                bullets.remove(collidingBullet);
            }
//            // update players and draw

            iterator = getPlayers();
            while (iterator.hasNext()) {
                PlayerShip player = (PlayerShip) iterator.next();
                boolean collision = false;
                ListIterator<BackgroundObject> backgroundObjects = getBackgroundObjects();
                while (backgroundObjects.hasNext()) {
                    Object background = backgroundObjects.next();
                    Wall wall = null;
                    if (background instanceof Wall) {
                        wall = (Wall) background;
                    } else {
                        continue;
                    }
                    if (collision = player.collision(wall)) {
                        break;
                    }
                }
                if (!collision) {
                    player.update(w, h);
                } else {
                    player.bounce(w, h);
                }
                player.draw(g2, this);
            }
//            
//            // powerups
            iterator = powerups.listIterator();
            while (iterator.hasNext()) {
                Ship powerup = (Ship) iterator.next();
                ListIterator<PlayerShip> players = getPlayers();
                while (players.hasNext()) {
                    PlayerShip player = players.next();
                    if (powerup.collision(player)) {
                        AbstractWeapon weapon = powerup.getWeapon();
                        player.setWeapon(weapon);
                        powerup.die();
                        iterator.remove();
                    }
                }
                powerup.draw(g2, this);
            }
            if (powerups.size() < 10 && getFrameNumber() % 200 == 0) {
                addRandomPowerUp();
            }
//            
//            // interface stuff
            iterator = ui.listIterator();
            while (iterator.hasNext()) {
                InterfaceObject object = (InterfaceObject) iterator.next();
                object.draw(g2);
            }
        } // end game stuff
        else {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Calibri", Font.PLAIN, 24));
            if (!gameWon) {
                g2.drawImage(sprites.get("gameover"), w / 3 - 50, h / 2, null);
            } else {
                g2.drawImage(sprites.get("youwon"), sizeX / 3, 100, null);
            }
            g2.drawString("Score", sizeX / 3, 400);
            int i = 1;
            for (PlayerShip player : mPlayers) {
                g2.drawString("sss" + ": " + Integer.toString(player.getScore()), sizeX / 3, 375 + 50 * i);
                i++;
            }
        }

    }

    public ArrayList<Ship> getPowerups() {
        return powerups;
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
        if (!mPlayers.isEmpty()) {
            clock.tick();
        }
        Dimension windowSize = getWindowSize();
//        Graphics2D g2 = createGraphics2D(windowSize.width, windowSize.height);
        Graphics2D g2 = createGraphics2D(sizeX, sizeY);
        drawFrame(sizeX, sizeY, g2);
        g2.dispose();
//        g.drawImage(bimg, 0, 0, this);
        Point loc = getPlayerWindowLocation(mPlayers.get(0), windowSize);
        BufferedImage left = bimg.getSubimage(loc.x, loc.y, windowSize.width / 2, windowSize.height);
        loc = getPlayerWindowLocation(mPlayers.get(1), windowSize);
        BufferedImage right = bimg.getSubimage(loc.x, loc.y, windowSize.width / 2, windowSize.height);
        g.drawImage(left, 0, 0, this);
        g.drawImage(right, windowSize.width / 2, 0, this);

        Image mini = bimg.getScaledInstance(this.getHeight() / 5,
                bimg.getWidth() * this.getHeight() / (5 * bimg.getHeight()),
                BufferedImage.SCALE_DEFAULT);

        g.drawImage(mini, this.getWidth() / 2 - mini.getWidth(observer) / 2, this.getHeight() - mini.getHeight(observer), this);
        mScoreInfo.draw(g);
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
                thread.sleep(23); // pause a little to slow things down
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

    public boolean isGameOver() {
        return gameOver;
    }

    // signal that we can stop entering the game loop
    public void finishGame() {
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

        HashMap<String, ArrayList<Integer[]>> configFile = 
                Utils.getBattlefildDataFromConfigFile(battlefieldconfig, getClass());
        if (configFile.isEmpty()) {
            return;
        }
        Image wall1 = sprites.get("wall1");
        Image wall2 = sprites.get("wall2");
        sizeX = configFile.get("bf").get(0)[0];
        sizeY = configFile.get("bf").get(0)[1];
        int tileSize = configFile.get("bf").get(0)[2];
        addBackground(new Background(sizeX, sizeY, noSpeed, sprites.get("background")));

        for (Integer[] vals : configFile.get("w")) {
            addBackground(new Wall(vals[0], vals[1], new Point(vals[2] * tileSize, vals[3] * tileSize), wall1, false));
        }

        for (Integer[] vals : configFile.get("bw")) {

            int NumberX = (int) (vals[0] / tileSize);
            int NumberY = (int) (vals[1] / tileSize);

            for (int i = 0; i < NumberY; i++) {
                for (int j = 0; j < NumberX; j++) {
                    addBackground(
                            new Wall(tileSize, tileSize,
                                    new Point((vals[2] + j) * tileSize, (vals[3] + i) * tileSize),
                                    wall2, true));
                }
            }

        }
    }

    private Point getPlayerWindowLocation(PlayerShip playerTank, Dimension d) {
        Point p = new Point(playerTank.getLocationPoint());
        p.translate(-d.width / 4, -d.height / 2);
        p.x = p.x < 0 ? 0 : p.x;
        p.x = p.x > bimg.getWidth() - d.width / 2 ? bimg.getWidth() - d.width / 2 : p.x;
        p.y = p.y < 0 ? 0 : p.y;
        p.y = p.y > bimg.getHeight() - d.height ? bimg.getHeight() - d.height : p.y;
        return p;
    }

    @Override
    public void setKeyListener(AbsInputController aThis) {
        this.addKeyListener(aThis);
    }
}
