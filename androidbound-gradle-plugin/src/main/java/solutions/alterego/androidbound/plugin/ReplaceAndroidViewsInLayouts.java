package solutions.alterego.androidbound.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

public class ReplaceAndroidViewsInLayouts extends DefaultTask {

    private boolean mStopOnMissingId = false;

    private int mFileCount;

    private Iterable<File> mFilesToProcess;

    private File mOutputDirectory;

    @Optional
    @Input
    public boolean getStopOnMissingId() {
        return mStopOnMissingId;
    }

    public void setStopOnMissingId(boolean stopOnMissingId) {
        mStopOnMissingId = stopOnMissingId;
    }

    @InputFiles
    public Iterable<File> getFilesToProcess() {
        return mFilesToProcess;
    }

    public void setFilesToProcess(Set<File> files) {
        mFilesToProcess = files;
        mFileCount = files.size();

        System.out.println("AndroidBoundPlugin replaceAndroidViewsInLayouts files to process = " + mFileCount);
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return mOutputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        mOutputDirectory = outputDirectory;

        System.out.println("AndroidBoundPlugin replaceAndroidViewsInLayouts output dir = " + mOutputDirectory);
    }

    @TaskAction
    public void lookForAndroidViewsAndReplace() throws IOException {
        if (!getInputs().getHasInputs()) {
            System.out.println("AndroidBoundPlugin replaceAndroidViewsInLayouts no input files!");
            return;
        }

        Java8Iterator.forEachRemaining(getInputs().getFiles().iterator(), new Consumer<File>() {
            @Override
            public void accept(File file) {
                System.out.println("AndroidBoundPlugin replaceAndroidViewsInLayouts processing file = " + file.getAbsolutePath());

                //TODO find Android views and replace them with binding views if they have an ID and save them in a file filename_viewbinding.xml
                //TODO if there are missing IDs, print the file name and line and view classes with missing IDs in the file
                //TODO if mStopOnMissingId is set and there are missing IDs, stop processing
            }
        });
    }

}
