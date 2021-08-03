package cope.cosmos.client.alts;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import cope.cosmos.client.manager.managers.AltManager;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.render.RenderUtil;
import java.util.Map;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AltEntry implements IGuiListEntry, Wrapper {

    private String email;
    private String password;
    private YggdrasilUserAuthentication auth;
    private ResourceLocation unknown = new ResourceLocation("textures/misc/unknown_server.png");
    private ResourceLocation selected = new ResourceLocation("textures/gui/world_selection.png");

    public AltEntry(String email, String password) {
        this.email = email;
        this.password = password;
        this.auth = AltManager.logIn(email, password, false);
    }

    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        try {
            AltEntry.mc.fontRenderer.drawStringWithShadow(this.auth.getSelectedProfile().getName(), (float) (x + 36), (float) (y + 2), -1);
            AltEntry.mc.fontRenderer.drawStringWithShadow(this.email, (float) (x + 36), (float) (y + 12), -7829368);
            AltEntry.mc.fontRenderer.drawStringWithShadow("Premium", (float) (x + 36), (float) (y + 22), -11141291);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            ResourceLocation npe = DefaultPlayerSkin.getDefaultSkinLegacy();
            Map map = AltEntry.mc.getSkinManager().loadSkinFromCache(this.auth.getSelectedProfile());

            if (map.containsKey(Type.SKIN)) {
                npe = AltEntry.mc.getSkinManager().loadSkin((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN);
            }

            AltEntry.mc.getTextureManager().bindTexture(npe);
            GL11.glEnable(3042);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
            GL11.glDisable(3042);
            if (isSelected) {
                AltEntry.mc.getTextureManager().bindTexture(this.selected);
                RenderUtil.drawRect((float) x, (float) y, 32.0F, 32.0F, -1601138544);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(x - 6, y + 3, 32.0F, 3.0F, 32, 32, 256.0F, 256.0F);
            }
        } catch (NullPointerException nullpointerexception) {
            AltEntry.mc.fontRenderer.drawStringWithShadow("Unknown Account", (float) (x + 36), (float) (y + 2), -43691);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            AltEntry.mc.getTextureManager().bindTexture(this.unknown);
            GL11.glEnable(3042);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
            GL11.glDisable(3042);
            if (isSelected) {
                AltEntry.mc.getTextureManager().bindTexture(this.selected);
                RenderUtil.drawRect((float) x, (float) y, 32.0F, 32.0F, -1601138544);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(x - 6, y + 3, 32.0F, 3.0F, 32, 32, 256.0F, 256.0F);
            }
        }

    }

    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        if (relativeX <= 32 && relativeX < 32) {
            AltManager.logIn(this.email, this.password, true);
            return true;
        } else {
            return false;
        }
    }

    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

    public String getName() {
        return this.auth.getSelectedProfile().getName();
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}
