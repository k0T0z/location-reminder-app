package com.udacity.project4.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersRepository
import kotlinx.coroutines.runBlocking

class FakeTestRepository : RemindersRepository {

    var remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private val observableReminders = MutableLiveData<Result<List<ReminderDTO>>>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(remindersServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return Result.Success(remindersServiceData[id] as ReminderDTO)
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }

//    fun addReminders(vararg reminders: ReminderDTO) {
//        for (reminder in reminders) {
//            remindersServiceData[reminder.id] = reminder
//        }
//        runBlocking {
//            observableReminders.value = getReminders()
//        }
//    }
}