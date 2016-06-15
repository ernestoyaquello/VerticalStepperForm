package ernestoyaquello.com.verticalstepperform.utils;

import android.content.res.ColorStateList;
import android.support.v7.widget.AppCompatButton;

public class Appearance {

    public static void setButtonColor(AppCompatButton button, int buttonColor, int buttonTextColor,
                                      int buttonPressedColor, int buttonPressedTextColor) {
        int[][] states = new int[][]{
                new int [] {android.R.attr.state_pressed},
                new int [] {android.R.attr.state_focused},
                new int [] {}
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
        button.setSupportBackgroundTintList(buttonColours);
        button.setTextColor(buttonTextColours);
    }

}
