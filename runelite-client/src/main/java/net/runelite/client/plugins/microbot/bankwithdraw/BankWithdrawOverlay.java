package net.runelite.client.plugins.microbot.bankwithdraw;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayerID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.equipment.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class BankWithdrawOverlay extends OverlayPanel {
    
    private final BankWithdrawPlugin plugin;
    private final BankWithdrawConfig config;
    
    @Inject
    BankWithdrawOverlay(BankWithdrawPlugin plugin, BankWithdrawConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Bank Withdraw overlay"));
    }
    
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(250, 200));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Bank Withdraw Script")
                    .color(Color.GREEN)
                    .build());
            
            // Check mithril pickaxe status
            boolean hasMithrilPickaxe = Rs2Equipment.isWearing(ItemID.MITHRIL_PICKAXE, EquipmentInventorySlot.WEAPON);
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Mithril Pickaxe:")
                    .right(hasMithrilPickaxe ? "✓ Equipped" : "✗ Not Equipped")
                    .rightColor(hasMithrilPickaxe ? Color.GREEN : Color.RED)
                    .build());
            
            // Check mining level
            int miningLevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
            boolean validMiningLevel = miningLevel > 20;
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Mining Level:")
                    .right(miningLevel + (validMiningLevel ? " ✓" : " ✗"))
                    .rightColor(validMiningLevel ? Color.GREEN : Color.RED)
                    .build());
            
            // Check poison status
            int poisonStatus = Microbot.getClient().getVarpValue(VarPlayerID.POISON);
            boolean isPoisoned = poisonStatus > 0;
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Poison Status:")
                    .right(isPoisoned ? "✗ Poisoned" : "✓ Not Poisoned")
                    .rightColor(isPoisoned ? Color.RED : Color.GREEN)
                    .build());
            
            // Overall status
            boolean allRequirementsMet = hasMithrilPickaxe && validMiningLevel && !isPoisoned;
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .right(allRequirementsMet ? "Ready to withdraw" : "Requirements not met")
                    .rightColor(allRequirementsMet ? Color.GREEN : Color.RED)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Script Version:")
                    .right(String.valueOf(BankWithdrawScript.version))
                    .build());
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}