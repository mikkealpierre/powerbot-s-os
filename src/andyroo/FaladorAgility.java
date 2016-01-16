package andyroo;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "Fally Agility", properties = "author=andyroo; topic=1298690; client=4;",
        description = "Fally agility"
)

public class FaladorAgility extends PollingScript<ClientContext> implements PaintListener {

    /***********************************
     * Constants
     ******************************************/

    static final Tile START_TILE = new Tile(3036, 3340, 0);

    static final Tile OBSTACLE2_TILE = new Tile(3050, 3349, 3);
    static final Tile OBSTACLE5_TILE = new Tile(3035, 3361, 3);
    static final Tile OBSTACLE8_TILE = new Tile(3017, 3345, 3);
    static final Tile OBSTACLE10_TILE = new Tile(3013, 3335, 3);
    static final Tile OBSTACLE11_TILE = new Tile(3018, 3333, 3);
    static final Tile OBSTACLE12_TILE = new Tile(3029, 3333, 3);

    static final Area START_AREA = new Area(new Tile(3033, 3337, 0), new Tile(3041, 3346, 0));
    static final Area OBSTACLE_AREA_1 = new Area(new Tile(3035, 3344, 3), new Tile(3041, 3341, 3));
    static final Area OBSTACLE_AREA_2 = new Area(new Tile(3052, 3341, 3), new Tile(3044, 3350, 3));
    static final Area OBSTACLE_AREA_3 = new Area(new Tile(3047, 3357, 3), new Tile(3051, 3359, 3));
    static final Area OBSTACLE_AREA_4 = new Area(new Tile(3044, 3361, 3), new Tile(3049, 3368, 3));
    static final Area OBSTACLE_AREA_5 = new Area(new Tile(3042, 3365, 3), new Tile(3033, 3360, 3));
    static final Area OBSTACLE_AREA_6 = new Area(new Tile(3025, 3351, 3), new Tile(3030, 3355, 3));
    static final Area OBSTACLE_AREA_7 = new Area(new Tile(3008, 3352, 3), new Tile(3022, 3359, 3));
    static final Area OBSTACLE_AREA_8 = new Area(new Tile(3015, 3342, 3), new Tile(3023, 3350, 3));
    static final Area OBSTACLE_AREA_9 = new Area(new Tile(3010, 3343, 3), new Tile(3015, 3347, 3));
    static final Area OBSTACLE_AREA_10 = new Area(new Tile(3008, 3334, 3), new Tile(3014, 3343, 3));
    static final Area OBSTACLE_AREA_11 = new Area(new Tile(3010, 3330, 3), new Tile(3018, 3335, 3));
    static final Area OBSTACLE_AREA_12 = new Area(new Tile(3018, 3331, 3), new Tile(3025, 3336, 3));

    static final int ROUGH_WALL_ID = 10833;
    static final int TIGHTROPE1_ID = 10834;
    static final int HANDHOLDS_ID = 10836;
    static final int GAP1_ID = 11161;
    static final int GAP2_ID = 11360;
    static final int TIGHTROPE2_ID = 11361;
    static final int TIGHTROPE3_ID = 11364;
    static final int GAP3_ID = 11365;
    static final int LEDGE1_ID = 11366;
    static final int LEDGE2_ID = 11367;
    static final int LEDGE3_ID = 11368;
    static final int LEDGE4_ID = 11370;
    static final int EDGE1_ID = 11371;

    static final int[] ROUGH_WALL_BOUNDS = {16, 96, -192, -68, 100, 140};
    static final int[] TIGHTROPE1_BOUNDS = {64, 120, 28, 68, 44, 128};
    static final int[] HANDHOLDS_BOUNDS = {-28, 28, -12, 60, 60, 120};
    static final int[] TIGHTROPE2_BOUNDS = {-68, 4, 0, 76, -4, 72};
    static final int[] TIGHTROPE3_BOUNDS = {0, 60, -68, 8, 44, 116};

