package lazarusgame.game;

import gamengine.game.BackgroundObject;
import gamengine.game.GameObject;
import gamengine.game.MoveableObject;
import gamengine.game.Wall;
import gamengine.modifiers.AbstractGameModifier;
import gamengine.utils.Direction;
import gamengine.utils.ImageUtils;
import gamengine.utils.SoundPlayer;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import lazarusgame.LazarusWorld;
import lazarusgame.modifiers.motions.InputController;

public class Lazarus extends MoveableObject implements Observer {

    private int level;
    public int left = 0, right = 0, info = 0;
    private int frame;
    private int currFrameNum;
    private int timer;
    Image animationLeft[] = new Image[7];
    Image animationLeftJump[] = new Image[7];
    Image animationRight[] = new Image[7];
    Image animationRightJump[] = new Image[7];
    boolean animInProgress = false;
    boolean fallingDown = false;
    static BufferedImage lazarusImage = ImageUtils.toBufferedImage(LazarusWorld.sprites.get("lazarus"));
    public boolean posibleMoves[];
    private Method mCurrentAction;
    private boolean isAffraid = false;
    private SoundPlayer sound;

    public Lazarus(Point location) {
        super(location, new Point(0, 1), lazarusImage);
        frame = 0;
        timer = 0;
        level = 3;
        sound = new SoundPlayer(2);
        posibleMoves = new boolean[]{true,true,true,true};
        
        animationLeft = ImageUtils.spriteToImagesArray(LazarusWorld.sprites.get("Lazarus_left7"), 7, 80);
        animationLeftJump = ImageUtils.spriteToImagesArray(LazarusWorld.sprites.get("Lazarus_jump_left7"), 7, 80);
        animationRight = ImageUtils.spriteToImagesArray(LazarusWorld.sprites.get("Lazarus_right7"), 7, 80);
        animationRightJump = ImageUtils.spriteToImagesArray(LazarusWorld.sprites.get("Lazarus_jump_right7"), 7, 80);
    }

