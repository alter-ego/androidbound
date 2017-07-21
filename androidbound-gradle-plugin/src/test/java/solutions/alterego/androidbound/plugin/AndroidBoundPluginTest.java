package solutions.alterego.androidbound.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AndroidBoundPluginTest {

    @Test
    public void demo_plugin_should_add_task_to_project() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("solutions.alterego.androidbound.plugin");

        assertTrue(project.getTasks().getByName("writeBindings") instanceof WriteBindingsTask);
    }
}