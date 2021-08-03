package cope.cosmos.client.features.modules.combat;

import cope.cosmos.client.events.BlockBreakEvent;
import cope.cosmos.client.events.PacketEvent;
import cope.cosmos.client.events.TotemPopEvent;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.loader.asm.mixins.accessor.ICPacketPlayer;
import cope.cosmos.util.player.InventoryUtil;
import cope.cosmos.util.player.PlayerUtil;
import cope.cosmos.util.player.Rotation;
import cope.cosmos.util.render.RenderBuilder;
import cope.cosmos.util.render.RenderUtil;
import cope.cosmos.util.system.MathUtil;
import cope.cosmos.util.world.AngleUtil;
import cope.cosmos.util.world.BlockUtil;
import cope.cosmos.util.world.HoleUtil;
import cope.cosmos.util.world.RaytraceUtil;
import cope.cosmos.util.world.TeleportUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Surround extends Module {

    public static Surround INSTANCE;
    public static Setting surround = new Setting("Surround", "Block positions for surround", Surround.SurroundVectors.BASE);
    public static Setting completion = new Setting("Completion", "When to toggle surround", Surround.Completion.AIR);
    public static Setting center = new Setting("Center", "Mode to center the player position", Surround.Center.TELEPORT);
    public static Setting autoSwitch = new Setting("Switch", "Mode to switch to blocks", InventoryUtil.Switch.NORMAL);
    public static Setting swing = new Setting("Swing", "Hand to swing when placing blocks", PlayerUtil.Hand.MAINHAND);
    public static Setting blocks = new Setting("Blocks", "Allowed block placements per tick", Double.valueOf(0.0D), Double.valueOf(4.0D), Double.valueOf(10.0D), 0);
    public static Setting raytrace = new Setting("Raytrace", "Verify if the placement is visible", Boolean.valueOf(false));
    public static Setting packet = new Setting("Packet", "Place with packets", Boolean.valueOf(false));
    public static Setting confirm = new Setting("Confirm", "Confirm the placement", Boolean.valueOf(false));
    public static Setting reactive = new Setting("Reactive", "Replaces surround blocks when they break", Boolean.valueOf(true));
    public static Setting chainPop = new Setting("ChainPop", "Surround when popping totems", Boolean.valueOf(false));
    public static Setting rotate = new Setting("Rotation", "Mode for attack rotations", Rotation.Rotate.NONE);
    public static Setting rotateCenter = (new Setting("Center", "Center rotations on target", Boolean.valueOf(false))).setParent(Surround.rotate);
    public static Setting rotateRandom = (new Setting("Random", "Randomize rotations to simulate real rotations", Boolean.valueOf(false))).setParent(Surround.rotate);
    public static Setting render = new Setting("Render", "Render a visual of the surround", Boolean.valueOf(true));
    public static Setting renderMode = (new Setting("Mode", "Style of the visual", RenderBuilder.Box.FILL)).setParent(Surround.render);
    public static Setting renderSafe = (new Setting("SafeColor", "Color for surrounded blocks", new Color(0, 255, 0, 40))).setParent(Surround.render);
    public static Setting renderUnsafe = (new Setting("UnsafeColor", "Color for unsafe blocks", new Color(255, 0, 0, 40))).setParent(Surround.render);
    int previousSlot = -1;
    int surroundPlaced = 0;
    BlockPos previousPosition;
    BlockPos surroundPosition;
    Rotation surroundRotation;

    public Surround() {
        super("Surround", Category.COMBAT, "Surrounds your feet with obsidian");
        this.previousPosition = BlockPos.ORIGIN;
        this.surroundPosition = BlockPos.ORIGIN;
        this.surroundRotation = new Rotation(Float.NaN, Float.NaN, (Rotation.Rotate) Surround.rotate.getValue());
        Surround.INSTANCE = this;
    }

    public void onEnable() {
        super.onEnable();
        this.previousPosition = new BlockPos(new Vec3d((double) MathUtil.roundFloat(Surround.mc.player.getPositionVector().x, 0), (double) MathUtil.roundFloat(Surround.mc.player.getPositionVector().y, 0), (double) MathUtil.roundFloat(Surround.mc.player.getPositionVector().z, 0)));
        switch ((Surround.Center) Surround.center.getValue()) {
        case TELEPORT:
            double xPosition = Surround.mc.player.getPositionVector().x;
            double zPosition = Surround.mc.player.getPositionVector().z;
            int zDirection;

            if (Math.abs((double) this.previousPosition.getX() + 0.5D - Surround.mc.player.getPositionVector().x) >= 0.2D) {
                zDirection = (double) this.previousPosition.getX() + 0.5D - Surround.mc.player.getPositionVector().x > 0.0D ? 1 : -1;
                xPosition += 0.3D * (double) zDirection;
            }

            if (Math.abs((double) this.previousPosition.getZ() + 0.5D - Surround.mc.player.getPositionVector().z) >= 0.2D) {
                zDirection = (double) this.previousPosition.getZ() + 0.5D - Surround.mc.player.getPositionVector().z > 0.0D ? 1 : -1;
                zPosition += 0.3D * (double) zDirection;
            }

            TeleportUtil.teleportPlayer(xPosition, Surround.mc.player.posY, zPosition);
            break;

        case MOTION:
            Surround.mc.player.motionX = (Math.floor(Surround.mc.player.posX) + 0.5D - Surround.mc.player.posX) / 2.0D;
            Surround.mc.player.motionZ = (Math.floor(Surround.mc.player.posZ) + 0.5D - Surround.mc.player.posZ) / 2.0D;

        case NONE:
        }

    }

    public void onUpdate() {
        this.surroundPlaced = 0;
        switch ((Surround.Completion) Surround.completion.getValue()) {
        case AIR:
            if (!this.previousPosition.equals(new BlockPos(new Vec3d((double) MathUtil.roundFloat(Surround.mc.player.getPositionVector().x, 0), (double) MathUtil.roundFloat(Surround.mc.player.getPositionVector().y, 0), (double) MathUtil.roundFloat(Surround.mc.player.getPositionVector().z, 0)))) || Surround.mc.player.posY > (double) this.previousPosition.getY()) {
                this.disable();
                this.getAnimation().setState(false);
                return;
            }
            break;

        case SURROUNDED:
            if (HoleUtil.isInHole(Surround.mc.player)) {
                this.disable();
                this.getAnimation().setState(false);
                return;
            }

        case PERSISTENT:
        }

        this.handleSurround();
    }

    public void onRender3d() {
        if (((Boolean) Surround.render.getValue()).booleanValue()) {
            Iterator iterator = ((Surround.SurroundVectors) Surround.surround.getValue()).getVectors().iterator();

            while (iterator.hasNext()) {
                Vec3d surroundVectors = (Vec3d) iterator.next();

                RenderUtil.drawBox((new RenderBuilder()).position(new BlockPos(surroundVectors.add(new Vec3d(Surround.mc.player.posX, (double) Math.round(Surround.mc.player.posY), Surround.mc.player.posZ)))).color(!Objects.equals(BlockUtil.getBlockResistance(new BlockPos(surroundVectors.add(new Vec3d(Surround.mc.player.posX, (double) Math.round(Surround.mc.player.posY), Surround.mc.player.posZ)))), BlockUtil.BlockResistance.RESISTANT) && !Objects.equals(BlockUtil.getBlockResistance(new BlockPos(surroundVectors.add(new Vec3d(Surround.mc.player.posX, (double) Math.round(Surround.mc.player.posY), Surround.mc.player.posZ)))), BlockUtil.BlockResistance.UNBREAKABLE) ? (Color) Surround.renderUnsafe.getValue() : (Color) Surround.renderSafe.getValue()).box((RenderBuilder.Box) Surround.renderMode.getValue()).setup().line(1.5F).cull(((RenderBuilder.Box) Surround.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) Surround.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).shade(((RenderBuilder.Box) Surround.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) Surround.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).alpha(((RenderBuilder.Box) Surround.renderMode.getValue()).equals(RenderBuilder.Box.GLOW) || ((RenderBuilder.Box) Surround.renderMode.getValue()).equals(RenderBuilder.Box.REVERSE)).depth(true).blend().texture());
            }
        }

    }

    public void handleSurround() {
        this.previousSlot = Surround.mc.player.inventory.currentItem;
        if (!HoleUtil.isInHole(Surround.mc.player)) {
            InventoryUtil.switchToSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), (InventoryUtil.Switch) Surround.autoSwitch.getValue());
            this.placeSurround();
            InventoryUtil.switchToSlot(this.previousSlot, InventoryUtil.Switch.NORMAL);
        }

    }

    public void placeSurround() {
        Iterator iterator = ((Surround.SurroundVectors) Surround.surround.getValue()).getVectors().iterator();

        while (iterator.hasNext()) {
            Vec3d surroundVectors = (Vec3d) iterator.next();

            if (Objects.equals(BlockUtil.getBlockResistance(new BlockPos(surroundVectors.add(new Vec3d(Surround.mc.player.posX, (double) Math.round(Surround.mc.player.posY), Surround.mc.player.posZ)))), BlockUtil.BlockResistance.BLANK) && (double) this.surroundPlaced <= ((Double) Surround.blocks.getValue()).doubleValue()) {
                this.surroundPosition = new BlockPos(surroundVectors.add(new Vec3d(Surround.mc.player.posX, (double) Math.round(Surround.mc.player.posY), Surround.mc.player.posZ)));
                if (RaytraceUtil.raytraceBlock(this.surroundPosition, AutoCrystal.Raytrace.NORMAL) && ((Boolean) Surround.raytrace.getValue()).booleanValue()) {
                    return;
                }

                if (this.surroundPosition != BlockPos.ORIGIN && !((Rotation.Rotate) Surround.rotate.getValue()).equals(Rotation.Rotate.NONE)) {
                    float[] surroundAngles = ((Boolean) Surround.rotateCenter.getValue()).booleanValue() ? AngleUtil.calculateCenter(this.surroundPosition) : AngleUtil.calculateAngles(this.surroundPosition);

                    this.surroundRotation = new Rotation((float) ((double) surroundAngles[0] + (((Boolean) Surround.rotateRandom.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : 0.0D)), (float) ((double) surroundAngles[1] + (((Boolean) Surround.rotateRandom.getValue()).booleanValue() ? ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D) : 0.0D)), (Rotation.Rotate) Surround.rotate.getValue());
                    if (!Float.isNaN(this.surroundRotation.getYaw()) && !Float.isNaN(this.surroundRotation.getPitch())) {
                        this.surroundRotation.updateModelRotations();
                    }
                }

                Iterator iterator1 = Surround.mc.world.loadedEntityList.iterator();

                while (iterator1.hasNext()) {
                    Entity item = (Entity) iterator1.next();

                    if (item instanceof EntityItem && ((EntityItem) item).getItem().getItem().equals(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                        item.setDead();
                        Surround.mc.world.removeEntityFromWorld(item.getEntityId());
                    }
                }

                BlockUtil.placeBlock(new BlockPos(surroundVectors.add(new Vec3d(Surround.mc.player.posX, (double) Math.round(Surround.mc.player.posY), Surround.mc.player.posZ))), ((Boolean) Surround.packet.getValue()).booleanValue(), ((Boolean) Surround.confirm.getValue()).booleanValue());
                PlayerUtil.swingArm((PlayerUtil.Hand) Surround.swing.getValue());
                ++this.surroundPlaced;
            }
        }

    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        if (HoleUtil.isPartOfHole(event.getBlockPos().down()) && ((Boolean) Surround.reactive.getValue()).booleanValue()) {
            BlockUtil.placeBlock(event.getBlockPos().down(), ((Boolean) Surround.packet.getValue()).booleanValue(), ((Boolean) Surround.confirm.getValue()).booleanValue());
        }

    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        if (!HoleUtil.isInHole(Surround.mc.player) && event.getPopEntity().equals(Surround.mc.player) && ((Boolean) Surround.chainPop.getValue()).booleanValue()) {
            InventoryUtil.switchToSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), (InventoryUtil.Switch) Surround.autoSwitch.getValue());
            this.placeSurround();
            InventoryUtil.switchToSlot(this.previousSlot, InventoryUtil.Switch.NORMAL);
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer && !Float.isNaN(this.surroundRotation.getYaw()) && !Float.isNaN(this.surroundRotation.getPitch())) {
            ((ICPacketPlayer) event.getPacket()).setYaw(this.surroundRotation.getYaw());
            ((ICPacketPlayer) event.getPacket()).setPitch(this.surroundRotation.getPitch());
        }

    }

    public static enum Completion {

        AIR, SURROUNDED, PERSISTENT;
    }

    public static enum Center {

        TELEPORT, MOTION, NONE;
    }

    public static enum SurroundVectors {

        BASE(new ArrayList(Arrays.asList(new Vec3d[] { new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D)}))), STANDARD(new ArrayList(Arrays.asList(new Vec3d[] { new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(0.0D, 0.0D, -1.0D)}))), PROTECT(new ArrayList(Arrays.asList(new Vec3d[] { new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 2.0D), new Vec3d(0.0D, 0.0D, -2.0D), new Vec3d(3.0D, 0.0D, 0.0D), new Vec3d(-3.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 3.0D), new Vec3d(0.0D, 0.0D, -3.0D)})));

        private final List vectors;

        private SurroundVectors(List vectors) {
            this.vectors = vectors;
        }

        public List getVectors() {
            return this.vectors;
        }
    }
}
