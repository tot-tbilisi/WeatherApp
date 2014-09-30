package ge.tot.weatherapp;

import android.test.ActivityInstrumentationTestCase2;

import ge.tot.weatherapp.ui.MyActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;

/**
 * BasicUITest
 */
public class BasicUITest extends ActivityInstrumentationTestCase2<MyActivity> {

    public BasicUITest() {
        super(MyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testListItemClick() throws Exception {
        onData(hasToString(startsWith("Tue"))).perform(click());
        onView(withId(R.id.forecast_description))
                .check(matches(isDisplayed()));
    }

    public void testListItemClick2() throws Exception {
        onData(hasToString(startsWith("Wed"))).perform(click());
        onView(withId(R.id.forecast_description))
                .check(matches(isDisplayed()));
    }



}
