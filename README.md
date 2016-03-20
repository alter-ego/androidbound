#What is AndroidBound?

This library offers easy view bindings for Android.

#The what now?
View bindings - you know, so that you don't have to keep writing `TextView tv = findViewById(R.id.texView)` and then `tv.setText("some text")` and then later `tv.setText("some new text")`. It can also automatically update the views or be bound in the other direction so that you can receive events in your app that come from the view.

The basic idea is similar to MVP model, except that this is MVVM - Model (data), View (layout) and ViewModel (object that communicates between the data to the view). The difference between MVP and MVVM is that the ViewModel introduces the concept of a binder - which uses automatic mechanisms to connect to views - so that the programmer doesn't have to write lots of boilerplate code to connect the views to the data. Otherwise, logically, it's very similar to MVP and MVC in general.  

#Why?
Activity then only needs to take care of its lifecycle and the external events (onNewIntent, onActivityResult) and funnel them to the ViewModel. That way, the Android stuff remains in the Activity and the data logic is in the ViewModel. Combined with losing lots of boilerplate code managing the views, you will have smaller classes that are easier to write, test and maintain with clearly separated responsibilities.  

This approach leads to cleaner code and enables you to separate the development (you can have one person working on the layouts and another on the ViewModels). Also, should you change the idea and want to switch from Activities to Fragments or vice versa, it's easier because the logic is separated already. 

It's easier to test that way because you can write quick unit tests for most of the code, use slower integration tests for Android things, and then if those work, the problems can only arise in the layouts so UI tests lose the importance they currently have on Android.

#What is in there then?
Regarding bindings, this library supports one-way and two-way bindings (from ViewModel to View, from View to ViewModel, and both at the same time).
Regarding views, most of the Android UI widgets are supported (with a notable exception of RecyclerView, which is in our roadmap). Regarding events, most of the widget methods are supported, like setting text, color, background color, items for ListViews etc.

Sooner or later, we will write a complete list of supported Views and events. In the meantime, you can check the code and if you think something's missing, either open an issue or fork it, write it and open a pull request.

#How does it work internally?
It's magic. 

#No, really, how does it work internally?
It reads the XML, parses the binding string, then through reflection finds the methods and properties in the ViewModel and then uses RxJava Observer/Observable pattern to subscribe to changes and subsequently notify the ViewModel or change the widget states. 

#Isn't that slow?
Well, it's slower than not using reflection, yes. But it isn't that noticeable. The main advantage is that writing code is quicker then normally, so it's very useful for smaller and mid-sized apps. For large, complicated apps that already needs lots of memory and are generally slow, this will probably make things worse. But at that point you probably don't care about the speed that much. ;)

Anyway, we don't have any benchmarks currently. Feel free to do some and send them to us.

#So how is this different from Android Data Binding? 
Compared to [Android Data Binding](http://developer.android.com/tools/data-binding/guide.html), the execution is similar but the idea is different. The binding implementation is pretty similar, but they don't solve the problem of a 1K+-line Activity, they just shuffle those lines to the layout, so you will have 500-line activity and 500-line layout, with logic divided between them, somehow. It takes the [Single Reponsibility Principle](https://en.wikipedia.org/wiki/Single_responsibility_principle), shoots it in the head, pisses on its corpse, goes to its funeral and then kills everyone present.  

AndroidBound doesn't let you put logic in the views because the logic should in the ViewModel. Again - Android stuff in the Activity/Fragment, logic (minus viewbinding boilerplate) in the ViewModel.

#OK, you convinced me. How do I use this?
Here's a quick guide.

1. Setup the Activity and ViewModel.

	**If you extend normal (non-Binding) Activities:**

		IAndroidLogger logger = new DetailedAndroidLogger(LOGGING_TAG, LOGGING_LEVEL);
		ViewBinder viewBinder = new ViewBinder(this, logger, null);
		ViewModel viewModel = new MainActivityViewModel(this, logger);

		View view = viewBinder.inflate(this, viewModel, R.layout.activity_main, null);
		setContentView(view);

	**If you extend a Binding Activity, like BindingAppCompatActivity:**

		IAndroidLogger logger = new DetailedAndroidLogger(LOGGING_TAG, LOGGING_LEVEL);
        ViewBinder viewBinder = new ViewBinder(this, logger, null);
		ViewModel viewModel = new MainBindableActivityViewModel(this, logger);

        setViewBinder(viewBinder);
        setBoundData(viewModel);
        setContentView(R.layout.activity_bindable_main);


2. Add binding to you widget in the layout file, e.g.

		<TextView
            android:id="@+id/activity_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            binding="{ Text @= MainActivityTitle }; { TextColor @= MainActivityTitleColor }"/>

3. In your ViewModel, either add the property called `MainActivityTitle`, like this:

		public String MainActivityTitle = "Title";

	or add the accessor method starting with get:

		public String getMainActivityTitle() {
			return "Title";
		}

	When the view is initialized, it will automatically pass this value to the TextView. If you want to refresh it later, you should call `raisePropertyChanged("MainActivityTitle")` and the TextView will receive the new value of the bound property (`MainActivityTitle`) or it will call the bound method again (`getMainActivityTitle()`). For example, this is what a setter would like for the property:

		public void setMainActivityTitle(String title) {
			MainActivityTitle = title;
			raisePropertyChanged("MainActivityTitle");
		}

4. When you don't need the ViewModel any more, simply call `ViewModel.dispose()` which should release all the references and unsubscribe from all the subscriptions.

For more details, please see the example. Wiki should be coming, sooner or later.

#It doesn't work!!!
Yes it does. 

1. Check that the binding string in the widget is correct: you start a binding declaration with `{` and close it with `}`. If you want to put more of them, put `;` between them. Don't forget to enclose them in `"`. Check that the bound widget property is correct (e.g. `Text`, `Color` etc.). Check that the binding command is correctly written (`-` for one-time binding, `=` for continuous binding; `@` on one or both sides; when in doubt, just put `=` and it should work).

1. Check that the declared bound property/method corresponds to the property name or properly-prefixed method name (`is`, `get`, `set` for values, `can` & `do` couple for commands + declared name).

1. Check that the value is actually set by the time the binder makes a pass (it's either initialized when instantiating a ViewModel or you have to call `raisePropertyChanged` when set later). 

#Who wrote this best library ever/biggest peace of crap ever?

Most of the code in the first version was written by [Fabrizio Chignoli](https://github.com/lazyoft) even though the commits don't show it. Then [Ivan Morgillo](https://github.com/hamen) and [Sasa Sekulic](https://github.com/mrsasha) made things worse.

## COMPATIBILITY

This library was tested with the version 23.2.1 of the support libary. It hasn't been tested with the design support library. Also, there's no support for the RecyclerView.

## ROADMAP

**0.6** First public release

**0.7** Add support for RecyclerView. Add support for multiple ViewModels in Binding activities.

**0.x** Extract Universal Image Loader and add support for other image-loading libraries like Picasso and Fresco.

_Think we're missing something? Open an issue!_

## LICENSE

This library is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).