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


    public void testListItemClick() throws Exception {
        getActivity();
        onData(hasToString(startsWith("Tue"))).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.forecast_description))
                .check(matches(isDisplayed()));
    }



}
