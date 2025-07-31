package net.runelite.client.plugins.microbot.bankwithdraw;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayerID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;

import java.util.concurrent.TimeUnit;

public class BankWithdrawScript extends Script {
    
    public static double version = 1.0;
    
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                
                if (!meetsRequirements()) {
                    return;
                }
                
                if (!Rs2Bank.isOpen()) {
                    if (!openBank()) {
                        return;
                    }
                }
                
                withdrawIronOres();
                
            } catch (Exception ex) {
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
    
    private boolean meetsRequirements() {
        if (!isMithrilPickaxeEquipped()) {
            return false;
        }
        
        if (!hasSufficientMiningLevel()) {
            return false;
        }
        
        if (isPoisoned()) {
            return false;
        }
        
        return true;
    }
    
    private boolean isMithrilPickaxeEquipped() {
        return Rs2Equipment.isWearing(ItemID.MITHRIL_PICKAXE, EquipmentInventorySlot.WEAPON);
    }
    
    private boolean hasSufficientMiningLevel() {
        int miningLevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
        return miningLevel > 20;
    }
    
    private boolean isPoisoned() {
        int poisonStatus = Microbot.getClient().getVarpValue(VarPlayerID.POISON);
        return poisonStatus > 0;
    }
    
    private boolean openBank() {
        if (Rs2Bank.isOpen()) {
            return true;
        }
        
        if (Rs2Bank.openBank()) {
            Rs2Bank.sleepUntilOpen();
            return Rs2Bank.isOpen();
        }
        
        return false;
    }
    
    private void withdrawIronOres() {
        if (!Rs2Bank.isOpen()) {
            return;
        }
        
        if (!Rs2Bank.hasBankItem("iron ore")) {
            return;
        }
        
        Rs2Bank.withdrawX("iron ore", 5);
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
    }
}