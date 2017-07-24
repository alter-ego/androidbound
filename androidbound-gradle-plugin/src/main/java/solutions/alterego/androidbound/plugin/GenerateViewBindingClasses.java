package solutions.alterego.androidbound.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

public class GenerateViewBindingClasses extends DefaultTask{

    private int mFileCount;

    private Iterable<File> mFilesToProcess;

    private File mOutputDirectory;

    private File mGeneratedLayoutOutputDirectory;

    @InputFiles
    public Iterable<File> getFilesToProcess() {
        return mFilesToProcess;
    }

    public void setFilesToProcess(Set<File> files) {
        mFilesToProcess = files;
        mFileCount = files.size();

        System.out.println("AndroidBoundPlugin generateViewBindingClasses files to process = " + mFileCount);
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return mOutputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        mOutputDirectory = outputDirectory;

        System.out.println("AndroidBoundPlugin generateViewBindingClasses output dir = " + mOutputDirectory);
    }

    @InputDirectory
    public File getGeneratedLayoutDirectory() {
        return mGeneratedLayoutOutputDirectory;
    }

    public void setGeneratedLayoutDirectory(File outputDirectory) {
        mGeneratedLayoutOutputDirectory = outputDirectory;

        System.out.println("AndroidBoundPlugin generateViewBindingClasses layout dir = " + mOutputDirectory);
    }

    @TaskAction
    public void generateViewBindingClasses() throws IOException {
        if (!getInputs().getHasInputs()) {
            System.out.println("AndroidBoundPlugin generateViewBindingClasses no input files!");
            return;
        }

        Java8Iterator.forEachRemaining(getInputs().getFiles().iterator(), new Consumer<File>() {
            @Override
            public void accept(File file) {
                System.out.println("AndroidBoundPlugin generateViewBindingClasses processing class = " + file.getAbsolutePath());

                //TODO check if the class is bindable
                //TODO check for existence of generated converted layout XML "filename_viewbinding.xml"
                //TODO if it has a ViewModel and if it contains bindable views that have IDs, then generate binding file {caller}_{source}_{layout}_ViewBinding
                //TODO write file contents
                //TODO add binding file constructor call to caller init
                //TODO add binding file destructor call to caller onDestroy()
                //TODO add implements IHasCompileTimeViewBindings to caller
            }
        });
    }

}
