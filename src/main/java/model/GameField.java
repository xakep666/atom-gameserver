package model;

import model.entities.BushEntity;
import model.entities.CellEntity;
import model.entities.FoodEntity;
import model.entities.GameEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by xakep666 on 05.10.16.
 *
 * Provides a game field
 */
public class GameField {
    public static double SIZE_X = 500;
    public static double SIZE_Y = 500;
    private static Logger log = LogManager.getLogger(GameField.class);
    private static List<Color> foodColors = new ArrayList<>();
    private static List<Color> playerColors = new ArrayList<>();


    static {
        //food colors init ----
        foodColors.add(Color.RED);
        foodColors.add(Color.YELLOW);
        foodColors.add(Color.GREEN);
        foodColors.add(Color.CYAN);
        //---------------------
        //player colors init
        playerColors.add(Color.RED);
        playerColors.add(Color.YELLOW);
        playerColors.add(Color.GREEN);
        playerColors.add(Color.BLACK);
        playerColors.add(Color.CYAN);
        //---------------------
    }

    private List<GameEntity> entities = new ArrayList<>();

    public GameField() {

        if (log.isDebugEnabled()) {
            log.debug("Created game field \""+this+"\"");
        }
    }

    /**
     * Randomly generates color using list
     * @param colors list of colors
     * @return generated color
     */
    private static Color generateColor(List<Color> colors) {
        return colors.get((int)Math.round(Math.random()*(colors.size()-1)));
    }

    /**
     * Generates coordinate on field in free region
     * @param radius radius of entity
     * @return coordinate on field
     */
    @NotNull
    private Point2D.Double genCenterCoordinate(double radius) {
        Point2D.Double center;
        do {
            double x = Math.random()*SIZE_X;
            double y = Math.random()*SIZE_Y;
            center = new Point2D.Double(x,y);
        } while (findNearby(center,10).size()!=0);
        return center;
    }

    /**
     * Randomly generates radius of entity
     * @param min minimal radius
     * @param max maximal radius
     * @return generated radius
     */
    private static double generateRadius(double min, double max) {
        return Math.random()*(max-min)+min;
    }

    /**
     * Spawns a green bush on free area
     */
    public void spawnBush() {
        double radius = generateRadius(BushEntity.minRadius,BushEntity.maxRadius);
        Point2D.Double center = genCenterCoordinate(radius);
        entities.add(new BushEntity(center,radius,this));
    }

    /**
     * Spawns food particle on free area
     */
    public void spawnFood() {
        double radius = FoodEntity.radius;
        Point2D.Double center = genCenterCoordinate(radius);
        Color color = generateColor(foodColors);
        entities.add(new FoodEntity(center,radius,color,this));
    }

    /**
     * Spawns player controlled cell on free area
     * @param player player, who will controll cell
     */
    public void spawnPlayerCell(@NotNull Player player) {
        double radius = CellEntity.minRadius;
        Point2D.Double center = genCenterCoordinate(radius);
        Color color = generateColor(playerColors);
        entities.add(new CellEntity(center,radius,color,player,this));
    }

    /**
     * Splits player controlled cells
     * @param player player, who controls cells
     * @param numchild number of children cells
     */
    public void splitPlayerCells(@NotNull Player player, int numchild) {
        List<CellEntity> newCells = new LinkedList<>();
        entities.forEach(e -> {
            if (e instanceof CellEntity && ((CellEntity)e).getOwner().equals(player)) {
                Collections.copy(newCells,((CellEntity)e).split(numchild));
            }
        });
        entities.removeIf(e -> (e instanceof CellEntity) && ((CellEntity)e).getOwner().equals(player));
        entities.addAll(newCells);
    }

    /**
     * Finds nearby located objects (difference between distance and sum of radiuses less or equal than given value).
     * @param entity object to check
     * @param distance distance to check
     * @return list of collided object
     */
    public List<GameEntity> findNearby(GameEntity entity, double distance) {
        List<GameEntity> nearby = new ArrayList<>();
        entities.sort((GameEntity e1,GameEntity e2) -> {
            double lb1 = e1.getCenterCoordinate().getX()-e1.getRadius();
            double lb2 = e2.getCenterCoordinate().getX()-e2.getRadius();
            if (lb1<lb2) return -1;
            if (lb1>lb2) return 1;
            return 0;
        });
        entities.forEach(e -> {
            //do actual check if right border of given entity greater then left border of checking particle
            if (entity.getCenterCoordinate().getX() + entity.getRadius() + distance>
                    e.getCenterCoordinate().getX() - e.getRadius())
                if (Math.abs(e.getCenterCoordinate().distance(entity.getCenterCoordinate()) -
                        e.getRadius()+entity.getRadius()) <= distance) {
                    nearby.add(e);
                }
        });
        return nearby;
    }

    /**
     * Finds nearby located objects (difference between distance and radius less or equal then given value)
     * @param coordinate coordinate to check
     * @param distance distance to check
     * @return list of nearby located objects
     */
    public List<GameEntity> findNearby(Point2D.Double coordinate, double distance) {
        List<GameEntity> nearby = new LinkedList<>();
        entities.forEach(e -> {
            if (coordinate.getX() + distance > e.getCenterCoordinate().getX() - e.getRadius())
                if (Math.abs(e.getCenterCoordinate().distance(coordinate)-e.getRadius())<=distance)
                    nearby.add(e);
        });
        return nearby;
    }
    /**
     * @return list of player controlled cells
     */
    public List<CellEntity> getPlayerCells() {
        List<CellEntity> ret = new ArrayList<>();
        entities.forEach(e -> {
            if (e instanceof CellEntity)
                ret.add((CellEntity)e);
        });
        return ret;
    }

    /**
     * @return all non player controlled entities
     */
    public List<GameEntity> getNonPlayerEntities() {
        List<GameEntity> ret = new ArrayList<>();
        entities.forEach(e -> {
            if (!(e instanceof CellEntity))
                ret.add(e);
        });
        return ret;
    }

    /**
     * Removes object from field
     * @param entity object to remove
     */
    public void removeEntity(@NotNull GameEntity entity) {
        entities.removeIf(e -> e.equals(entity));
    }

    /**
     * Removes all cells controlled by given player
     * @param player player which cells will be removed
     */
    public void removePlayerCells(@NotNull Player player) {
        entities.removeIf(e -> (e instanceof CellEntity) && ((CellEntity)e).getOwner().equals(player));
    }
}
