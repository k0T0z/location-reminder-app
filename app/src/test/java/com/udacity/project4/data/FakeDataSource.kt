package com.udacity.project4.data

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao

class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : RemindersDao {
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override suspend fun getReminders(): List<ReminderDTO> {
        reminders?.let {
            return ArrayList(it)
        }
        return ArrayList<ReminderDTO>()
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        TODO("Not yet implemented")
    }

}

// class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
//    override suspend fun saveReminder(reminder: ReminderDTO) {
//        reminders?.add(reminder)
//    }
//
//    override suspend fun deleteAllReminders() {
//        reminders?.clear()
//    }
//
//    override suspend fun getReminders(): Result<List<ReminderDTO>> {
//        reminders?.let {
//            return Result.Success(ArrayList(it))
//        }
//        return Result.Error("Error reminder not found")
//    }
//
//    override suspend fun getReminder(id: String): Result<ReminderDTO> {
//        TODO("Not yet implemented")
//    }
//}