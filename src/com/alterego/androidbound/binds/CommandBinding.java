package com.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.helpers.Reflector;
import com.alterego.androidbound.helpers.Reflector.CommandInfo;
import com.alterego.androidbound.interfaces.ICommand;

public class CommandBinding extends BindingBase {

    private ICommand mCommand = ICommand.empty;

    private CommandInfo mInfo;

    public static boolean isCommand(Object subject, String commandName) {
        if (subject == null) {
            return false;
        }
        return Reflector.isCommand(subject.getClass(), commandName);
    }

    public CommandBinding(Object subject, String commandName, IAndroidLogger logger) {
        super(subject, logger);

        mInfo = Reflector.getCommand(subject.getClass(), commandName);

        setupBinding();
    }

    private void setupBinding() {
        setupChanges(false);

        if (mInfo.invoker != null) {
            mCommand = new ICommand() {
                @Override
                public boolean canExecute(Object parameter) {
                    try {
                        mInfo.check(getSubject(), parameter);
                    } catch (Exception ex) {
                        getLogger().error("Error while checking command " + mInfo.name + ": " + ex.getMessage());
                    }
                    return true;
                }

                @Override
                public void execute(Object parameter) {
                    try {
                        mInfo.invoke(getSubject(), parameter);
                    } catch (Exception ex) {
                        getLogger().error("Error while raising command " + mInfo.name + ": " + ex.getMessage());
                    }
                }
            };
        }
    }

    @Override
    public Class<?> getType() {
        return ICommand.class;
    }

    @Override
    public Object getValue() {
        return mCommand;
    }

    @Override
    public void setValue(Object value) {
        getLogger().warning("Cannot set value for command " + mInfo.name);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
