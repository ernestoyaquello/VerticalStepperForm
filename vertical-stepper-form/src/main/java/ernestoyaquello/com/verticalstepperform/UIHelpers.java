package ernestoyaquello.com.verticalstepperform;

import android.content.res.ColorStateList;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;

class UIHelpers {

    static void setButtonColor(
            AppCompatButton button,
            int buttonColor,
            int buttonTextColor,
            int buttonPressedColor,
            int buttonPressedTextColor) {

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{}
        };
        ColorStateList buttonColours = new ColorStateList(
                states,
                new int[]{
                        buttonPressedColor,
                        buttonPressedColor,
                        buttonColor
                });
        ColorStateList buttonTextColours = new ColorStateList(
                states,
                new int[]{
                        buttonPressedTextColor,
                        buttonPressedTextColor,
                        buttonTextColor
                });
        ViewCompat.setBackgroundTintList(button, buttonColours);
        button.setTextColor(buttonTextColours);
    }
}
