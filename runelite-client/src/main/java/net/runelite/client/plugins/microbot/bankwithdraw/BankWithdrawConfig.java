package net.runelite.client.plugins.microbot.bankwithdraw;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("BankWithdraw")
public interface BankWithdrawConfig extends Config {
    
    @ConfigItem(
            keyName = "enableScript",
            name = "Enable Script",
            description = "Toggle the bank withdraw script on/off"
    )
    default boolean enableScript() {
        return true;
    }
    
    @ConfigItem(
            keyName = "logMessages",
            name = "Show Log Messages",
            description = "Show detailed log messages in the chatbox"
    )
    default boolean logMessages() {
        return true;
    }
}