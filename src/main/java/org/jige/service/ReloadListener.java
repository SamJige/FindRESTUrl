package org.jige.service;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jige.util.StringTools;
import org.slf4j.LoggerFactory;

public class ReloadListener implements DynamicPluginListener {
    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        LoggerFactory.getLogger(getClass()).info("Loaded");
        StringTools.log("Loaded");
    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LoggerFactory.getLogger(getClass()).info("Unloaded");
        StringTools.log("Unloaded");
    }
}
