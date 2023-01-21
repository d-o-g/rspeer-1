package org.rspeer.ui.debug.quicklaunchtool.model;

/**
 * @author MalikDz
 */

public class BotConfigurationData {

    private int engineTick;
    private boolean disableSceneRendering, disableModelRendering, superLowCpu, lowCpu;

    public BotConfigurationData(boolean disableSceneRendering, boolean disableModelRendering, boolean superLowCpu,
                                boolean lowCpu, int engineTick) {
        this.disableSceneRendering = disableSceneRendering;
        this.disableModelRendering = disableModelRendering;
        this.superLowCpu = superLowCpu;
        this.engineTick = engineTick;
        this.lowCpu = lowCpu;
    }

    public int getEngineTick() {
        return engineTick;
    }

    public boolean disablingSceneRendering() {
        return disableSceneRendering;
    }

    public boolean disablingModelRendering() {
        return disableModelRendering;
    }

    public boolean activatingSuperLowCpu() {
        return superLowCpu;
    }

    public boolean activatingLowCpu() {
        return lowCpu;
    }
}
