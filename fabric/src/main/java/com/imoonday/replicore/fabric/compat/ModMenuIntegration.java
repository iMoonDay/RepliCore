package com.imoonday.replicore.fabric.compat;

import com.imoonday.replicore.RepliCore;
import com.imoonday.replicore.client.ModConfigScreenFactory;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> RepliCore.clothConfig ? ModConfigScreenFactory.create(parent) : null;
    }
}
