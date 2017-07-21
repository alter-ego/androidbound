package solutions.alterego.androidbound.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WriteBindingsTaskTest {
    @Test
    public void should_be_able_to_add_task_to_project() {
        Project project = ProjectBuilder.builder().build();
        Task task = project.task("writeBindings");

        assertTrue(task instanceof DefaultTask);
        assertTrue(task instanceof WriteBindingsTask);
    }
}