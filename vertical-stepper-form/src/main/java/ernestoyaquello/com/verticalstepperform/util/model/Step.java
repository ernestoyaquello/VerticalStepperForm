package ernestoyaquello.com.verticalstepperform.util.model;

public class Step {

    private String title;
    private String subtitle;
    private String buttonText;

    public Step(String title) {
        this(title, null);
    }

    public Step(String title, String subtitle) {
        this(title, subtitle, null);
    }

    public Step(String title, String subtitle, String buttonText) {
        this.title = title;
        this.subtitle = subtitle;
        this.buttonText = buttonText;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getSubtitle() {
        return subtitle != null ? subtitle : "";
    }

    public String getButtonText() {
        return buttonText;
    }
}
