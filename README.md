# Vertical Stepper Form Library
This Android library implements a [**vertical stepper form**](https://material.google.com/components/steppers.html) following Google Material Design guidelines.

## Demo
![Demo picture](https://raw.githubusercontent.com/ernestoyaquello/vertical-stepper-form/master/stepper-example.gif)
### Example application
Take a look at the [example application code](https://github.com/ernestoyaquello/vertical-stepper-form/tree/master/app/src/main/java/verticalstepperform/ernestoyaquello/com/verticalstepperform) if you wish.

## Installation and usage
1. To include the library in your project, first add it via Gradle:

	```
	dependencies {
		compile 'com.ernestoyaquello.stepperform:vertical-stepper-form:0.9.3'
	}
	```
2. Now, you have to add a ```VerticalStepperFormLayout``` view to your activity layout. This view will contain the vertical stepper form. For design purposes, it is recommended that you don't put anything else than this view in your activity layout (see the code below).

  ```xml
  <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent" android:layout_height="match_parent"
      tools:context=".StepperExampleActivity">
  
      <ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout
          android:id="@+id/vertical_stepper_form"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:layout_alignParentTop="true"/>
  
  </RelativeLayout>
  ```
3. In ```onCreate()```, you will need to find the view and call ```initialiseVerticalStepperForm()```:

  ```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.your_activity_layout);
      
      VerticalStepperFormLayout verticalStepperForm = 
          (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
      String[] mySteps = {"Name", "Email", "Phone Number"};
      int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
      int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
      
      verticalStepperForm.initialiseVerticalStepperForm(mySteps, colorPrimary, colorPrimaryDark, this, this);
      
      ...
      
  }
  ```
  NOTE: In this step you may need need to import ```ernestoyaquello.com.verticalstepperform.*```.

4. Finally, edit your activity class to make it implement ```VerticalStepperForm```. Implement the methods ```createStepContentView()```, ```onStepOpening()``` and ```sendData()```.


###Implementing the methods
####createStepContentView()
This method will be called automatically by the system to generate the view of the content of each step. You have to implement the generation of the corresponding step view and return it:
```java
@Override
protected View createStepContentView(int stepNumber) {
	View view = null;
	switch (stepNumber) {
		case 0:
			view = createNameStep();
			break;
		case 1:
			view = createEmailStep();
			break;
		case 2:
			view = createPhoneNumberStep();
			break;
	}
	return view;
}

private View createNameStep() {
	// Here we generate programmatically the view that will be added by the system to the step content layout
	EditText name = new EditText(this);
	name.setSingleLine(true);
	name.setHint("Your name");
	name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			...
			return false;
		}
	});
	return name;
}

private View createEmailStep() {
	// In this case we generate the view by inflating a XML file
	LayoutInflater inflater = LayoutInflater.from(getBaseContext());
	LinearLayout emailLayoutContent = (LinearLayout) inflater.inflate(R.layout.email_step_layout, null, false);
	EditText email = (EditText) emailLayoutContent.findViewById(R.id.email);
	...
	return emailLayoutContent;
}

private View createPhoneNumberStep() {
	LayoutInflater inflater = LayoutInflater.from(getBaseContext());
	LinearLayout phoneLayoutContent = (LinearLayout) inflater.inflate(R.layout.phone_step_layout, null, false);
	...
	return phoneLayoutContent;
}
```


####onStepOpening()
This method will be called every time a step is open, so it can be used for checking conditions. It is noteworthy that the button "Continue" is disabled by default in every step and it only shows up after certain user actions (for example, the introduction of a correct name or email):
```java
@Override
protected void onStepOpening(int stepNumber) {
	switch (stepNumber) {
		case 0: 
			checkName();
			break;
		case 1:
			checkEmail();
			break;
		case 2: 
			// As soon as the phone number step is open, we mark it as completed in order to show the "Continue"
			// button (We do this because this field is optional, so the user can skip it without giving any info)
			verticalStepperForm.setStepAsCompleted(2);
			// In this case, equivalent to: verticalStepperForm.setActiveStepAsCompleted();
			break;
	}
}

private void checkName() {
	if(name.length() >= MIN_CHARACTERS_NAME && name.length() <= MAX_CHARACTERS_NAME) {
		verticalStepperForm.setActiveStepAsCompleted();
	} else {
		verticalStepperForm.setActiveStepAsUncompleted();
	}
}

private void checkEmail() {
	...
}
```
NOTE: You can also use this method to trigger some actions whenever a certain step is open.

####sendData()
In this method you have to implement the sending of the data.

### Screen rotation
This library handles screen rotation by saving and restoring the state of the form. Therefore, if you want to use ```onSaveInstanceState()``` and ```onRestoreInstanceState()```, don't forget to call ```super()``` **at the end**:
```java
@Override
public void onSaveInstanceState(Bundle savedInstanceState) {
	...
	super.onSaveInstanceState(savedInstanceState);
}

@Override
public void onRestoreInstanceState(Bundle savedInstanceState) {
	...
	super.onRestoreInstanceState(savedInstanceState);
}
```

### Further details
Check out the [example application code](https://github.com/ernestoyaquello/vertical-stepper-form/tree/master/app/src/main/java/verticalstepperform/ernestoyaquello/com/verticalstepperform).

## Contribution
Feel free to contribute to this library and help to improve it :)

## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
