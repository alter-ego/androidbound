package solutions.alterego.androidbound.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskCollection;

import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

public class AndroidBoundPlugin implements Plugin<Project> {

    private Project mProject;

    @Override
    public void apply(Project project) {
        mProject = project;

        AndroidBoundSettings settings = mProject.getExtensions().create("androidbound", AndroidBoundSettings.class);

        mProject.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {

                System.out.println("-----------------------------------------> AndroidBoundPlugin");

                //read & replace Android views in layouts, write new layouts
                Task replaceViews = createReplaceViewsTask(project, settings);

                //TODO delete old layout files from build folder if there's a generated layout file (removes lint warnings?)

                //generate binding files && rewrite activities that have bindings to call ViewBinder with compile-time id list
                Task generateClasses = createGenerateViewBindingsTask(project, settings);
                generateClasses.dependsOn(replaceViews);

                //TODO test generated files using google test compile?

                linkTasksToJavaSourceCompilation(project, generateClasses);

                System.out.println("AndroidBoundPlugin -----------------------------------------< ");
            }
        });
    }

    private File getLayoutOutputFolder() {
        String layoutFilesPath = mProject.getBuildDir() + "/src/main/res/layout";
        return new File(layoutFilesPath);
    }

    private Set<File> getLayoutFiles() {
        //TODO add support for tests? flavours? build types? how do we get getSourceSets()? Proto approach doesn't seem to be working:
        //https://github.com/google/protobuf-gradle-plugin/blob/master/src/main/groovy/com/google/protobuf/gradle/ProtobufPlugin.groovy

        String layoutFilesPath = mProject.getProjectDir() + "/src/main/res/layout";
        Set<File> layoutFiles = mProject.fileTree(layoutFilesPath).getFiles();

        System.out.println("AndroidBoundPlugin getLayoutFiles() size = "
                + mProject.fileTree(layoutFilesPath).getFiles().size() + " in dir = " + layoutFilesPath);

        return layoutFiles;
    }

    private File getJavaOutputFolder() {
        String layoutFilesPath = mProject.getBuildDir() + "/src/main/java";
        return new File(layoutFilesPath);
    }

    private Set<File> getJavaFiles() {
        //TODO add support for tests? flavours? build types? how do we get getSourceSets()? Proto approach doesn't seem to be working:
        //https://github.com/google/protobuf-gradle-plugin/blob/master/src/main/groovy/com/google/protobuf/gradle/ProtobufPlugin.groovy

        String javaSourceFilesPath = mProject.getProjectDir() + "/src/main/java";
        Set<File> javaFiles = mProject.fileTree(javaSourceFilesPath).getFiles();

        System.out.println("AndroidBoundPlugin getJavaFiles() files size = "
                + mProject.fileTree(javaSourceFilesPath).getFiles().size() + " in dir = " + javaSourceFilesPath);

        return javaFiles;
    }

    private Task createReplaceViewsTask(Project project, AndroidBoundSettings settings) {
        Task replaceViews = project.getTasks()
                .create("replaceAndroidViewsInLayouts", ReplaceAndroidViewsInLayouts.class, new Action<ReplaceAndroidViewsInLayouts>() {
                    public void execute(ReplaceAndroidViewsInLayouts replaceAndroidViewsInLayouts) {
                        replaceAndroidViewsInLayouts.setStopOnMissingId(settings.getStopOnMissingViewIds());
                        replaceAndroidViewsInLayouts.setFilesToProcess(getLayoutFiles());
                        replaceAndroidViewsInLayouts.setOutputDirectory(getLayoutOutputFolder());

                        replaceAndroidViewsInLayouts.setGroup("AndroidBoundPlugin");
                        replaceAndroidViewsInLayouts.setDescription("Replace android views in layouts with viewbinding views");

                        System.out.println("AndroidBoundPlugin afterEvaluate replaceAndroidViewsInLayouts");
                    }
                });

        return replaceViews;
    }

    private Task createGenerateViewBindingsTask(Project project, AndroidBoundSettings settings) {
        Task generateClasses = project.getTasks()
                .create("generateViewBindingClasses", GenerateViewBindingClasses.class, new Action<GenerateViewBindingClasses>() {
                    public void execute(GenerateViewBindingClasses generateViewBindingClasses) {
                        generateViewBindingClasses.setFilesToProcess(getJavaFiles());
                        generateViewBindingClasses.setOutputDirectory(getJavaOutputFolder());
                        generateViewBindingClasses.setGeneratedLayoutDirectory(getLayoutOutputFolder());

                        generateViewBindingClasses.setGroup("AndroidBoundPlugin");
                        generateViewBindingClasses.setDescription("Generate viewbinding class files");

                        System.out.println("AndroidBoundPlugin afterEvaluate generateViewBindingClasses");
                    }
                });

        return generateClasses;
    }

    private void linkTasksToJavaSourceCompilation(Project project, Task generateClasses) {
        TaskCollection<Task> javaCompileTasks = project.getTasks().matching(new Spec<Task>() {
            @Override
            public boolean isSatisfiedBy(Task task) {
                return task.getName().contains("java");
            }
        });

        Java8Iterator.forEachRemaining(javaCompileTasks.iterator(), new Consumer<Task>() {
            @Override
            public void accept(Task task) {
                task.dependsOn(generateClasses);
            }
        });
    }

}
