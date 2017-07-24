package solutions.alterego.androidbound.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class ReplaceAndroidViewsInLayoutsTaskTest {
    @Test
    public void should_be_able_to_add_task_to_project() {
        Project project = ProjectBuilder.builder().build();
//        project.getPluginManager().apply(AndroidBoundPlugin.class);
        project.getPlugins().apply("solutions.alterego.androidbound.plugin");

        //TODO
//        assertTrue(project.getTasks().getByName("replaceAndroidViewsInLayouts") instanceof DefaultTask);
//        assertTrue(project.getTasks().getByName("replaceAndroidViewsInLayouts") instanceof ReplaceAndroidViewsInLayouts);
    }
}