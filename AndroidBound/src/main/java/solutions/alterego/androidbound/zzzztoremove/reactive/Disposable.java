package solutions.alterego.androidbound.zzzztoremove.reactive;

public class Disposable implements IDisposable {

    boolean disposeCalled;

    VoidAction onDispose;

    public Disposable() {
        this.onDispose = Actions.doNothingVoid();
    }

    public Disposable(VoidAction voidAction) {
        this.onDispose = voidAction;
    }

    @Override
    public void finalize() throws Throwable {
        if (!disposeCalled) {
            dispose();
        }
        super.finalize();
    }

    public synchronized void dispose() {
        disposeCalled = true;
        onDispose.invoke();
    }
}
