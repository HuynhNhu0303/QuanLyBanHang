import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class MonthPickerDialog : DialogFragment() {

    private var onDateSetListener: ((Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        return DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, _ ->
                onDateSetListener?.invoke(selectedMonth + 1) // Tháng bắt đầu từ 0
            },
            year,
            month,
            1 // Ngày không quan trọng trong trường hợp này
        )
    }

    fun setOnDateSetListener(listener: (Int) -> Unit) {
        onDateSetListener = listener
    }
}
