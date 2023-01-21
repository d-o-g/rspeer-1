package org.rspeer.runetek.providers;

public interface RSAnimationSequence extends RSDoublyNode {

    int getAnimatingPrecedence();

    int getLoopOffset();

    int getMainHand();

    int getMaxLoops();

    int getOffHand();

    int getPriority();

    int getReplayMode();

    int getWalkingPrecedence();

    boolean isStretch();

    int[] getFrameIds();

    int[] getFrameLengths();

    int[] getInterleaveOrder();

    /**
     * @return The duration of this animation in ticks
     */
    default int getDuration() {
        int[] frameLengths = getFrameLengths();
        if (frameLengths == null) {
            return 0;
        }
        int ms = 0;
        for (int i : frameLengths) {
            ms += i;
        }
        int duration = ms * 30 / 600;
        return duration < 1 ? 1 : duration;
    }
}