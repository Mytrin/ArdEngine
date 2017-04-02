package net.sf.ardengine.rpg.multiplayer;

public abstract class DelayedTask {

    private long when;

    public DelayedTask(long when){
        this.when = when;
    }

    public boolean timePassed(long delta){
        when -= delta;
        return when <= 0;
    }

    protected abstract void execute();

}
