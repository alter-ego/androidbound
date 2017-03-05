package solutions.alterego.androidbound.binding.types;

import solutions.alterego.androidbound.helpers.Reflector;
import solutions.alterego.androidbound.helpers.reflector.CommandInfo;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.ILogger;

public class CommandBinding extends BindingBase {

    private ICommand mCommand = ICommand.empty;

    private CommandInfo mInfo;

    public CommandBinding(Object subject, String commandName, ILogger logger) {
        super(subject, logger);

        mInfo = Reflector.getCommand(subject.getClass(), commandName);

        setupBinding();
    }

    public static boolean isCommand(Object subject, String commandName) {
        if (subject == null) {
            return false;
        }
        return Reflector.isCommand(subject.getClass(), commandName);
    }

    private void setupBinding() {
        setupChanges(false);

        if (mInfo.getInvokerMethod() != null) {
            mCommand = new ICommand() {
                @Override
                public boolean canExecute(Object parameter) {
                    try {
                        return mInfo.check(getSubject(), parameter);
                    } catch (Exception ex) {
                        getLogger().error("Error while checking command " + mInfo.getCommandName() + ": " + ex.getMessage());
                    }
                    return true;
                }

                @Override
                public void execute(Object parameter) {
                    try {
                        mInfo.invoke(getSubject(), parameter);
                    } catch (Exception ex) {
                        getLogger().error("Error while raising command " + mInfo.getCommandName() + ": " + ex.getMessage());
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
        getLogger().warning("Cannot set value for command " + mInfo.getCommandName());
    }

    @Override
    public void addValue(Object object) {
        getLogger().warning("Cannot add value for command " + mInfo.getCommandName());
    }

    @Override
    public void removeValue(Object result) {
        getLogger().warning("Cannot add value for command " + mInfo.getCommandName());
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
