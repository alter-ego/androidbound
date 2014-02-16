package com.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.helpers.Reflector;
import com.alterego.androidbound.helpers.Reflector.CommandInfo;
import com.alterego.androidbound.interfaces.ICommand;

public class CommandBinding extends BindingBase {
	private ICommand command = ICommand.empty;
	private CommandInfo info;
	
	public static boolean isCommand(Object subject, String commandName) {
		if (subject == null) {
			return false;
		}
		
		return Reflector.isCommand(subject.getClass(), commandName);
	}

	public CommandBinding(Object subject, String commandName, IAndroidLogger logger) {
		super(subject, logger);
		
		this.info = Reflector.getCommand(subject.getClass(), commandName);

		setupBinding();
	}

	private void setupBinding() {
		this.setupChanges(false);

		if(this.info.invoker != null) {
			this.command = new ICommand() {
				@Override
				public boolean canExecute(Object parameter) {
					try {
						info.check(getSubject(), parameter);
					} catch(Exception ex) {
						getLogger().error("Error while checking command " + info.name + ": " + ex.getMessage());
					}
					return true;
				}

				@Override
				public void execute(Object parameter) {
					try {
						info.invoke(getSubject(), parameter);
					} catch(Exception ex) {
						getLogger().error("Error while raising command " + info.name + ": " + ex.getMessage());
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
		return command;
	}

	@Override
	public void setValue(Object value) {
		getLogger().warning("Cannot set value for command " + info.name);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
