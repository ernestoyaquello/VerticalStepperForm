package ernestoyaquello.com.verticalstepperform.interfaces;

public interface OnBottomNavigationButtonClickListener {
    enum ButtonType {PREVIOUS, NEXT};
    void onNavigationButtonClick(ButtonType buttonType);
}
