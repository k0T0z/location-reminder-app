package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.data.FakeDataSource
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
    private val localReminders = listOf(reminder1, reminder2, reminder3).sortedBy { it.id }
    private val newReminders = listOf(reminder3).sortedBy { it.id }

    private lateinit var remindersLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var remindersRepository: RemindersLocalRepository

    @Before
    fun createRepository() {
        remindersLocalDataSource = FakeDataSource(localReminders.toMutableList())
        remindersRepository = RemindersLocalRepository(
            remindersLocalDataSource, Dispatchers.Unconfined
        )
    }


    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
        val reminders = remindersRepository.getReminders() as Result.Success
        assertThat(reminders.data, IsEqual(localReminders))
    }

}