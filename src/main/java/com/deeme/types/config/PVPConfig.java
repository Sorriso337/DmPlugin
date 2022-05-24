package com.deeme.types.config;

import com.github.manolo8.darkbot.config.types.Num;
import com.github.manolo8.darkbot.config.types.Option;

@Option("PVP Config")
public class PVPConfig {
    @Option(value = "Movement", description = "The ship will move")
    public boolean move = true;

    @Option(value = "Auto change config", description = "It will change configuration automatically")
    public boolean changeConfig = true;

    @Option(value = "Use the run configuration", description = "Will use the run setting if enemies flee")
    public boolean useRunConfig = true;

    @Option(value = "Auto choose the best rocket", description = "Automatically switches missiles")
    public boolean useBestRocket = true;

    @Option(value = "Auto choose the best formation", description = "Automatically switches formations")
    public boolean useBestFormation = true;

    @Option(value = "Maximum range for enemies", description = "Enemies above this range will not be attacked")
    @Num(min = 0, max = 600, step = 50)
    public int rangeForEnemies = 100;

    public @Option(value = "Ability", description = "Ability Conditions")
    ExtraKeyConditions ability = new ExtraKeyConditions();

    public @Option(value = "ISH-01", description = "ISH-01 Conditions")
    ExtraKeyConditions ISH = new ExtraKeyConditions();

    public @Option(value = "SMB-01", description = "SMB-01 Conditions")
    ExtraKeyConditions SMB = new ExtraKeyConditions();

    public @Option(value = "PEM-01", description = "PEM-01 Conditions")
    ExtraKeyConditions PEM = new ExtraKeyConditions();

    public @Option(value = "Other Key", description = "Other Key Conditions")
    ExtraKeyConditions otherKey = new ExtraKeyConditions();
}
