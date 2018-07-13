package game.template.logic.cellfillers;

import game.template.graphics.Animation;
import game.template.graphics.MasterAnimation;
import game.template.logic.Map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ComputerTank extends Tank {
    protected boolean isMobile;
    private char type;
    protected UserTank enemyTank = null;
    private boolean temporarilyDisabled = true;
    private boolean doesCollisionDamageUserTank;
    private int damageInCaseCollisionIsDestructive = 2;
    private int deg;
    //here
    private BufferedImage gun;
    private int velocity;
    private int shootLatency;

    //constructor changed
    public ComputerTank(int y, int x, int health, Map whichMap, boolean doesCollisionDamageUserTank, String location, String gunLocation, String bulletLocation, boolean isMobile, int velocity, int shootLatency) {
        super(y, x, health, whichMap, location, bulletLocation);
        setAlive(true);
        this.doesCollisionDamageUserTank = doesCollisionDamageUserTank;
//        displayTheAnimations();
        //here
        animation = new Animation(images, 120, 120, 5, 20,
                false, locX, locY, 0);
        //here
        try {
            gun = ImageIO.read(new File(gunLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((Animation) getAnimation()).setGun(gun);
        //till here
//        if (whichMap.doesntGoOutOfMap(this, true, 0))
//            temporarilyDisabled = false;
        this.isMobile = isMobile;
        this.velocity = velocity;
        setVelocity(velocity);
        this.shootLatency = shootLatency;
        deg = 0;
    }

    //here
    public ComputerTank(int y, int x, int health, Map whichMap, boolean doesCollisionDamageUserTank, String location) {
        super(y, x, health, whichMap, location, ".\\Bullet\\HeavyBullet.png");
        this.doesCollisionDamageUserTank = doesCollisionDamageUserTank;
    }

    /**
     * Remember to update only objects that ARE visible.
     */

    public void finalizeMove() {
        if (deg != 0 && deg != 180) {
            if (locX > enemyTank.locX) {
                deg -= 5;
                ((Animation) animation).setMovingRotationDeg(deg);
            } else {
                deg += 5;
                ((Animation) animation).setMovingRotationDeg(deg);
            }
            setForward(false);
        } else {
            if (locX > enemyTank.locX) {
                setForward(false);
            } else {
                setForward(true);
            }
        }
    }

    public void move() {
        this.animation.active = true;
        deg %= 360;
        if (deg < 0) {
            deg = 360 + deg;
        }
        findEnemyTank();
        if (Math.abs(locX - enemyTank.locX) > 240) {
            int xSign = -(locX - enemyTank.locX) / Math.abs(locX - enemyTank.locX);
            int plusX = velocity * xSign;
            locX += plusX + avoidCollision() * xSign;
            animation.changeCoordinates(locX, locY);
            finalizeMove();
        } else if (Math.abs(locX - enemyTank.locX) < 240) {
            int xSign = (locX - enemyTank.locX) / Math.abs(locX - enemyTank.locX);
            int plusX = velocity * xSign;
            System.out.println(xSign);
            locX += plusX + avoidCollision() * xSign;
            animation.changeCoordinates(locX, locY);
            finalizeMove();
        } else if (Math.abs(locY - enemyTank.locY) > 240) {
            int ySign = -(locY - enemyTank.locY) / Math.abs(locY - enemyTank.locY);
            int plusY = velocity * ySign;
            locY += plusY + avoidCollision() * ySign;
            animation.changeCoordinates(locX, locY);
            finalizeMove();
        } else if (Math.abs(locY - enemyTank.locY) < 240) {
            int ySign = (locY - enemyTank.locY) / Math.abs(locY - enemyTank.locY);
            int plusY = velocity * ySign;
            locY += plusY + avoidCollision() * ySign;
            animation.changeCoordinates(locX, locY);
            finalizeMove();
        }
    }

    public void findEnemyTank() {
        if (enemyTank == null) {
            for (GameObject o : whichMap.getVisibleObjects()) {
                if (o instanceof UserTank)
                    enemyTank = (UserTank) o;
            }
        }
    }

    @Override
    public Bullet shoot(double deg) {
        findEnemyTank();
        return new Bullet(heavyBulletImage, (int) (locX + 67 + Math.cos(-deg) * 110),
                (int) (locY + 75 + Math.sin(-deg) * (110)), Math.cos(-deg), Math.sin(-deg), -deg, whichMap, 100);
    }


    @Override
    public void update() {
        animation.active = false;
        if (isMobile && validateAbility())
            move();
        if (!temporarilyDisabled) {
            //till here
            findEnemyTank();
            double deg = Math.atan2((-enemyTank.locY + locY), (enemyTank.locX - locX));
            ((Animation) animation).setCannonRotationDeg(-deg);
            Bullet bullet = null;
            long time = System.currentTimeMillis();
            if (lastShootTime == 0 || time - lastShootTime > shootLatency) {
                lastShootTime = time;
                bullet = shoot(deg);
            }
            if (bullet != null) {
                ((Animation) animation).getBullets().add(bullet);
            }

        }
        //shoot();
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (getHealth() < 0)
            setAlive(false);
    }

    protected boolean validateAbility() {
//        if (whichMap.doesntGoOutOfMap(this, true))
//            temporarilyDisabled = false;
//        else
//            temporarilyDisabled = true;
//        return !temporarilyDisabled;
        return true;
    }

    public boolean isDoesCollisionDamageUserTank() {
        return doesCollisionDamageUserTank;
    }

    @Override
    public int getDamage() {
        return damageInCaseCollisionIsDestructive;
    }

    @Override
    public MasterAnimation getAnimation() {
        return animation;
    }
}


//    int ySign = (int)(state.locY - enemyTank.state.locY) / Math.abs(state.locY - enemyTank.state.locY);
//            int plusY = 8 * ySign;
//            boolean isYDone = false;
//            while (!isYDone)
//            {
//                state.locY += plusY;
//                isYDone = true;
//                for (GameObject o: whichMap.getVisibleObjects())
//                {
//                    if (GameState.checkIfTwoObjectsCollide(this, o))
//                    {
//                        state.locY -= plusY;
//                        plusY -=2;
//                        isYDone = false;
//                        break;
//                    }
//                }
//                if (plusY == 0)
//                    isYDone = true;
//            }