package com.udacity.project4.data

import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.withContext

class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return try {
            Result.Success(ArrayList(reminders!!))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        try {
            for (reminder in reminders!!) {
                if (reminder.id == id) {
                    return Result.Success(reminder)
                }
            }
        } catch (e: Exception) {
            return Result.Error(e.localizedMessage)
        }
        return Result.Error("Reminder not found")
    }
}