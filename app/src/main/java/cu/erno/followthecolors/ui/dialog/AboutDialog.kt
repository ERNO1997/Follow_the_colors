package cu.erno.followthecolors.ui.dialog

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import cu.erno.followthecolors.R
import cu.erno.followthecolors.databinding.DialogAboutBinding
import cu.erno.followthecolors.ui.viewmodel.GameViewModel

class AboutDialog : DialogFragment() {

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
        val binding = DialogAboutBinding.inflate(inflater, container, false)

        val appVersion = requireActivity().packageManager
            .getPackageInfo(requireActivity().packageName, PackageManager.GET_CONFIGURATIONS)
            .versionName
        binding.txtVersion.text = getString(R.string.version, appVersion)

        return binding.root
    }
}