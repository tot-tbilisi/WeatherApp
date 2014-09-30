package ge.tot.weatherapp.app.events;

/**
 * UnlockAchievementEvent
 */
public class UnlockAchievementEvent {
    private String achievementId;

    public UnlockAchievementEvent(String achievementId) {
        this.achievementId = achievementId;
    }

    public String getAchievementId() {
        return achievementId;
    }
}
