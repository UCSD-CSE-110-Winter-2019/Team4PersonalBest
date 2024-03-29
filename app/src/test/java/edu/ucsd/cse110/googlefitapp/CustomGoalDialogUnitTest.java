package edu.ucsd.cse110.googlefitapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Button;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import edu.ucsd.cse110.googlefitapp.dialog.CustomGoalDialog;
import edu.ucsd.cse110.googlefitapp.fitness.FitnessService;
import edu.ucsd.cse110.googlefitapp.fitness.FitnessServiceFactory;
import edu.ucsd.cse110.googlefitapp.fitness.GoogleFitnessServiceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class CustomGoalDialogUnitTest {
    private static final String TEST_SERVICE = "TEST_CUSTOM_GOAL_DIALOG";
    private Button setGoalBtn;
    private DialogFragment dialogFragment;
    private MainActivity activity;

    @Before
    public void setUp() {
        FitnessServiceFactory googleFitnessServiceFactory = new GoogleFitnessServiceFactory();

        googleFitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(Activity mainActivity) {
                return new TestFitnessService(mainActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra("TEST", true);
        intent.putExtra("TEST_SERVICE_MAIN", TEST_SERVICE);
        intent.putExtra("TEST_SERVICE_STEP_COUNT", TEST_SERVICE);
        activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
        setGoalBtn = activity.findViewById(R.id.btnSetGoal);
        setGoalBtn.performClick();
        dialogFragment = (DialogFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("fragment_set_goal");
    }

    @Test
    public void testCustomGoalDialogIsShown() {
        assertNotNull(dialogFragment);
        assertTrue(dialogFragment.getShowsDialog());
    }

    @Test
    public void testCustomGoalDialogCancelable() {
        assertTrue(dialogFragment.isCancelable());
    }

    @Test
    public void testCustomGoalDialogBindToMain() {
        assertNotNull(dialogFragment.getActivity());
        assertEquals(activity, dialogFragment.getContext());
    }

    @Test
    public void testCustomGoalDialogTag() {
        assertEquals("fragment_set_goal", dialogFragment.getTag());
    }

    @Test
    public void testNullTitle() {
        CustomGoalDialog goalDialog = CustomGoalDialog.newInstance(null);
        assertNull(goalDialog);
    }

    @Test
    public void testTitle() {
        CustomGoalDialog goalDialog = CustomGoalDialog.newInstance("title");
        Bundle bundle = goalDialog.getArguments();
        Assert.assertNotNull(bundle);
        String title = bundle.getString("title");
        assertEquals("title", title);
    }

    private class TestFitnessService implements FitnessService {
        private static final String TAG = "[TestStepCountActFitnessService]: ";
        private Activity mainActivity;

        public TestFitnessService(Activity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public int getRequestCode() {
            return 0;
        }

        @Override
        public void setup() {
            Log.d(TAG, "setup");
        }

        @Override
        public void updateStepCount() {
        }

        @Override
        public void stopAsync() {
        }

        @Override
        public void startAsync() {
        }

        @Override
        public boolean hasPermission() {
            return false;
        }

        @Override
        public void addInactiveSteps(int extraStep) {
        }

        @Override
        public void addActiveSteps(int step, int min, int sec, float stride) {

        }

        @Override
        public String getUID() {
            return null;
        }

        @Override
        public String getEmail() {
            return null;
        }
    }
}
