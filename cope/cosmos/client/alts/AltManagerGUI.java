package cope.cosmos.client.alts;

import cope.cosmos.client.manager.managers.AltManager;
import cope.cosmos.loader.asm.mixins.accessor.ISession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class AltManagerGUI extends GuiScreen {

    private GuiButton delete;
    private GuiScreen lastGui;
    private AltManagerGUI.AltSlotList altList;
    private GuiTextField crackedNameField;

    public AltManagerGUI(GuiScreen lastGui) {
        this.lastGui = lastGui;
    }

    public void initGui() {
        super.initGui();
        this.crackedNameField = new GuiTextField(69, this.mc.fontRenderer, 4, 20, 95, 15);
        this.crackedNameField.setText(this.mc.getSession().getUsername());
        this.crackedNameField.setMaxStringLength(16);
        this.altList = new AltManagerGUI.AltSlotList(this, this.mc, this.width, this.height, 40, this.height - 60, 36);
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 52, 75, 20, "Add"));
        this.delete = new GuiButton(2, this.width / 2 + 1, this.height - 52, 75, 20, "Delete");
        this.buttonList.add(this.delete);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 75, this.height - 30, 150, 20, "Back"));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.altList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.mc.fontRenderer, "Cosmos Alt Manager", this.width / 2, 15, -1);
        String s = "Signed in as ";

        this.drawString(this.mc.fontRenderer, s, 4, 6, -5592406);
        this.drawString(this.mc.fontRenderer, this.mc.getSession().getUsername(), this.mc.fontRenderer.getStringWidth(s) + 3, 6, -1);
        this.crackedNameField.drawTextBox();
        if (!this.crackedNameField.isFocused()) {
            this.crackedNameField.setText(this.mc.getSession().getUsername());
        }

        this.delete.enabled = ((Boolean) this.altList.getVisibility().get()).booleanValue();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.crackedNameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.altList.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void handleMouseInput() throws IOException {
        this.altList.handleMouseInput();
        super.handleMouseInput();
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.lastGui);
        } else {
            this.crackedNameField.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 28) {
                ((ISession) this.mc.getSession()).setUsername(this.crackedNameField.getText());
                this.crackedNameField.setFocused(false);
            }

            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        this.altList.actionPerformed(button);
        switch (button.id) {
        case 1:
            this.mc.displayGuiScreen(new AltCreatorGUI(this));
            break;

        case 2:
            if (((Boolean) this.altList.getVisibility().get()).booleanValue()) {
                AltEntry e = (AltEntry) this.altList.getAlts().get(this.altList.getSelectedId());

                this.altList.getAlts().remove(e);
                AltManager.getAlts().remove(e);
            }
            break;

        case 3:
            this.mc.displayGuiScreen(this.lastGui);
        }

    }

    private static class AltSlotList extends GuiListExtended {

        private final List alts = new ArrayList();
        private int selectedId = -1;

        public AltSlotList(AltManagerGUI parentGui, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
            super(mc, width, height, top, bottom, slotHeight);
            this.alts.clear();
            AltManager.getAlts().forEach((alt) -> {
                this.alts.add(alt);
            });
        }

        public AltEntry getListEntry(int index) {
            return (AltEntry) this.alts.get(index);
        }

        public int getListWidth() {
            return super.getListWidth() + 50;
        }

        protected void elementClicked(int i, boolean b, int i1, int i2) {
            this.selectElement(i);
        }

        protected int getSize() {
            return this.alts.size();
        }

        protected int getScrollBarX() {
            return super.getScrollBarX() + 20;
        }

        protected boolean isSelected(int slotIndex) {
            return this.selectedId == slotIndex;
        }

        protected Supplier getVisibility() {
            return () -> {
                return Boolean.valueOf(this.selectedId > -1);
            };
        }

        protected List getAlts() {
            return this.alts;
        }

        protected int getSelectedId() {
            return this.selectedId;
        }

        private void selectElement(int element) {
            this.selectedId = element;
            this.showSelectionBox = true;
            this.selectedElement = element;
        }
    }
}
