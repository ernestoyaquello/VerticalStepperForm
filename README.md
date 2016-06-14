# Vertical Stepper Form Library
This Android library implements a [**vertical stepper form**](https://material.google.com/components/steppers.html) following Google Material Design guidelines.

## Demo
![Demo picture](http://i.imgur.com/pSNKLFe.gif)

## Installation and usage
1. To add the library to your project, write **```compile 'com.ernestoyaquello.stepperform:vertical-stepper-form:0.7.0'```** in your Gradle configuration file.
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
      // We need to use these lines of code to initialize the stepper form
      verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
      if(verticalStepperForm != null) {
          verticalStepperForm.setSteps(stepsStrings);
          initStepperForm();
      }
  }
  ```
4. Finally, to make it work, your activity class must extend ```VerticalStepperFormBaseActivity```. After doing so, you will have to implement the methods ```addCustomStep()```, ```customStepsCheckingOnStepOpening()``` and ```sendData()``` in the activity.

####Library methods
Text not finished...
