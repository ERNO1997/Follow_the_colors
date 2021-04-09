package cu.erno.followthecolors.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import cu.erno.followthecolors.R
import cu.erno.followthecolors.databinding.FragmentHomeBinding
import cu.erno.followthecolors.ui.viewmodel.GameViewModel

class HomeFragment : Fragment() {

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.record.observe(viewLifecycleOwner, Observer { record ->
            binding.txtRecord.text = getString(R.string.record, record)
        })

        binding.layoutHowToPlay.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_tutorialDialog)
        }

        binding.layoutStatistics.setOnClickListener {
            Snackbar.make(binding.layoutStatistics, "Statistics", Snackbar.LENGTH_SHORT).show()
        }

        binding.layoutShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with_friends)))

        }

        binding.layoutRate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.apklis.cu/application/cu.erno.followthecolors"))
            startActivity(intent)
        }

        binding.layoutAbout.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_aboutDialog)
        }

        binding.btnPlay.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val extras = FragmentNavigatorExtras(
                    binding.txtTitle to binding.txtTitle.transitionName,
                    //binding.txtRecord to binding.txtRecord.transitionName,
                    binding.btnPlay to binding.btnPlay.transitionName
                )
                findNavController().navigate(
                    R.id.action_homeFragment_to_gameFragment, null, null, extras
                )
            } else {
                findNavController().navigate(R.id.action_homeFragment_to_gameFragment)
            }
        }

        return binding.root
    }
}