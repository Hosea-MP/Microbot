package net.runelite.client.plugins.microbot.bankwithdraw;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayerID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

public class BankWithdrawScript extends Script {
    
    public static double version = 1.0;
    
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                
                // Check all conditions before proceeding
                if (!meetsRequirements()) {
                    Microbot.log("Requirements not met - stopping script");
                    return;
                }
                
                // Open bank if not already open
                if (!Rs2Bank.isOpen()) {
                    if (!openBank()) {
                        Microbot.log("Could not open bank");
                        return;
                    }
                }
                
                // Withdraw 5 iron ores
                withdrawIronOres();
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
    
    /**
     * Checks if all requirements are met:
     * 1. Mithril pickaxe is equipped
     * 2. Mining level is greater than 20
     * 3. Player is not poisoned
     */
    private boolean meetsRequirements() {
        // Check if mithril pickaxe is equipped
        if (!isMithrilPickaxeEquipped()) {
            Microbot.log("Mithril pickaxe is not equipped");
            return false;
        }
        
        // Check mining level is greater than 20
        if (!hasSufficientMiningLevel()) {
            Microbot.log("Mining level must be greater than 20");
            return false;
        }
        
        // Check if player is not poisoned
        if (isPoisoned()) {
            Microbot.log("Player is poisoned - cannot withdraw items");
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if a mithril pickaxe is equipped in the weapon slot
     */
    private boolean isMithrilPickaxeEquipped() {
        return Rs2Equipment.isWearing(ItemID.MITHRIL_PICKAXE, EquipmentInventorySlot.WEAPON);
    }
    
    /**
     * Checks if the player's mining level is greater than 20
     */
    private boolean hasSufficientMiningLevel() {
        int miningLevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
        Microbot.log("Current mining level: " + miningLevel);
        return miningLevel > 20;
    }
    
    /**
     * Checks if the player is currently poisoned or venomed
     * Based on VarPlayer POISON - positive values indicate poison/venom damage
     */
    private boolean isPoisoned() {
        int poisonStatus = Microbot.getClient().getVarpValue(VarPlayerID.POISON);
        // Positive values indicate poison/venom damage
        // Zero or negative values indicate no poison or immunity
        return poisonStatus > 0;
    }
    
    /**
     * Attempts to open the bank
     */
    private boolean openBank() {
        if (Rs2Bank.isOpen()) {
            return true;
        }
        
        Microbot.log("Attempting to open bank...");
        
        // Try to open bank (this will find the nearest bank and open it)
        if (Rs2Bank.openBank()) {
            // Wait for bank to open
            Rs2Bank.sleepUntilOpen();
            return Rs2Bank.isOpen();
        }
        
        return false;
    }
    
    /**
     * Withdraws 5 iron ores from the bank
     */
    private void withdrawIronOres() {
        if (!Rs2Bank.isOpen()) {
            Microbot.log("Bank is not open");
            return;
        }
        
        // Check if bank contains iron ore
        if (!Rs2Bank.hasBankItem("iron ore")) {
            Microbot.log("No iron ore found in bank");
            return;
        }
        
        // Withdraw exactly 5 iron ores
        if (Rs2Bank.withdrawX("iron ore", 5)) {
            Microbot.log("Successfully withdrew 5 iron ores");
        } else {
            Microbot.log("Failed to withdraw iron ores");
        }
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
    }
}