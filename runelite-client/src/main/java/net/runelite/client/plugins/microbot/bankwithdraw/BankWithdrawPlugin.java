package net.runelite.client.plugins.microbot.bankwithdraw;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Microbot + "Bank Withdraw",
        description = "Withdraws 5 iron ores from bank if mithril pickaxe equipped, mining level > 20, and not poisoned",
        tags = {"bank", "mining", "iron", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class BankWithdrawPlugin extends Plugin {
    
    @Inject
    private BankWithdrawConfig config;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private BankWithdrawOverlay bankWithdrawOverlay;
    
    @Provides
    BankWithdrawConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BankWithdrawConfig.class);
    }
    
    private BankWithdrawScript bankWithdrawScript;
    
    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setMouse(new VirtualMouse());
        
        if (overlayManager != null) {
            overlayManager.add(bankWithdrawOverlay);
        }
        
        bankWithdrawScript = new BankWithdrawScript();
        bankWithdrawScript.run();
    }
    
    protected void shutDown() {
        bankWithdrawScript.shutdown();
        overlayManager.remove(bankWithdrawOverlay);
    }
}