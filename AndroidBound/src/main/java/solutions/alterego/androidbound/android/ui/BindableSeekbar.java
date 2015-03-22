package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.IObservable;
import solutions.alterego.androidbound.zzzztoremove.reactive.ISubject;
import solutions.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableSeekbar extends SeekBar implements INotifyPropertyChanged {
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
		return this.progressTrackBegin;
	}
	
	public void setProgressTrackBegin(ICommand cmd) {
		if(cmd == null) {
			this.progressTrackBegin = ICommand.empty;
			return;
		}
		this.progressTrackBegin = cmd;
	}
	
	public ICommand getProgressTrackEnd() {
		return this.progressTrackEnd;
	}
	
	public void setProgressTrackEnd(ICommand cmd) {
		if(cmd == null) {
			this.progressTrackEnd = ICommand.empty;
			return;
		}
		this.progressTrackEnd = cmd;
	}
	
	public ICommand getProgressTrackChanged() {
		return this.progressTrackChanged;
	}
	
	public void setProgressTrackChanged(ICommand cmd) {
		if(cmd == null) {
			this.progressTrackChanged = ICommand.empty;
		}
		this.progressTrackChanged = cmd;
	}
	
	@Override
	public IObservable<String> onPropertyChanged() {
		if(this.propertyChanged == null || this.disposed) {
			this.propertyChanged = new Subject<String>();
			this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					if(BindableSeekbar.this.disposed) {
						return;
					}
					
					if(BindableSeekbar.this.progressTrackEnd.canExecute(null)) {
						BindableSeekbar.this.progressTrackEnd.execute(null);
					}
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					if(BindableSeekbar.this.disposed) {
						return;
					}
					
					if(BindableSeekbar.this.progressTrackBegin.canExecute(null)) {
						BindableSeekbar.this.progressTrackBegin.execute(null);
					}
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if(BindableSeekbar.this.disposed) {
						return;
					}
					
					if(BindableSeekbar.this.progressTrackChanged.canExecute(null)) {
						BindableSeekbar.this.progressTrackChanged.execute(null);
					}
					
					if(BindableSeekbar.this.propertyChanged != null) {
						BindableSeekbar.this.propertyChanged.onNext("Progress");
					}
				}
			});
		}
		return this.propertyChanged;
	}

	@Override
	public void dispose() {
		if(this.disposed) {
			return;
		}
		
		this.disposed = true;
		if(this.propertyChanged != null) {
			this.propertyChanged.dispose();
		}
		
		this.propertyChanged = null;
		this.progressTrackBegin = null;
		this.progressTrackChanged = null;
		this.progressTrackEnd = null;
	}
		
	private ICommand progressTrackBegin = ICommand.empty;
	private ICommand progressTrackEnd = ICommand.empty;
	private ICommand progressTrackChanged = ICommand.empty;
	private boolean disposed;
	private ISubject<String> propertyChanged;
}
