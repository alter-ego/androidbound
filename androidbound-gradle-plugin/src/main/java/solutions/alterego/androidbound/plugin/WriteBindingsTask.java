package solutions.alterego.androidbound.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class WriteBindingsTask extends DefaultTask {

    @TaskAction
    public void writeBindingsForFiles() {
        AndroidBoundSettings extension = getProject().getExtensions().findByType(AndroidBoundSettings.class);
        if (extension == null) {
            extension = new AndroidBoundSettings();
        }

        boolean stop = extension.getStopOnMissingViewIds();
        System.out.println("AndroidBoundPlugin stopping on missing view ids = " + stop);

        //TODO
    }
}
