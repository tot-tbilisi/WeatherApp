package ge.tot.weatherapp.app.events;

/**
 * IncrementAchievementEvent
 */
public class IncrementAchievementEvent {
    private String achievementId;

    public IncrementAchievementEvent(String achievementId) {
        this.achievementId = achievementId;
    }

    public String getAchievementId() {
        return achievementId;
    }
}
