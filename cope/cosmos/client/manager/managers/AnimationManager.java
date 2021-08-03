package cope.cosmos.client.manager.managers;

import cope.cosmos.client.manager.Manager;

public class AnimationManager extends Manager {

    private final int time;
    private boolean initialState;
    private AnimationManager.State previousState;
    private AnimationManager.State currentState;
    private long currentStateStart;

    public AnimationManager(int time, boolean initialState) {
        super("AnimationManager", "Manages simple two-way animations", 1);
        this.previousState = AnimationManager.State.STATIC;
        this.currentState = AnimationManager.State.STATIC;
        this.currentStateStart = 0L;
        this.time = time;
        this.initialState = initialState;
        if (initialState) {
            this.previousState = AnimationManager.State.EXPANDING;
        }

    }

    public double getAnimationFactor() {
        return this.currentState == AnimationManager.State.EXPANDING ? (double) (System.currentTimeMillis() - this.currentStateStart) / (double) this.time : (this.currentState == AnimationManager.State.RETRACTING ? (double) ((long) this.time - (System.currentTimeMillis() - this.currentStateStart)) / (double) this.time : (this.previousState == AnimationManager.State.EXPANDING ? 1.0D : 0.0D));
    }

    public boolean getState() {
        return this.initialState;
    }

    public void setState(boolean expand) {
        if (expand) {
            this.currentState = AnimationManager.State.EXPANDING;
            this.initialState = true;
        } else {
            this.currentState = AnimationManager.State.RETRACTING;
        }

        this.currentStateStart = System.currentTimeMillis();
    }

    public void setStateHard(boolean expand) {
        if (expand) {
            this.currentState = AnimationManager.State.EXPANDING;
            this.initialState = true;
            this.currentStateStart = System.currentTimeMillis();
        } else {
            this.previousState = AnimationManager.State.RETRACTING;
            this.currentState = AnimationManager.State.RETRACTING;
            this.initialState = false;
        }

    }

    public static enum State {

        EXPANDING, RETRACTING, STATIC;
    }
}
