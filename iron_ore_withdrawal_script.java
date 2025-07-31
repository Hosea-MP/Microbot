/*
 * Iron Ore Bank Withdrawal Script
 * 
 * This script withdraws 5 iron ores from the bank only if:
 * 1. A mithril pickaxe is equipped
 * 2. The player's mining level is greater than 20
 * 3. The player is not poisoned
 * 
 * Usage: Place this script in your RuneLite microbot scripts folder and run it
 */

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayerID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;

public class IronOreWithdrawalScript {
    
    public static void main() {
        // Check if player is logged in
        if (!Microbot.isLoggedIn()) {
            System.out.println("Player is not logged in!");
            return;
        }
        
        // Check all requirements
        if (!meetsAllRequirements()) {
            System.out.println("Requirements not met - script aborted");
            return;
        }
        
        // Open bank if needed
        if (!Rs2Bank.isOpen()) {
            System.out.println("Opening bank...");
            if (!Rs2Bank.openBank()) {
                System.out.println("Failed to open bank");
                return;
            }
            Rs2Bank.sleepUntilOpen();
        }
        
        // Withdraw 5 iron ores
        withdrawIronOres();
    }
    
    /**
     * Checks if all requirements are met for iron ore withdrawal
     */
    private static boolean meetsAllRequirements() {
        // Check 1: Mithril pickaxe equipped
        if (!Rs2Equipment.isWearing(ItemID.MITHRIL_PICKAXE, EquipmentInventorySlot.WEAPON)) {
            System.out.println("❌ Mithril pickaxe is not equipped in weapon slot");
            return false;
        }
        System.out.println("✅ Mithril pickaxe is equipped");
        
        // Check 2: Mining level > 20
        int miningLevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
        if (miningLevel <= 20) {
            System.out.println("❌ Mining level (" + miningLevel + ") must be greater than 20");
            return false;
        }
        System.out.println("✅ Mining level (" + miningLevel + ") is sufficient");
        
        // Check 3: Not poisoned
        int poisonStatus = Microbot.getClient().getVarpValue(VarPlayerID.POISON);
        if (poisonStatus > 0) {
            System.out.println("❌ Player is poisoned (poison value: " + poisonStatus + ")");
            return false;
        }
        System.out.println("✅ Player is not poisoned");
        
        return true;
    }
    
    /**
     * Withdraws 5 iron ores from the bank
     */
    private static void withdrawIronOres() {
        // Check if iron ore is available in bank
        if (!Rs2Bank.hasBankItem("iron ore")) {
            System.out.println("❌ No iron ore found in bank");
            return;
        }
        
        // Get current iron ore count in bank
        int bankCount = Rs2Bank.getBankItem("iron ore").getQuantity();
        System.out.println("Iron ore in bank: " + bankCount);
        
        if (bankCount < 5) {
            System.out.println("⚠️ Only " + bankCount + " iron ore available, withdrawing all");
            Rs2Bank.withdrawAll("iron ore");
        } else {
            // Withdraw exactly 5 iron ores
            System.out.println("Withdrawing 5 iron ores...");
            if (Rs2Bank.withdrawX("iron ore", 5)) {
                System.out.println("✅ Successfully withdrew 5 iron ores");
            } else {
                System.out.println("❌ Failed to withdraw iron ores");
            }
        }
    }
}

/*
 * How to use this script:
 * 
 * 1. Make sure you have:
 *    - A mithril pickaxe equipped in your weapon slot
 *    - Mining level greater than 20
 *    - No poison/venom status effect
 *    - Iron ore in your bank
 * 
 * 2. Be near a bank or have the bank interface open
 * 
 * 3. Run the script by calling IronOreWithdrawalScript.main()
 * 
 * The script will:
 * - Check all requirements first
 * - Open the bank if not already open
 * - Withdraw exactly 5 iron ores (or less if not enough available)
 * - Provide detailed feedback about the process
 */