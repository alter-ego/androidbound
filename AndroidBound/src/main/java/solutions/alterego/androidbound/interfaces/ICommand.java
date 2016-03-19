package solutions.alterego.androidbound.interfaces;

public interface ICommand {

    public static final ICommand empty = new ICommand() {
        public boolean canExecute(Object parameter) {
            return false;
        }

        public void execute(Object parameter) {
        }
    };

    boolean canExecute(Object parameter);

    void execute(Object parameter);
}
