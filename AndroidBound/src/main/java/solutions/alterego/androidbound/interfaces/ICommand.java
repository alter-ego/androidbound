package solutions.alterego.androidbound.interfaces;

import android.view.View;

public interface ICommand {

    ICommand empty = new ICommand() {
        public boolean canExecute(Object parameter) {
            return false;
        }

        public void execute(Object parameter) {
        }

        @Override
        public boolean canExecute(View view, Object parameter) {
            return false;
        }

        @Override
        public void execute(View view, Object parameter) {
        }
    };

    boolean canExecute(Object parameter);

    void execute(Object parameter);

    boolean canExecute(View view, Object parameter);

    void execute(View view, Object parameter);
}
