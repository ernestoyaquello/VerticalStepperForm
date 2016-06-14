# Vertical Stepper Form Library
This Android library implements a [**vertical stepper form**](https://material.google.com/components/steppers.html) following Google Material Design guidelines.

## Demo
![Demo picture](http://i.imgur.com/pSNKLFe.gif)

## Installation and usage
1. To add the library to your project, write **```compile 'com.ernestoyaquello.stepperform:vertical-stepper-form:0.7.1'```** in your Gradle configuration file.
2. Now, you have to add a ```VerticalStepperFormLayout``` view to your activity layout. This view will contain the vertical stepper form. For design purposes, it is recommended that you don't put anything else than this view in your layout (see the code below).

  ```xml
  <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent" android:layout_height="match_parent">
  
      <ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout
          android:id="@+id/vertical_stepper_form"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:layout_alignParentTop="true"/>
  
  </RelativeLayout>
  ```
3. In your activity Java class, you need to add some lines to ```onCreate()``` in order to initialize the stepper form:

  ```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.your_activity_layout);
      
      // We need to add these lines of code to initialize the stepper form
      verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
      if(verticalStepperForm != null) {
          // Define the names of your fields/steps
          String[] mySteps = {"Name", "Email", "Phone Number"}; 
          // Add the steps to the stepper form
          verticalStepperForm.setSteps(mySteps);
          // Initialize the stepper
          initStepperForm();
      }
      
      ...
      
  }
  ```
4. Edit your activity class to make it extend ```VerticalStepperFormBaseActivity```.
5. Finally, you will have to implement the methods ```createCustomStep()```, ```customStepsCheckingOnStepOpening()``` and ```sendData()```.

####Library methods
#####```createCustomStep()```
This method will be called automatically by the system to generate the view of each step. You have to implement the generation of the corresponding step view and return it:
```java
@Override
protected View createCustomStep(int numStep, RelativeLayout stepContent) {
	View view = null;
	switch (numStep) {
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
	// We generate programmatically the view that will be added by the system to the step content layout
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
	// We generate the view by inflating a XML file
	LayoutInflater inflater = LayoutInflater.from(getBaseContext());
	LinearLayout emailLayoutContent = (LinearLayout) inflater.inflate(R.layout.email_step_layout, null, false);
	EditText email = (EditText) emailLayoutContent.findViewById(R.id.email);
	...
	return emailLayoutContent;
}

private View createPhoneNumberStep() {
	// We generate the view by inflating a XML file
	LayoutInflater inflater = LayoutInflater.from(getBaseContext());
	LinearLayout phoneLayoutContent = (LinearLayout) inflater.inflate(R.layout.phone_step_layout, null, false);
	...
	return phoneLayoutContent;
}
```
