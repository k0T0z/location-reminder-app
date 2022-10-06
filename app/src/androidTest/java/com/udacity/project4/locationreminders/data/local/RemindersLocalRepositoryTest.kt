package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeAndroidTestRepository
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    private val reminder1 = ReminderDTO("Title1", "Description1", "cairo", 30.0, 30.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "cairo", 30.0, 30.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "cairo", 30.0, 30.0)
    private val localReminders = listOf(reminder1, reminder2, reminder3)

    // Class under test
    private lateinit var database: RemindersDatabase
    private lateinit var remindersRepository: RemindersLocalRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Unconfined
        )
    }

    @After
    fun done() = database.close()

    @Test
    fun getReminders_requestsAllRemindersFromLocalRepository() = runBlocking {
        remindersRepository.saveReminder(reminder1)
        remindersRepository.saveReminder(reminder2)
        remindersRepository.saveReminder(reminder3)
        val reminders = remindersRepository.getReminders() as Result.Success
        Assert.assertThat(reminders.data.toList(), IsEqual(localReminders))
    }

    @Test
    fun getReminders_requestsEmptyListFromLocalRepository() = runBlocking {
        val reminders = remindersRepository.getReminders() as Result.Success
        Assert.assertThat(reminders.data.toList(), IsEqual(emptyList<ReminderDTO>()))
    }

    @Test
    fun deleteReminders_requestsEmptyListFromLocalRepository() = runBlocking {
        remindersRepository.saveReminder(reminder1)
        remindersRepository.saveReminder(reminder2)
        remindersRepository.saveReminder(reminder3)
        var reminders = remindersRepository.getReminders() as Result.Success
        Assert.assertThat(reminders.data.toList(), IsEqual(localReminders))
        remindersRepository.deleteAllReminders()
        reminders = remindersRepository.getReminders() as Result.Success
        Assert.assertThat(reminders.data.toList(), IsEqual(emptyList<ReminderDTO>()))
    }

    @Test
    fun getReminder_requestsReminderFromLocalRepository_success() = runBlocking {
        remindersRepository.saveReminder(reminder1)
        val reminder = remindersRepository.getReminder(reminder1.id) as Result.Success
        Assert.assertThat(reminder.data.id, IsEqual(reminder1.id))
        Assert.assertThat(reminder.data.latitude, IsEqual(reminder1.latitude))
        Assert.assertThat(reminder.data.longitude, IsEqual(reminder1.longitude))
        Assert.assertThat(reminder.data.location, IsEqual(reminder1.location))
        Assert.assertThat(reminder.data.title, IsEqual(reminder1.title))
        Assert.assertThat(reminder.data.description, IsEqual(reminder1.description))
    }

    @Test
    fun getReminder_requestsReminderFromLocalRepository_failure() = runBlocking {
        remindersRepository.saveReminder(reminder1)
        val reminder = remindersRepository.getReminder(reminder2.id)
        Assert.assertThat(reminder, IsEqual(Result.Error("Reminder not found!")))
    }
}