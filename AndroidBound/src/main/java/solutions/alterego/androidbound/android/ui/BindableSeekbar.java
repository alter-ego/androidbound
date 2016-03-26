package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;

public class BindableSeekbar extends SeekBar implements INotifyPropertyChanged {

    private ICommand progressTrackBegin = ICommand.empty;

    private ICommand progressTrackEnd = ICommand.empty;

    private ICommand progressTrackChanged = ICommand.empty;

    private boolean disposed;

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    public BindableSeekbar(Context context) {
        super(context);
    }

    public BindableSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BindableSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ICommand getProgressTrackBegin() {
        return progressTrackBegin;
    }

    public void setProgressTrackBegin(ICommand cmd) {
        if (cmd == null) {
            progressTrackBegin = ICommand.empty;
            return;
        }
        progressTrackBegin = cmd;
    }

    public ICommand getProgressTrackEnd() {
        return progressTrackEnd;
    }

    public void setProgressTrackEnd(ICommand cmd) {
        if (cmd == null) {
            progressTrackEnd = ICommand.empty;
            return;
        }
        progressTrackEnd = cmd;
    }

    public ICommand getProgressTrackChanged() {
        return progressTrackChanged;
    }

    public void setProgressTrackChanged(ICommand cmd) {
        if (cmd == null) {
            progressTrackChanged = ICommand.empty;
        }
        progressTrackChanged = cmd;
    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (propertyChanged == null || disposed) {
            propertyChanged = PublishSubject.create();
            setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (BindableSeekbar.this.disposed) {
                        return;
                    }

                    if (BindableSeekbar.this.progressTrackEnd.canExecute(null)) {
                        BindableSeekbar.this.progressTrackEnd.execute(null);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (BindableSeekbar.this.disposed) {
                        return;
                    }

                    if (BindableSeekbar.this.progressTrackBegin.canExecute(null)) {
                        BindableSeekbar.this.progressTrackBegin.execute(null);
                    }
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
                    if (BindableSeekbar.this.disposed) {
                        return;
                    }

                    if (BindableSeekbar.this.progressTrackChanged.canExecute(null)) {
                        BindableSeekbar.this.progressTrackChanged.execute(null);
                    }

                    if (BindableSeekbar.this.propertyChanged != null) {
                        BindableSeekbar.this.propertyChanged.onNext("Progress");
                    }
                }
            });
        }
        return propertyChanged;
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;
        if (propertyChanged != null) {
            propertyChanged.onCompleted();
        }

        propertyChanged = null;
        progressTrackBegin = null;
        progressTrackChanged = null;
        progressTrackEnd = null;
    }
}
