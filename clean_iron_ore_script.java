import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayerID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;

public class IronOreWithdrawalScript {
    
    public static void main() {
        if (!Microbot.isLoggedIn()) {
            return;
        }
        
        if (!meetsAllRequirements()) {
            return;
        }
        
        if (!Rs2Bank.isOpen()) {
            if (!Rs2Bank.openBank()) {
                return;
            }
            Rs2Bank.sleepUntilOpen();
        }
        
        withdrawIronOres();
    }
    
    private static boolean meetsAllRequirements() {
        if (!Rs2Equipment.isWearing(ItemID.MITHRIL_PICKAXE, EquipmentInventorySlot.WEAPON)) {
            return false;
        }
        
        int miningLevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
        if (miningLevel <= 20) {
            return false;
        }
        
        int poisonStatus = Microbot.getClient().getVarpValue(VarPlayerID.POISON);
        if (poisonStatus > 0) {
            return false;
        }
        
        return true;
    }
    
    private static void withdrawIronOres() {
        if (!Rs2Bank.hasBankItem("iron ore")) {
            return;
        }
        
        int bankCount = Rs2Bank.getBankItem("iron ore").getQuantity();
        
        if (bankCount < 5) {
            Rs2Bank.withdrawAll("iron ore");
        } else {
            Rs2Bank.withdrawX("iron ore", 5);
        }
    }
}