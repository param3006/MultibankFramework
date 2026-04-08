package com.multibank.models;

import com.multibank.Interfaces.FrameworkConfig;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigCache;

public class ConfigManager {

    private ConfigManager(){};

    public static FrameworkConfig get(){
        return ConfigCache.getOrCreate(FrameworkConfig.class);
    }


}