    /******************************************************************************************/

    private Area currentArea;
    static private Timer timer = new Timer();
    int startXP;

    /******************************************************************************************/


    static public void writeln(String s) {
        System.out.println(s);
    }


    @Override
    public void start() {
        timer.schedule(new AntibanTask(), 0, 5000);
        startXP = ctx.skills.experience(16);
    }


    public void stop() {
        System.out.println("Starting XP: " + startXP);
        System.out.println("Ending XP: " + ctx.skills.experience(16));
        System.out.println(ctx.skills.experience(16) - startXP);
        timer.cancel();
    }


    // check for marks of grace
    private void markCheck() {
        GroundItem mark = ctx.groundItems.select(10).id(11849).poll();

        if (currentArea != null && currentArea.contains(mark.tile())) {
            writeln("mark found");
            if (mark.inViewport()) {
                mark.click("Take");
                waitMovement(ctx);
            } else if (mark.valid()) {
                writeln("attempt to pick up mark");
                ctx.movement.step(mark.tile());
            }
        }
    }


    static public void adjustCamera(ClientContext ctx) {
        Camera cam = ctx.camera;

        if (Random.nextInt(0, 10) == 0)
            cam.pitch(Random.nextInt(50, 100));
        else {
            cam.angle(Random.nextGaussian(0, 360, cam.yaw(), 40));
            cam.pitch(true);
        }
    }


    class AntibanTask extends TimerTask {
        public void run() {
            int roll = Random.nextInt(0, 2);
            if(ctx.game.loggedIn()) {
                switch (roll) {
                    case 0:
                        writeln("antiban 0");
                        adjustCamera(ctx);
                        break;
                    case 1:
                        writeln("antiban 1");

                        if (ctx.game.tab() == Game.Tab.INVENTORY) {
                            ctx.game.tab(Game.Tab.STATS);
                        } else ctx.game.tab(Game.Tab.STATS);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    static public void waitObstacle(final ClientContext ctx) {
        Condition.wait(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Player me = ctx.players.local();
                //writeln("waiting to animate");
                return me.animation() != -1;
            }
        }, 250, 10);
        Condition.wait(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Player me = ctx.players.local();
                //writeln("waiting to stop moving");
                return !me.inMotion();
            }
        }, 250, 8);
    }

