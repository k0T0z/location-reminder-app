package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SaveReminderViewModelTest {
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var reminderDataSource: ReminderDataSource
    private val reminder1 = ReminderDataItem("Title1", "Description1", "cairo", 30.0, 30.0)
    private val reminder2 = ReminderDataItem("Title2", "Description2", "cairo", 30.0, 30.0)
    private val reminder3 = ReminderDataItem(null, "Description3", "cairo", 30.0, 30.0)
    private val reminder4 = ReminderDataItem("Title4", "Description3", null, null, null)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        reminderDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(getApplicationContext(), reminderDataSource)
    }

    @After
    fun done() = stopKoin()

    @Test
    fun saveReminder_loadReminder() = runBlockingTest{
        saveReminderViewModel.saveReminder(reminder1)
        val reminder = reminderDataSource.getReminder(reminder1.id) as Result.Success
        assertThat(reminder.data, not(nullValue()))
    }

    @Test
    fun saveReminder_loadReminder_error() = runBlockingTest{
        saveReminderViewModel.saveReminder(reminder1)
        val reminder = reminderDataSource.getReminder(reminder2.id)
        assertEquals(reminder, Result.Error("Error reminder not found"))
    }

    @Test
    fun onClear() {
        saveReminderViewModel.onClear()
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), nullValue())
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue(), nullValue())
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(), nullValue())
        assertThat(saveReminderViewModel.selectedPOI.getOrAwaitValue(), nullValue())
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), nullValue())
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), nullValue())
    }

    @Test
    fun saveReminder_loading() {
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminder1)
        assertEquals(saveReminderViewModel.showLoading.getOrAwaitValue(), true)
        mainCoroutineRule.resumeDispatcher()
        assertEquals(saveReminderViewModel.showLoading.getOrAwaitValue(), false)
    }

    @Test
    fun saveReminder_showToast() {
        saveReminderViewModel.saveReminder(reminder1)
        assertEquals(saveReminderViewModel.showToast.getOrAwaitValue(), "Reminder Saved !")
    }

    @Test
    fun saveReminder_navigationCommand() {
        saveReminderViewModel.saveReminder(reminder1)
        assertEquals(saveReminderViewModel.navigationCommand.getOrAwaitValue(), NavigationCommand.Back)
    }

    @Test
    fun saveReminder_validateEnteredData() {
        assertEquals(saveReminderViewModel.validateEnteredData(reminder1), true)
        assertEquals(saveReminderViewModel.validateEnteredData(reminder3), false)
        assertEquals(saveReminderViewModel.validateEnteredData(reminder4), false)
    }
}