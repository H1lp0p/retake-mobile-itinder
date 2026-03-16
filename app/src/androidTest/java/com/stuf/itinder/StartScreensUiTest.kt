package com.stuf.itinder

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.IdlingRegistry
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartScreensUiTest {

    @Before
    fun registerIdlingResources() {
        AnimationsTrackerHolder.instance = TestAnimationsTracker
        IdlingRegistry.getInstance().register(TestAnimationsTracker.idling)
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(TestAnimationsTracker.idling)
        AnimationsTrackerHolder.instance = NoOpAnimationsTracker
    }

    /**
     * 1. Отображение основных элементов Welcome-экрана.
     */
    @Test
    fun welcomeScreen_displaysMainElements() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.Logo)).check(matches(isDisplayed()))
        onView(withId(R.id.textView)).check(matches(isDisplayed()))
        onView(withId(R.id.ImageBg)).check(matches(isDisplayed()))

        onView(withId(R.id.RegisterButton))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.LoginButton))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    /**
     * 2. Кнопка «Назад» на экране регистрации возвращает на Welcome.
     */
    @Test
    fun register_backButton_returnsToWelcome() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.RegisterButton)).perform(click())

        onView(withId(R.id.BackBtn)).perform(click())

        onView(withId(R.id.Logo)).check(matches(isDisplayed()))
        onView(withId(R.id.RegisterButton)).check(matches(isDisplayed()))
        onView(withId(R.id.LoginButton)).check(matches(isDisplayed()))
    }

    /**
     * 3. Системная кнопка Back возвращает на Welcome с экрана регистрации.
     */
    @Test
    fun register_systemBack_returnsToWelcome() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.RegisterButton)).perform(click())

        pressBack()

        onView(withId(R.id.Logo)).check(matches(isDisplayed()))
        onView(withId(R.id.RegisterButton)).check(matches(isDisplayed()))
        onView(withId(R.id.LoginButton)).check(matches(isDisplayed()))
    }

    /**
     * 4. Навигация на главный экран через регистрацию и экран "О себе".
     */
    @Test
    fun registrationFlow_navigatesToMainScreen() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.RegisterButton)).perform(click())

        onView(withId(R.id.RegisterButton)).perform(click())

        onView(withId(R.id.PlaceholderBtn)).perform(click())

        onView(
            allOf(
                withId(R.id.Title),
                withText(R.string.main_screen_title)
            )
        ).check(matches(isDisplayed()))
    }
}
