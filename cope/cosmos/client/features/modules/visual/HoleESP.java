package cope.cosmos.client.features.modules.visual;

import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.util.render.RenderBuilder;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.system.Timer;
import cope.cosmos.util.world.BlockUtil;
import cope.cosmos.util.world.Hole;
import cope.cosmos.util.world.HoleUtil;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;

public class HoleESP extends Module {

    public static HoleESP INSTANCE;
    public static Setting range = new Setting("Range", "Range to scan for holes", Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(20.0D), 0);
    public static Setting main = new Setting("Main", "Visual style for the main render", RenderBuilder.Box.GLOW);
    public static Setting mainHeight = (new Setting("Height", "Height of the main render", Double.valueOf(-1.0D), Double.valueOf(1.0D), Double.valueOf(3.0D), 1)).setParent(HoleESP.main);
    public static Setting mainWidth = (new Setting(() -> {
        return Boolean.valueOf(((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.BOTH) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.CLAW) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.OUTLINE));
    }, "Width", "Line width of the main render", Double.valueOf(0.0D), Double.valueOf(1.5D), Double.valueOf(3.0D), 1)).setParent(HoleESP.main);
    public static Setting outline = new Setting("Outline", "Visual style for the outline render", RenderBuilder.Box.OUTLINE);
    public static Setting outlineHeight = (new Setting("Height", "Height of the outline render", Double.valueOf(-1.0D), Double.valueOf(0.0D), Double.valueOf(3.0D), 1)).setParent(HoleESP.outline);
    public static Setting outlineWidth = (new Setting(() -> {
        return Boolean.valueOf(((RenderBuilder.Box) HoleESP.outline.getValue()).equals(RenderBuilder.Box.BOTH) || ((RenderBuilder.Box) HoleESP.outline.getValue()).equals(RenderBuilder.Box.CLAW) || ((RenderBuilder.Box) HoleESP.outline.getValue()).equals(RenderBuilder.Box.OUTLINE));
    }, "Width", "Line width of the outline render", Double.valueOf(0.0D), Double.valueOf(1.5D), Double.valueOf(3.0D), 1)).setParent(HoleESP.outline);
    public static Setting depth = new Setting("Depth", "Enables vanilla depth", Boolean.valueOf(true));
    public static Setting doubles = new Setting("Doubles", "Considers double holes as safe holes", Boolean.valueOf(true));
    public static Setting voids = new Setting("Void", "Highlights void and roof holes", Boolean.valueOf(false));
    public static Setting colors = new Setting("Colors", "Colors for the rendering", Boolean.valueOf(true));
    public static Setting obsidianColor = (new Setting("Obsidian", "Color of the obsidian holes", new Color(144, 0, 255, 45))).setParent(HoleESP.colors);
    public static Setting bedrockColor = (new Setting("Bedrock", "Color of the bedrock holes", new Color(93, 235, 240, 45))).setParent(HoleESP.colors);
    public static Setting voidColor = (new Setting(() -> {
        return (Boolean) HoleESP.voids.getValue();
    }, "Void", "Color of the void holes", new Color(255, 0, 0, 45))).setParent(HoleESP.colors);
    public static Map holes = new HashMap();
    public static Timer holeTimer = new Timer();

    public HoleESP() {
        super("HoleESP", Category.VISUAL, "Highlights nearby safe holes", () -> {
            return Setting.formatEnum((Enum) HoleESP.main.getValue()) + ", " + Setting.formatEnum((Enum) HoleESP.outline.getValue());
        });
        HoleESP.INSTANCE = this;
    }

    public void onRender3d() {
        Iterator iterator = (new HashSet(HoleESP.holes.entrySet())).iterator();

        while (iterator.hasNext()) {
            Entry holeEntry = (Entry) iterator.next();

            this.renderHole((Hole) holeEntry.getKey(), (Color) holeEntry.getValue());
        }

    }

    public void renderHole(Hole hole, Color color) {
        if (hole.getType().equals(Hole.Type.VOID)) {
            RenderUtil.drawBox((new RenderBuilder()).position(hole.getHole()).color(color).box(RenderBuilder.Box.FILL).setup().line(1.5F).depth(true).blend().texture());
        } else {
            RenderUtil.drawBox((new RenderBuilder()).position(hole.getHole()).height(((Double) HoleESP.mainHeight.getValue()).doubleValue() - 1.0D).length(!hole.getType().equals(Hole.Type.DOUBLEBEDROCKX) && !hole.getType().equals(Hole.Type.DOUBLEOBSIDIANX) ? 0.0D : 1.0D).width(!hole.getType().equals(Hole.Type.DOUBLEBEDROCKZ) && !hole.getType().equals(Hole.Type.DOUBLEOBSIDIANZ) ? 0.0D : 1.0D).color(color).box((RenderBuilder.Box) HoleESP.main.getValue()).setup().line((float) ((Double) HoleESP.mainWidth.getValue()).doubleValue()).cull(((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.REVERSE)).shade(((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.REVERSE)).alpha(((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.REVERSE)).depth(((Boolean) HoleESP.depth.getValue()).booleanValue()).blend().texture());
            RenderUtil.drawBox((new RenderBuilder()).position(hole.getHole()).height(((Double) HoleESP.outlineHeight.getValue()).doubleValue() - 1.0D).length(!hole.getType().equals(Hole.Type.DOUBLEBEDROCKX) && !hole.getType().equals(Hole.Type.DOUBLEOBSIDIANX) ? 0.0D : 1.0D).width(!hole.getType().equals(Hole.Type.DOUBLEBEDROCKZ) && !hole.getType().equals(Hole.Type.DOUBLEOBSIDIANZ) ? 0.0D : 1.0D).color(color).box((RenderBuilder.Box) HoleESP.outline.getValue()).setup().line((float) ((Double) HoleESP.outlineWidth.getValue()).doubleValue()).cull(((RenderBuilder.Box) HoleESP.outline.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.REVERSE)).shade(((RenderBuilder.Box) HoleESP.outline.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.REVERSE)).alpha(((RenderBuilder.Box) HoleESP.outline.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) HoleESP.main.getValue()).equals(RenderBuilder.Box.REVERSE)).depth(((Boolean) HoleESP.depth.getValue()).booleanValue()).blend().texture());
        }

    }

    public static void addHole(Hole newHole, Color color) {
        boolean unique = true;
        Iterator iterator = HoleESP.holes.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry holeEntry = (Entry) iterator.next();

            if (newHole.getHole().equals(((Hole) holeEntry.getKey()).getHole())) {
                unique = false;
                break;
            }
        }

        if (unique) {
            HoleESP.holes.put(newHole, color);
        }

    }

    public void onThread() {
        Iterator potentialHoles;

        if (HoleESP.holeTimer.passed(1000L, Timer.Format.SYSTEM)) {
            potentialHoles = HoleESP.holes.entrySet().iterator();

            while (potentialHoles.hasNext()) {
                Entry potentialHole = (Entry) potentialHoles.next();

                if (HoleESP.mc.player.getDistanceSq(((Hole) potentialHole.getKey()).getHole()) >= Math.pow(((Double) HoleESP.range.getValue()).doubleValue(), 2.0D)) {
                    HoleESP.holes.remove(potentialHole.getKey());
                }

                if (!HoleUtil.isObsidianHole(((Hole) potentialHole.getKey()).getHole()) || !HoleUtil.isBedRockHole(((Hole) potentialHole.getKey()).getHole()) || !HoleUtil.isDoubleObsidianHoleX(((Hole) potentialHole.getKey()).getHole()) || !HoleUtil.isDoubleObsidianHoleZ(((Hole) potentialHole.getKey()).getHole()) || !HoleUtil.isDoubleBedrockHoleX(((Hole) potentialHole.getKey()).getHole()) || !HoleUtil.isDoubleBedrockHoleZ(((Hole) potentialHole.getKey()).getHole())) {
                    HoleESP.holes.remove(potentialHole.getKey());
                }
            }

            HoleESP.holeTimer.reset();
        }

        potentialHoles = BlockUtil.getNearbyBlocks(HoleESP.mc.player, ((Double) HoleESP.range.getValue()).doubleValue(), false);

        while (potentialHoles.hasNext()) {
            BlockPos potentialHole1 = (BlockPos) potentialHoles.next();

            if (HoleUtil.isVoidHole(potentialHole1.down()) && ((Boolean) HoleESP.voids.getValue()).booleanValue()) {
                addHole(new Hole(potentialHole1.down(), Hole.Type.VOID), (Color) HoleESP.voidColor.getValue());
                return;
            }

            if (HoleUtil.isBedRockHole(potentialHole1)) {
                addHole(new Hole(potentialHole1, Hole.Type.BEDROCK), (Color) HoleESP.bedrockColor.getValue());
            } else if (HoleUtil.isObsidianHole(potentialHole1)) {
                addHole(new Hole(potentialHole1, Hole.Type.OBSIDIAN), (Color) HoleESP.obsidianColor.getValue());
            }

            if (((Boolean) HoleESP.doubles.getValue()).booleanValue()) {
                if (HoleUtil.isDoubleBedrockHoleX(potentialHole1.west())) {
                    addHole(new Hole(potentialHole1.west(), Hole.Type.DOUBLEBEDROCKX), (Color) HoleESP.bedrockColor.getValue());
                } else if (HoleUtil.isDoubleBedrockHoleZ(potentialHole1.north())) {
                    addHole(new Hole(potentialHole1.north(), Hole.Type.DOUBLEBEDROCKZ), (Color) HoleESP.bedrockColor.getValue());
                } else if (HoleUtil.isDoubleObsidianHoleX(potentialHole1.west())) {
                    addHole(new Hole(potentialHole1.west(), Hole.Type.DOUBLEOBSIDIANX), (Color) HoleESP.obsidianColor.getValue());
                } else if (HoleUtil.isDoubleObsidianHoleZ(potentialHole1.north())) {
                    addHole(new Hole(potentialHole1.north(), Hole.Type.DOUBLEOBSIDIANZ), (Color) HoleESP.obsidianColor.getValue());
                }
            }
        }

    }
}
