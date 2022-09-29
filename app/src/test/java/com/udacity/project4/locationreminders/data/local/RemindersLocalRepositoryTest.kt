package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.data.FakeTestRepository
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RemindersLocalRepositoryTest {
    private val reminder1 = ReminderDTO("Title1", "Description1", "cairo", 30.0, 30.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "cairo", 30.0, 30.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "cairo", 30.0, 30.0)
    private val localReminders = listOf(reminder1, reminder2, reminder3)

    // Class under test
    private lateinit var remindersRepository: FakeTestRepository

    @Before
    fun createRepository() {
        remindersRepository = FakeTestRepository()
    }


    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
        remindersRepository.saveReminder(reminder1)
        remindersRepository.saveReminder(reminder2)
        remindersRepository.saveReminder(reminder3)
        val reminders = remindersRepository.getReminders() as Result.Success
        assertThat(reminders.data.toList(), IsEqual(localReminders))
    }

}