    @Override
    public void draw(Graphics g, ImageObserver observer) {
        super.draw(g, observer);
//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public void update(int w, int h) {

        if (animInProgress) {
            try {
                mCurrentAction.invoke(this);
                updateLevel(h);
                return;
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Lazarus.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Lazarus.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Lazarus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            if (left == 1) {
                int leftLevel = LazarusWorld.getInstance().getLeftRightColLevels(getColumn())[0];
                if (leftLevel == level || leftLevel < level) {
                    makeMoveLeft();
                    playMoveSound();
                    mCurrentAction = this.getClass().getMethod("makeMoveLeft");
                } else if (leftLevel == level + 1) {
                    makeJumpLeft();
                    playMoveSound();
                    mCurrentAction = this.getClass().getMethod("makeJumpLeft");
                }
                else
                    left = 0;
                updateLevel(h);
            } else if (right == 1) {
                int rightLevel = LazarusWorld.getInstance().getLeftRightColLevels(getColumn())[1];
                if (rightLevel == level || rightLevel < level) {
                    makeMoveRight();
                    playMoveSound();
                    mCurrentAction = this.getClass().getMethod("makeMoveRight");
                } else if (rightLevel == level + 1) {
                    makeJumpRight();
                    playMoveSound();
                    mCurrentAction = this.getClass().getMethod("makeJumpRight");
                }
                else
                    right=0;
                updateLevel(h);
            }
            move();
            if (info == 1) {
                printNeighbours();
            }
            

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Lazarus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Lazarus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void playMoveSound()
    {
        if (timer==1){
            URL url  = LazarusWorld.class.getResource("resources/Move.wav");
            SoundPlayer sound = new SoundPlayer(2, url);
            sound.play();
        }
    }

    private void printNeighbours() {
        int rightLevel = LazarusWorld.getInstance().getLeftRightColLevels(getColumn())[1];
        int leftLevel = LazarusWorld.getInstance().getLeftRightColLevels(getColumn())[0];

        String str = String.format("curent levels l %d|left %d|right %d:", level, leftLevel, rightLevel);
        System.out.println(str);
    }

    public int getColumn() {
        return (int) (location.getX() + 20) / 40;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void collide(GameObject otherObject) {
        Area a = getCollisionArea();
        a.intersect(otherObject.getCollisionArea());
//        a.get
    }

    public boolean[] checkForCollision(ListIterator<BackgroundObject> iterator, GameObject o) {
//        boolean collisionDetected = false;
        boolean ml = true, mr = true, md = true, mt = true;
        boolean[] res = new boolean[]{true,true,true,true};
        while (iterator.hasNext()) {
            GameObject otherObject = (GameObject) iterator.next();
            if (!(otherObject instanceof Wall)) {
                continue;
            }
            Wall w = (Wall) otherObject;
            
            if (w instanceof EndButton && w.collision(this)) {
                LazarusWorld.getInstance().nextLevel();
                URL url  = LazarusWorld.class.getResource("resources/Button.wav");
                sound.setUrl(url);
                sound.play();
                return res;
            }
            Area a = otherObject.getCollisionArea();
            boolean contains;
            if (w.getWallPosition() == Direction.LEFT) {
                ml &= !isCollidingOnEdge(a, Direction.LEFT);
                md &= !isCollidingOnEdge(a, Direction.DOWN);
            } else if (w.getWallPosition() == Direction.RIGHT) {
                md &= !isCollidingOnEdge(a, Direction.DOWN);
                mr &= !isCollidingOnEdge(a, Direction.RIGHT);
            } else if (w.getWallPosition() == Direction.DOWN) {
                md &= !isCollidingOnEdge(a, Direction.DOWN);
            } else if (w.getWallPosition() == Direction.UP) {
//                contains = a.contains(getEdgePoint(Direction.UP));
                mt &= !isCollidingOnEdge(a, Direction.UP) | animInProgress;
            }

        }
        
        setPossibleMoveInArray(Direction.LEFT, ml, res);
        setPossibleMoveInArray(Direction.RIGHT, mr, res);
        setPossibleMoveInArray(Direction.DOWN, md, res);
        setPossibleMoveInArray(Direction.UP, mt, res);
//        this.setLocation(new Point(location.x, otherObject.getY() - height - 1));
        return res;
    }

    public boolean isCollidingOnEdge(Area a, Direction d) {
//        boolean contains;
        return  a.contains(getEdgePoint(d));
//        return contains;
    }

    public boolean[] checkForBoxCollision(ListIterator<Box> iterator, GameObject o) {
        boolean ml = true, mr = true, md = true, mt = true;
        Box downCollidedBox = null;
        while (iterator.hasNext()) {
            Box box = (Box) iterator.next();
            if (!box.collision(this)) {
                continue;
            }

            Area a = box.getCollisionArea();
            boolean contains;
            contains = a.contains(getEdgePoint(Direction.LEFT));
            ml &= !contains;
            contains = a.contains(getEdgePoint(Direction.RIGHT));
            mr &= !contains;
            contains = a.contains(getEdgePoint(Direction.DOWN));
            if (contains) {
                downCollidedBox = box;
            }
            md &= !contains;
            contains = a.contains(getEdgePoint(Direction.UP));
            mt &= !contains | animInProgress;

        }
        boolean[] res = new boolean[]{true, true, true, true};

        if (ml) {
            int leftLevel = LazarusWorld.getInstance().getLeftRightColLevels((int) (location.getX() / 40))[0];
            if (leftLevel > level + 1) {
                setPossibleMoveInArray(Direction.LEFT, false, res);
            }
        } else {
            setPossibleMoveInArray(Direction.LEFT, ml, res);
        }
        if (mr) {
            int rightLevel = LazarusWorld.getInstance().getLeftRightColLevels((int) (location.getX() / 40))[1];
            if (rightLevel > level + 1) {
                setPossibleMoveInArray(Direction.RIGHT, false, res);
            }
        } else {
            setPossibleMoveInArray(Direction.RIGHT, mr, res);
        }
        setPossibleMoveInArray(Direction.DOWN, md, res);
//        if (!md && downCollidedBox != null) {
//            Point p = downCollidedBox.getLocationPoint();
//            setLocation(new Point(this.location.x, p.y - height));
//        }
        setPossibleMoveInArray(Direction.UP, mt, res);  //use for killing lazarus
        return res;
    }

    public Point getEdgePoint(Direction d) {
        Point point;
        Rectangle location = getCollisionArea().getBounds();
        switch (d) {
            case DOWN:
                point = new Point(location.x + 5, location.y + 1 + location.height);
                break;
            case UP:
                point = new Point(location.x + 5, location.y - 1);
                break;
            case RIGHT:
                point = new Point(location.x + 1 + location.width, location.y + 5);
                break;
            case LEFT:
                point = new Point(location.x - 1, location.y + 5);
                break;
            default:
                point = new Point();
                break;
        }
        return point;
    }

    @Override
    public Area getCollisionArea() {
        if (animInProgress) {
            Rectangle r = null;
            if (left == 1) {
                int offset = 40 * (frame - 1) / currFrameNum;
                r = new Rectangle((int) location.getCenterX(), (int) location.getCenterY(), 40, 40);
                r.x -= offset;
            } else if (right == 1) {
                int offset = 40 * (frame - 1) / currFrameNum;
                r = new Rectangle((int) location.x, (int) location.getCenterY(), 40, 40);
                r.x += offset;
            }
            if (r == null) {
                return new Area(location);
            } else {
                return new Area(r);
            }
        }
        return new Area(location);
    }

    public void makeMoveLeft() {
        timer++;
//        printTimer();
        currFrameNum = animationLeft.length;
        if (timer % 3 == 0) {

            if (frame < animationLeft.length) {
                setImage(animationLeft[frame]);
                if (!animInProgress) {
                    location.y -= 40;
                    location.x -= 40;
                    animInProgress = true;
                }
            } else {
                setImage(lazarusImage);
                resetValues();
                animInProgress = false;
                location.y += 40;
            }
            frame++;
        }
    }

    public void makeJumpLeft() {
        timer++;
//        printTimer();
        currFrameNum = animationLeftJump.length;
        if (timer % 3 == 0) {

            if (frame < animationLeftJump.length) {
                setImage(animationLeftJump[frame]);
                if (!animInProgress) {
                    location.y -= 40;
                    location.x -= 40;
                    animInProgress = true;
                }
            } else {
                setImage(lazarusImage);
                resetValues();
                animInProgress = false;
//                level++;
            }
            frame++;
        }
    }

    public void makeMoveRight() {
        timer++;
//        printTimer();
        currFrameNum = animationRight.length;
        if (timer % 3 == 0) {

            if (frame < animationRight.length) {
                setImage(animationRight[frame]);
                if (!animInProgress) {
                    location.y -= 40;
                    animInProgress = true;
                }
            } else {
                setImage(lazarusImage);
                resetValues();
                animInProgress = false;
                location.y += 40;
                location.x += 40;
            }
            frame++;
        }
    }

    public void makeJumpRight() {
        timer++;
//        printTimer();
        currFrameNum = animationRightJump.length;
        if (timer % 3 == 0) {

            if (frame < animationRightJump.length) {
                setImage(animationRightJump[frame]);
                if (!animInProgress) {
                    location.y -= 40;
                    animInProgress = true;
                }
            } else {
                setImage(lazarusImage);
                resetValues();
                animInProgress = false;
                location.x += 40;
//                level++;
            }
            frame++;
        }
    }

    void resetValues() {
        right = 0;
        frame = 0;
        timer = 0;
        left = 0;
    }

    public void die() {
        this.show = false;
        LazarusSquished squished = new LazarusSquished(new Point(location.x, location.y));
        LazarusWorld.getInstance().addBackground(squished);
        URL url  = LazarusWorld.class.getResource("resources/Squished.wav");
        sound.setUrl(url);
        sound.play();
    }

    public void affraid() {
        this.show = false;
        Point point = new Point(location.x, location.y);
        LazarusAffraid affraid = new LazarusAffraid(point);
        affraid.setLoop(true);
        LazarusWorld.getInstance().addBackground(affraid);
        isAffraid = true;
    }

    public boolean isAffraid() {
        return isAffraid;
    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    @Override
    public void move() {
        if (canLazarusMove(Direction.DOWN)) {
            super.move();
            fallingDown = true;
        } else {
            fallingDown = false;
        }
    }

    public boolean canLazarusMove(Direction direction) {
        return posibleMoves[direction.ordinal()];
    }

    public void setPossibleMoveInArray(Direction direction, boolean state, boolean[] posibleMoves) {
        posibleMoves[direction.ordinal()] = state;
    }

    public boolean canLazarusMove() {
        boolean canMove = false;
        for (Direction move : Direction.values()) {
            canMove |= posibleMoves[move.ordinal()];
        }
        return canMove;
    }

    @Override
    protected void setMotion() {
//        motion = new SimpleMotion(LazarusWorld.getInstance());
//        motion.addObserver(this);
        motion = new InputController(this);
        motion.addObserver(this);
    }

    @Override
    public boolean canChangeFields() {
        return !fallingDown && !animInProgress;
    }

    public void mergePossibleMoves(boolean[] possibleMoves1, boolean[] possibleMoves2) {
        for (int i = 0; i < 4; i++) {
            possibleMoves1[i] &= possibleMoves2[i];
        }
    }

//    public void initPossibleMoves() {
//        for (int i = 0; i < 4; i++) {
//            this.posibleMoves[i] = true;
//        }
//    }

    public void updateLevel(int h) {
        level = (h - location.y - 10) / lazarusImage.getHeight();

    }

    private void printLevel() {
        System.out.println("lazarus level = " + level);
    }

    private void printTimer() {
        System.out.println("lazarus timer = " + timer);
    }

    public void setPosibleMoves(boolean[] posibleMoves) {
        this.posibleMoves = posibleMoves;
    }
}
