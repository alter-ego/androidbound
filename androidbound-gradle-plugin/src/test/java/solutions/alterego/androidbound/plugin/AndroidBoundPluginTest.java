package solutions.alterego.androidbound.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class AndroidBoundPluginTest {

    @Test
    public void plugin_should_add_task_to_project() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("solutions.alterego.androidbound.plugin");

        //TODO
//        assertTrue(project.getTasks().getByName("replaceAndroidViewsInLayouts") instanceof ReplaceAndroidViewsInLayouts);
//        assertTrue(project.getTasks().getByName("generateViewBindingClasses") instanceof GenerateViewBindingClasses);
    }
}