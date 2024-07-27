import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class YearPickerDialog : DialogFragment() {

    private var onDateSetListener: ((Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)

        return DatePickerDialog(
            requireContext(),
            { _, selectedYear, _, _ ->
                onDateSetListener?.invoke(selectedYear)
            },
            year,
            0, // Tháng không quan trọng trong trường hợp này
            1 // Ngày không quan trọng trong trường hợp này
        )
    }

    fun setOnDateSetListener(listener: (Int) -> Unit) {
        onDateSetListener = listener
    }
}
