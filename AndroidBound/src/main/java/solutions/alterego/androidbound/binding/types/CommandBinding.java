package solutions.alterego.androidbound.binding.types;

import android.view.View;

import solutions.alterego.androidbound.helpers.Reflector;
import solutions.alterego.androidbound.helpers.reflector.CommandInfo;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.ILogger;

public class CommandBinding extends BindingBase {

    private final boolean mDebugMode;

    private ICommand mCommand = ICommand.empty;

    private CommandInfo mInfo;

    public CommandBinding(Object subject, String commandName, ILogger logger, boolean debugMode) {
        super(subject, logger);
        mDebugMode = debugMode;

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
                        if (mDebugMode) {
                            throw new RuntimeException(ex);
                        }
                    }
                    return true;
                }

                @Override
                public void execute(Object parameter) {
                    try {
                        mInfo.invoke(getSubject(), parameter);
                    } catch (Exception ex) {
                        getLogger().error("Error while raising command " + mInfo.getCommandName() + ": " + ex.getMessage());
                        if (mDebugMode) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

                @Override
                public boolean canExecute(View view, Object parameter) {
                    try {
                        return mInfo.check(getSubject(), view, parameter);
                    } catch (Exception ex) {
                        getLogger().error("Error while checking command " + mInfo.getCommandName() + ": " + ex.getMessage());
                        if (mDebugMode) {
                            throw new RuntimeException(ex);
                        }
                    }
                    return true;
                }

                @Override
                public void execute(View view, Object parameter) {
                    try {
                        mInfo.invoke(getSubject(), view, parameter);
                    } catch (Exception ex) {
                        getLogger().error("Error while raising command " + mInfo.getCommandName() + ": " + ex.getMessage());
                        retryExecute(view, parameter);
                    }
                }

                private void retryExecute(View parameter, Object parameter2) {
                    try {
                        mInfo.invoke(getSubject(), parameter2);
                    } catch (Exception e) {
                        execute(parameter);
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
        String msg = "Cannot set value for command " + mInfo.getCommandName();
        getLogger().warning(msg);
        if (mDebugMode) {
            throw new RuntimeException(msg);
        }
    }

    @Override
    public void addValue(Object object) {
        String msg = "Cannot add value for command " + mInfo.getCommandName();
        getLogger().warning(msg);
        if (mDebugMode) {
            throw new RuntimeException(msg);
        }
    }

    @Override
    public void removeValue(Object result) {
        String msg = "Cannot remove value for command " + mInfo.getCommandName();
        getLogger().warning(msg);
        if (mDebugMode) {
            throw new RuntimeException(msg);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
