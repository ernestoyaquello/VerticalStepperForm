package verticalstepperform.ernestoyaquello.com.verticalstepperform.models;

import com.google.gson.Gson;

public class Alarm {
    private final String title;
    private final String description;
    private final int timeHour;
    private final int timeMinutes;
    private final boolean[] weekDays;

    public Alarm(String title, String description, int timeHour, int timeMinutes, boolean[] weekDays) {
        this.title = title;
        this.description = description;
        this.timeHour = timeHour;
        this.timeMinutes = timeMinutes;
        this.weekDays = weekDays;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public int getTimeMinutes() {
        return timeMinutes;
    }

    public boolean[] getWeekDays() {
        return weekDays;
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public static Alarm fromSerialized(String alarmSerialized) {
        return new Gson().fromJson(alarmSerialized, Alarm.class);
    }
}
