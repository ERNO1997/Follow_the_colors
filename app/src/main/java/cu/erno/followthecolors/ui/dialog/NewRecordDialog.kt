package cu.erno.followthecolors.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cu.erno.followthecolors.ui.viewmodel.GameViewModel
import cu.erno.followthecolors.R
import cu.erno.followthecolors.databinding.DialogGameOverBinding

class NewRecordDialog : DialogFragment() {

    private val viewModel: GameViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogGameOverBinding.inflate(inflater, container, false)

        binding.txtTitle.text = getString(R.string.new_record)

        viewModel.record.observe(viewLifecycleOwner, Observer { record ->
            binding.txtScore.text = getString(R.string.record, record)
        })

        binding.layoutHome.setOnClickListener {
            viewModel.resetGame()
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.layoutReset.setOnClickListener {
            viewModel.resetGame()
            findNavController().popBackStack()
        }
        isCancelable = false
        return binding.root
    }
}