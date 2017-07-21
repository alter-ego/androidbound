package solutions.alterego.androidbound.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AndroidBoundPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("androidbound", AndroidBoundSettings.class);
        project.getTasks().create("writeBindings", WriteBindingsTask.class);
    }
}
