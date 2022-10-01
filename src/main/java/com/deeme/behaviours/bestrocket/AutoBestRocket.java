package com.deeme.behaviours.bestrocket;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.deeme.types.VerifierChecker;
import com.deeme.types.backpage.Utils;

import eu.darkbot.api.PluginAPI;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.config.types.PercentRange;
import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.extensions.Configurable;
import eu.darkbot.api.extensions.Feature;
import eu.darkbot.api.game.entities.Entity;
import eu.darkbot.api.game.entities.Npc;
import eu.darkbot.api.game.items.ItemCategory;
import eu.darkbot.api.game.items.ItemFlag;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.game.items.SelectableItem.Rocket;
import eu.darkbot.api.game.other.Lockable;
import eu.darkbot.api.game.other.Movable;
import eu.darkbot.api.managers.AuthAPI;
import eu.darkbot.api.managers.BotAPI;
import eu.darkbot.api.managers.ConfigAPI;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.HeroItemsAPI;
import eu.darkbot.api.managers.MovementAPI;
import eu.darkbot.api.utils.Inject;

@Feature(name = "Auto Best Rocket", description = "Automatically switches rockets. Will use all available rockets")
public class AutoBestRocket implements Behavior, Configurable<BestRocketConfig> {

    protected final PluginAPI api;
    protected final BotAPI bot;
    protected final HeroAPI heroapi;
    protected final MovementAPI movement;
    private final HeroItemsAPI items;
    protected final ConfigSetting<PercentRange> repairHpRange;
    private BestRocketConfig config;

    public AutoBestRocket(PluginAPI api) {
        this(api, api.requireAPI(AuthAPI.class),
                api.requireAPI(BotAPI.class), api.requireAPI(HeroItemsAPI.class));
    }

    @Inject
    public AutoBestRocket(PluginAPI api, AuthAPI auth, BotAPI bot, HeroItemsAPI items) {
        if (!Arrays.equals(VerifierChecker.class.getSigners(), getClass().getSigners()))
            throw new SecurityException();
        VerifierChecker.checkAuthenticity(auth);

        Utils.showDonateDialog();

        this.api = api;
        this.bot = bot;
        this.items = items;
        this.heroapi = api.getAPI(HeroAPI.class);
        this.movement = api.getAPI(MovementAPI.class);

        ConfigAPI configAPI = api.getAPI(ConfigAPI.class);
        this.repairHpRange = configAPI.requireConfig("general.safety.repair_hp_range");
    }

    @Override
    public void setConfig(ConfigSetting<BestRocketConfig> arg0) {
        this.config = arg0.getValue();
    }

    @Override
    public void onTickBehavior() {
        Entity target = heroapi.getLocalTarget();
        if (target != null && target.isValid()) {
            if (target instanceof Npc) {
                changeRocket(getItemById(config.npcRocket));
            } else {
                changeRocket(getBestRocketPVP());
            }
        }
    }

    private void changeRocket(SelectableItem rocket) {
        try {
            if (rocket != null && heroapi.getRocket() != null && !heroapi.getRocket().getId().equals(rocket.getId())) {
                items.useItem(rocket, 500, ItemFlag.USABLE, ItemFlag.READY);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Rocket getBestRocketPVP() {
        Lockable target = heroapi.getLocalTarget();
        if (target != null && target.isValid()) {
            if (shoulFocusSpeed(target)) {
                if (items.getItem(Rocket.R_IC3, ItemFlag.USABLE, ItemFlag.READY).isPresent()) {
                    return Rocket.R_IC3;
                } else if (items.getItem(Rocket.DCR_250, ItemFlag.USABLE, ItemFlag.READY).isPresent()) {
                    return Rocket.DCR_250;
                }
            }
            if (shoulUsePLD(target)
                    && items.getItem(Rocket.PLD_8, ItemFlag.USABLE, ItemFlag.READY).isPresent()) {
                return Rocket.PLD_8;
            }
        }
        if (items.getItem(Rocket.PLT_3030, ItemFlag.USABLE, ItemFlag.READY).isPresent()) {
            return Rocket.PLT_3030;
        } else if (items.getItem(Rocket.PLT_2021, ItemFlag.USABLE, ItemFlag.READY).isPresent()) {
            return Rocket.PLT_2021;
        } else if (items.getItem(Rocket.PLT_2026, ItemFlag.USABLE, ItemFlag.READY).isPresent()) {
            return Rocket.PLT_2026;
        } else if (items.getItem(Rocket.R_310, ItemFlag.USABLE, ItemFlag.READY).isPresent()) {
            return Rocket.R_310;
        }
        return null;
    }

    private boolean shoulFocusSpeed(Lockable target) {
        double distance = heroapi.getLocationInfo().getCurrent().distanceTo(target.getLocationInfo());
        double speed = target instanceof Movable ? ((Movable) target).getSpeed() : 0;

        return (distance > 400 && speed > heroapi.getSpeed())
                || (distance < 600 && speed > heroapi.getSpeed()
                        && heroapi.getHealth().hpPercent() < repairHpRange.getValue().getMin());
    }

    private boolean shoulUsePLD(Lockable target) {
        return target instanceof Movable && ((Movable) target).isAiming(heroapi)
                && heroapi.getHealth().hpPercent() < 0.5;
    }

    private SelectableItem getItemById(String id) {
        Iterator<ItemCategory> it = SelectableItem.ALL_ITEMS.keySet().iterator();
        while (it.hasNext()) {
            ItemCategory key = it.next();
            List<SelectableItem> selectableItemList = SelectableItem.ALL_ITEMS.get(key);
            Iterator<SelectableItem> itItem = selectableItemList.iterator();
            while (itItem.hasNext()) {
                SelectableItem next = itItem.next();
                if (next.getId().equals(id)) {
                    return next;
                }
            }
        }
        return null;
    }

}
