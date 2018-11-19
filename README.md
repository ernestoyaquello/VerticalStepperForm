# Vertical Stepper Form Library
This Android library implements a highly customizable **vertical stepper form**.

## Demo
![Demo picture](https://raw.githubusercontent.com/ernestoyaquello/VerticalStepperForm/master/stepper-example.gif)

## How To Use It
### 1. Reference The Library
Add the library to your project via Gradle:

```
dependencies {
    compile 'com.ernestoyaquello.stepperform:vertical-stepper-form:2.0.0'
}
```
**NOTE:** Make sure you are using **AndroidX** instead of the old support libraries; otherwise this library might not work.

### 2. Add The Form To Your Layout
Add the view ```VerticalStepperFormView``` to your layout using XML. For design purposes, it is recommended that you don't put anything else than this view in the layout that will contain the form:

```xml
<!-- activity_form.xml -->
<ernestoyaquello.com.verticalstepperform.VerticalStepperFormView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/stepper_form"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:form_circle_background_color="@color/colorPrimary"
    app:form_next_button_background_color="@color/colorPrimary"
    app:form_next_button_pressed_background_color="@color/colorPrimaryDark"/>
```
As you can see in this example, the properties `form_circle_background_color`, `form_next_button_background_color` and `form_next_button_pressed_background_color` are being used to configure the form. There are plenty of other ones that you can use to customize it as you please.

### 3. Define Your Steps
[TBC]

### 4. Set Up The Form And Initialize It
In ```onCreate()```, you will need to find the view of the form to set it up and initialize it:

[TBC]

### 5. Handle Configuration Changes
[TBC]


## Further Details
Check out the [sample application code](https://github.com/ernestoyaquello/VerticalStepperForm/tree/master/app/src/main/java/verticalstepperform/ernestoyaquello/com/verticalstepperform) to see a more complete example of how this library can be used to create vertical stepper forms.

## Contribution
Feel free to contribute to this library, any help will be welcomed!

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
