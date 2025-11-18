package com.example.tp_final_v1;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SecondActivityTest {

    @Before
    public void setUp() {
        Intents.init();
        ActivityScenario.launch(SecondActivity.class);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void botonMoney_abreMoneyConverter() {

        onView(withId(R.id.btnConversor)).perform(click());

        intended(hasComponent(MoneyConvertor.class.getName()));
    }
}