package de.lightplugins.master;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardHook {

    public static StateFlag NO_LAVA_DAMAGE;

    public void setupCustomFlags() {


        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {

            StateFlag flag = new StateFlag("no-lava-damage", true);
            registry.register(flag);
            NO_LAVA_DAMAGE = flag; // only set our field if there was no error

        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("no-lava-damage");

            if (existing instanceof StateFlag) {
                NO_LAVA_DAMAGE = (StateFlag) existing;
                e.printStackTrace();
            }
        }
    }
}
