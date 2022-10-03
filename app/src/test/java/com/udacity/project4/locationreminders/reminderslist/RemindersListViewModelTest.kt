package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
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
class RemindersListViewModelTest {
    private lateinit var remindersDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private val reminder1 = ReminderDTO("Title1", "Description1", "cairo", 30.0, 30.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "cairo", 30.0, 30.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "cairo", 30.0, 30.0)
    private val localReminders = listOf(reminder1, reminder2, reminder3)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        remindersDataSource = FakeDataSource(localReminders.toMutableList())
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersDataSource)
    }

    @After
    fun done() = stopKoin()

    @Test
    fun loadReminders() {
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue(), not(nullValue()))
    }

    @Test
    fun loadNothing() {
        remindersDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersDataSource)
        remindersListViewModel.loadReminders()
        assertEquals(remindersListViewModel.remindersList.getOrAwaitValue(), emptyList<ReminderDataItem>())
    }

    @Test
    fun loadReminders_loading() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), true)
        mainCoroutineRule.resumeDispatcher()
        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), false)
    }

    @Test
    fun loadReminders_noData() {
        remindersDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersDataSource)
        remindersListViewModel.loadReminders()
        assertEquals(remindersListViewModel.showNoData.getOrAwaitValue(), true)
    }

    @Test
    fun loadReminders_yesData() {
        remindersListViewModel.loadReminders()
        assertEquals(remindersListViewModel.showNoData.getOrAwaitValue(), false)
    }

    @Test
    fun loadReminders_error() {
        remindersDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        assertEquals(remindersListViewModel.showSnackBar.getOrAwaitValue(), "Test exception")
    }

}