package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.data.FakeTestRepository
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemindersListViewModelTest {

    private lateinit var remindersDataSource: FakeDataSource
//    private lateinit var remindersRepository: FakeTestRepository
    private lateinit var remindersListViewModel: RemindersListViewModel
    private val reminder1 = ReminderDTO("Title1", "Description1", "cairo", 30.0, 30.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "cairo", 30.0, 30.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "cairo", 30.0, 30.0)
    private val localReminders = listOf(reminder1, reminder2, reminder3)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        remindersDataSource = FakeDataSource(localReminders.toMutableList())
//        remindersRepository = FakeTestRepository()
    }

    @Test
    fun loadReminders_fromDB() {
//        remindersRepository.addReminders(reminder1, reminder2, reminder3)

        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersDataSource)

//        remindersListViewModel.loadReminders()

//        val value = remindersListViewModel.remindersList.getOrAwaitValue()

    }
}