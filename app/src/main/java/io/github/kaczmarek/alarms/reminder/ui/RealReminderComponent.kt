package io.github.kaczmarek.alarms.reminder.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.arkivanov.decompose.ComponentContext
import io.github.kaczmarek.alarms.R
import io.github.kaczmarek.alarms.core.error_handling.ErrorHandler
import io.github.kaczmarek.alarms.core.error_handling.safeRun
import io.github.kaczmarek.alarms.core.utils.componentCoroutineScope
import io.github.kaczmarek.alarms.reminder.domain.DeleteRemindersInteractor
import io.github.kaczmarek.alarms.reminder.domain.SetReminderInteractor
import me.aartikov.sesame.compose.form.control.InputControl
import me.aartikov.sesame.compose.form.validation.control.isNotBlank
import me.aartikov.sesame.compose.form.validation.form.*

class RealReminderComponent(
    componentContext: ComponentContext,
    private val errorHandler: ErrorHandler,
    private val setReminderInteractor: SetReminderInteractor,
    private val deleteRemindersInteractor: DeleteRemindersInteractor
) : ComponentContext by componentContext, ReminderComponent {

    private val coroutineScope = componentCoroutineScope()

    override val titleInput = InputControl(
        maxLength = 30,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )

    override val descriptionInput = InputControl(
        maxLength = 50,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )

    private val formValidator = coroutineScope.formValidator {
        features = listOf(
            ValidateOnFocusLost,
            RevalidateOnValueChanged,
            SetFocusOnFirstInvalidControlAfterValidation
        )

        input(titleInput) {
            isNotBlank(R.string.reminder_field_is_blank_error_message)
        }

        input(descriptionInput) {
            isNotBlank(R.string.reminder_field_is_blank_error_message)
        }
    }

    private val dynamicResult by coroutineScope.dynamicValidationResult(formValidator)

    override val setReminderButtonEnabled by derivedStateOf {
        dynamicResult.isValid
    }

    override fun onSetReminderClick() {
        safeRun(errorHandler) {
            setReminderInteractor.execute(
                title = titleInput.text,
                description = descriptionInput.text,
                repeatPeriod = 30L
            )
            titleInput.onTextChanged("")
            descriptionInput.onTextChanged("")
        }
    }

    override fun onDeleteRemindersClick() {
        safeRun(errorHandler) {
            deleteRemindersInteractor.execute()
        }
    }
}