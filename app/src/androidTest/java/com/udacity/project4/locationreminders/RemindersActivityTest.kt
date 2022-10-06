package com.udacity.project4.locationreminders

import android.app.Application
import android.app.PendingIntent.getActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.DataBindingIdlingResource
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private val reminder1 = ReminderDTO("Title1", "Description1", "cairo1", 30.0, 30.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "cairo2", 30.0, 30.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "cairo3", 30.0, 30.0)

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        reminderDataSource = get()

        //clear the data to start fresh
        runBlocking {
            reminderDataSource.deleteAllReminders()
        }
    }

    @After
    fun done() = stopKoin()

//    TODO: add End to End testing to the app

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun saveReminder_addReminder_saveReminder() = runBlocking {
        reminderDataSource.saveReminder(reminder1)
        reminderDataSource.saveReminder(reminder2)
        reminderDataSource.saveReminder(reminder3)

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Description1")).check(matches(isDisplayed()))
        onView(withText("cairo1")).check(matches(isDisplayed()))

        onView(withText("Title2")).check(matches(isDisplayed()))
        onView(withText("Description2")).check(matches(isDisplayed()))
        onView(withText("cairo2")).check(matches(isDisplayed()))

        onView(withText("Title3")).check(matches(isDisplayed()))
        onView(withText("Description3")).check(matches(isDisplayed()))
        onView(withText("cairo3")).check(matches(isDisplayed()))

        onView(withId(R.id.addReminderFAB)).perform(click())

        onView(withId(R.id.reminderTitle)).perform(replaceText("test title"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("test description"))
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save_poi_button)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        onView(withText("test title")).check(matches(isDisplayed()))
        onView(withText("test description")).check(matches(isDisplayed()))
        onView(withText(R.string.random_location)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addReminder_saveReminder_titleErrorSnackbar() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_enter_title)))

        activityScenario.close()
    }

    @Test
    fun addReminder_saveReminder_locationErrorSnackbar() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("test title"))
        onView(withId(R.id.saveReminder)).perform(click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_select_location)))

        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save_poi_button)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        onView(withText("test title")).check(matches(isDisplayed()))
        onView(withText(R.string.random_location)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addReminder_saveReminder_reminderSavedToast() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("test title"))
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save_poi_button)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        activityScenario.onActivity {
            onView(withText(R.string.success_adding_geofence)).inRoot(
                RootMatchers.withDecorView(
                    not(
                        `is`(
                            it.window.decorView
                        )
                    )
                )
            ).check(
                matches(
                    isDisplayed()
                )
            )
            onView(withText(R.string.reminder_saved)).inRoot(
                RootMatchers.withDecorView(
                    not(
                        `is`(
                            it.window.decorView
                        )
                    )
                )
            ).check(
                matches(
                    isDisplayed()
                )
            )
        }

        onView(withText("test title")).check(matches(isDisplayed()))
        onView(withText(R.string.random_location)).check(matches(isDisplayed()))

        activityScenario.close()
    }
}