    static public void waitMovement(final ClientContext ctx) {
        Condition.wait(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Player me = ctx.players.local();
                //writeln("waiting to move");
                return me.inMotion();
            }
        }, 250, 10);
        Condition.wait(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Player me = ctx.players.local();
                //writeln("waiting to stop moving");
                return !me.inMotion();
            }
        }, 250, 8);
    }

    @Override
    public void repaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        int x = (int) ctx.input.getLocation().getX();
        int y = (int) ctx.input.getLocation().getY();

        g.setColor(new Color(255, 255, 255));
        g.drawLine(x - 5, y, x + 5, y);
        g.drawLine(x, y - 5, x, y + 5);
    }


    public void poll() {
        State s;
        markCheck();

        switch (s = state()) {
            case LOST:
                ctx.movement.step(START_TILE);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return START_TILE.distanceTo(ctx.players.local()) < 5;
                    }
                }, 300, 10);
                break;

            case START_POINT:
                GameObject roughWall = ctx.objects.select(10).id(ROUGH_WALL_ID).poll();

                if (roughWall.inViewport()) {
                    if(Random.nextInt(0, 2) == 0)
                        ctx.camera.angle(0);
                    else ctx.camera.angle(180);
                    roughWall.bounds(ROUGH_WALL_BOUNDS);
                    writeln("rough wall found");
                    roughWall.click("Climb");
                    //roughWall.hover();

                    // if fail, 1. point north/south 2. camera angle down
                }
                break;

            case OBSTACLE1:
                GameObject tightrope1 = ctx.objects.select(5).id(TIGHTROPE1_ID).poll();

                if (tightrope1.inViewport()) {
                    tightrope1.bounds(TIGHTROPE1_BOUNDS);
                    writeln("tightrope found");
                    //tightrope1.hover();
                    tightrope1.click("Cross");
                }
                break;

            case OBSTACLE2:
                GameObject handholds = ctx.objects.select(5).id(HANDHOLDS_ID).poll();

                if (handholds.inViewport()) {
                    handholds.bounds(HANDHOLDS_BOUNDS);
                    writeln("handholds found");
                    // handholds.hover();
                    handholds.click("Cross");
                } else {
                    ctx.movement.step(OBSTACLE2_TILE);

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return OBSTACLE2_TILE.distanceTo(ctx.players.local()) < 3;
                        }
                    }, 250, 10);
                }
                break;

            case OBSTACLE3:
                GameObject gap1 = ctx.objects.select(5).id(GAP1_ID).poll();

                if (gap1.inViewport()) {
                    //gap1.bounds(GAP1_BOUNDS);
                    writeln("gap found");
                    //gap1.hover();
                    gap1.click("Jump");
                }
                break;

            case OBSTACLE4:
                GameObject gap2 = ctx.objects.select(5).id(GAP2_ID).poll();

                if (gap2.inViewport()) {
                    //gap2.bounds(GAP2_BOUNDS);
                    writeln("gap found");
                    //gap2.hover();
                    gap2.click("Jump");
                }
                break;

            case OBSTACLE5:
                GameObject tightrope2 = ctx.objects.select(5).id(TIGHTROPE2_ID).poll();

                if (tightrope2.inViewport()) {
                    tightrope2.bounds(TIGHTROPE2_BOUNDS);
                    writeln("tightrope found");
                    //tightrope2.hover();
                    tightrope2.click("Cross");
                } else {
                    ctx.movement.step(OBSTACLE5_TILE);

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return OBSTACLE5_TILE.distanceTo(ctx.players.local()) < 3;
                        }
                    }, 250, 10);
                }
                break;

            case OBSTACLE6:
                GameObject tightrope3 = ctx.objects.select(5).id(TIGHTROPE3_ID).poll();

                if (tightrope3.inViewport()) {
                    tightrope3.bounds(TIGHTROPE3_BOUNDS);
                    writeln("tightrope found");
                    //tightrope3.hover();
                    tightrope3.click("Cross");
                }
                break;

            case OBSTACLE7:
                GameObject gap3 = ctx.objects.select(5).id(GAP3_ID).poll();

                if (gap3.inViewport()) {
                    //gap3.bounds(GAP3_BOUNDS);
                    writeln("gap found");
                    //gap3.hover();
                    gap3.click("Jump");
                }
                break;

            case OBSTACLE8:
                GameObject ledge1 = ctx.objects.select(5).id(LEDGE1_ID).poll();

                if (ledge1.inViewport()) {
                    //ledge1.bounds(LEDGE1_BOUNDS);
                    writeln("ledge found");
                    //ledge1.hover();
                    ledge1.click("Jump");
                } else {
                    ctx.movement.step(OBSTACLE8_TILE);

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().tile().compareTo(OBSTACLE8_TILE) == 0;
                        }
                    }, 250, 10);
                }
                break;

            case OBSTACLE9:
                GameObject ledge2 = ctx.objects.select(5).id(LEDGE2_ID).poll();

                if (ledge2.inViewport()) {
                    //ledge2.bounds(LEDGE2_BOUNDS);
                    writeln("ledge found");
                    //ledge2.hover();
                    ledge2.click("Jump");
                }
                break;

            case OBSTACLE10:
                GameObject ledge3 = ctx.objects.select(5).id(LEDGE3_ID).poll();

                if (ledge3.inViewport()) {
                    //ledge3.bounds(LEDGE3_BOUNDS);
                    writeln("ledge found");
                    //ledge3.hover();
                    ledge3.click("Jump");
                } else {
                    ctx.movement.step(OBSTACLE10_TILE);

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().tile().compareTo(OBSTACLE10_TILE) == 0;
                        }
                    }, 250, 10);
                }
                break;

            case OBSTACLE11:
                GameObject ledge4 = ctx.objects.select(5).id(LEDGE4_ID).poll();

                if (ledge4.inViewport()) {
                    //ledge4.bounds(LEDGE4_BOUNDS);
                    writeln("ledge found");
                    //ledge4.hover();
                    ledge4.click("Jump");
                } else {
                    ctx.movement.step(OBSTACLE11_TILE);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().tile().compareTo(OBSTACLE11_TILE) == 0;
                        }
                    }, 250, 10);
                }
                break;

            case OBSTACLE12:
                GameObject edge1 = ctx.objects.select(5).id(EDGE1_ID).poll();

                if (edge1.inViewport()) {
                    //edge1.bounds(EDGE1_BOUNDS);
                    writeln("ledge found");
                    //edge1.hover();
                    edge1.click("Jump");
                } else {
                    ctx.movement.step(OBSTACLE12_TILE);

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().tile().compareTo(OBSTACLE12_TILE) == 0;
                        }
                    }, 250, 10);
                }
                break;
            default:
                break;
        }
        if(s.ordinal() < 13) {
            waitObstacle(ctx);
        }
    }


    private State state() {
        if(!ctx.game.loggedIn()) {
            return State.INVALID;
        }

        Player me = ctx.players.local();

        if (START_AREA.contains(me.tile())) {
            currentArea = START_AREA;
            writeln("On the ground");
            return State.START_POINT;
        } else if (me.tile().floor() == 0) {
            return State.LOST;
        } else if (OBSTACLE_AREA_1.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_1;
            writeln("Obstacle 1");
            return State.OBSTACLE1;
        } else if (OBSTACLE_AREA_2.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_2;
            writeln("Obstacle 2");
            return State.OBSTACLE2;
        } else if (OBSTACLE_AREA_3.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_3;
            writeln("Obstacle 3");
            return State.OBSTACLE3;
        } else if (OBSTACLE_AREA_4.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_4;
            writeln("Obstacle 4");
            return State.OBSTACLE4;
        } else if (OBSTACLE_AREA_5.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_5;
            writeln("Obstacle 5");
            return State.OBSTACLE5;
        } else if (OBSTACLE_AREA_6.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_6;
            writeln("Obstacle 6");
            return State.OBSTACLE6;
        } else if (OBSTACLE_AREA_7.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_7;
            writeln("Obstacle 7");
            return State.OBSTACLE7;
        } else if (OBSTACLE_AREA_8.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_8;
            writeln("Obstacle 8");
            return State.OBSTACLE8;
        } else if (OBSTACLE_AREA_9.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_9;
            writeln("Obstacle 9");
            return State.OBSTACLE9;
        } else if (OBSTACLE_AREA_10.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_10;
            writeln("Obstacle 10");
            return State.OBSTACLE10;
        } else if (OBSTACLE_AREA_11.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_11;
            writeln("Obstacle 11");
            return State.OBSTACLE11;
        } else if (OBSTACLE_AREA_12.contains(me.tile())) {
            currentArea = OBSTACLE_AREA_12;
            writeln("Obstacle 12");
            return State.OBSTACLE12;
        } else {
            writeln("INVALID STATE");
            return State.INVALID;
        }
    }


    private enum State {
        START_POINT, OBSTACLE1, OBSTACLE2, OBSTACLE3, OBSTACLE4, OBSTACLE5,
        OBSTACLE6, OBSTACLE7, OBSTACLE8, OBSTACLE9, OBSTACLE10, OBSTACLE11, OBSTACLE12, INVALID, LOST
    }
}
