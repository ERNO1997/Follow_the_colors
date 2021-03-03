package cu.erno.followthecolors.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import cu.erno.followthecolors.ui.viewmodel.GameViewModel
import cu.erno.followthecolors.R
import cu.erno.followthecolors.ui.customview.TileView
import cu.erno.followthecolors.databinding.FragmentGameBinding
import kotlinx.coroutines.*

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by activityViewModels()

    // array of TileView for easier management of Tiles
    private lateinit var tiles: Array<TileView>

    // onCreate method to complete the transition sharing elements
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if Android version is higher or equal than 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition =
                TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Load binding from FragmentGameBinding
        val binding = FragmentGameBinding.inflate(layoutInflater, container, false)

        // Load array of TileView
        tiles = arrayOf(
            binding.imgRed, binding.imgBlue, binding.imgYellow,
            binding.imgGreen, binding.imgOrange, binding.imgPurple,
            binding.imgPink, binding.imgWhite, binding.imgBrown
        )
        // Set the onClick event for each tile
        tiles.forEachIndexed { index, tile ->
            tile.setOnClickListener {
                // If the tile is not ON then
                if (!tile.isON) {
                    turnOffTiles() // Turn OFF all tiles
                    tile.turnON() // Turn ON this tile
                    // Notice to viewModel this tile was pressed
                    viewModel.tileWasPressed(index)
                }
            }
        }
        // Set the onClick to the play button
        binding.btnPlay.setOnClickListener {
            lifecycleScope.launch {
                val list = viewModel.generateSequence()
                for (i in list) {
                    tiles[i].turnON()
                    delay(500)
                    turnOffTiles()
                }
                viewModel.notifyGenerationEnd()
            }
        }

        viewModel.gameState.observe(viewLifecycleOwner, Observer { gameState ->
            when (gameState) {
                GameViewModel.GameState.START -> {
                    disableTiles()
                    binding.btnPlay.isEnabled = true
                    binding.btnPlay.text = getString(R.string.start)
                }
                GameViewModel.GameState.GENERATING -> {
                    disableTiles()
                    binding.btnPlay.isEnabled = false
                    binding.btnPlay.text = getString(R.string.generating)
                }
                GameViewModel.GameState.GO_AHEAD -> {
                    enableTiles()
                    binding.btnPlay.isEnabled = false
                    binding.btnPlay.text = getString(R.string.go_ahead)
                }
                GameViewModel.GameState.NEXT -> {
                    disableTiles()
                    lifecycleScope.launch {
                        delay(500)
                        turnOffTiles()
                        binding.btnPlay.isEnabled = true
                        binding.btnPlay.text = getString(R.string.next)
                    }
                }
                GameViewModel.GameState.CHANGING -> {
                    disableTiles()
                    binding.btnPlay.isEnabled = false
                    binding.btnPlay.text = getString(R.string.changing)
                }
                GameViewModel.GameState.LOSE -> {
                    disableTiles()
                    binding.btnPlay.isEnabled = false
                    binding.btnPlay.text = getString(R.string.lose)
                }
                else -> Unit // for nullability of enums in Java
            }
        })

        viewModel.score.observe(viewLifecycleOwner, Observer { score ->
            binding.txtScore.text = getString(R.string.score, score)
        })

        viewModel.record.observe(viewLifecycleOwner, Observer { record ->
            binding.txtRecord.text = getString(R.string.record, record)
        })

        viewModel.brokeRecord.observe(viewLifecycleOwner, Observer { brokeRecord ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.txtRecord.setTextColor(
                    resources.getColorStateList(
                        if (brokeRecord) R.color.color_broke_record else R.color.color_normal_text,
                        requireActivity().theme
                    )
                )
            } else {
                binding.txtRecord.setTextColor(
                    resources.getColorStateList(
                        if (brokeRecord) R.color.color_broke_record else R.color.color_normal_text
                    )
                )
            }
        })

        viewModel.difficulty.observe(viewLifecycleOwner, Observer { difficulty ->
            when (difficulty) {
                GameViewModel.Difficulty.EASY -> {
                    for (i in 0 until 4) tiles[i].visibility = View.INVISIBLE
                    for (i in 4 until 9) tiles[i].visibility = View.GONE
                    lifecycleScope.launch {
                        for (i in 0 until 4) {
                            delay(100)
                            tiles[i].visibility = View.VISIBLE
                        }
                        viewModel.notifyStart()
                    }
                }
                GameViewModel.Difficulty.MEDIUM -> {
                    Toast.makeText(context, "Has llegado al nivel medio", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        delay(100)
                        turnOffTiles()
                        for (i in 0 until 4) {
                            delay(100)
                            tiles[i].visibility = View.INVISIBLE
                        }
                        for (i in 4 until 6) tiles[i].visibility = View.INVISIBLE
                        for (i in arrayOf(0, 1, 4, 5, 2, 3)) {
                            delay(100)
                            tiles[i].visibility = View.VISIBLE
                        }
                        viewModel.notifyChangingEnd()
                    }
                }
                GameViewModel.Difficulty.HARD -> {
                    Toast.makeText(context, "Has llegado al nivel avanzado", Toast.LENGTH_SHORT)
                        .show()
                    lifecycleScope.launch {
                        delay(100)
                        turnOffTiles()
                        for (i in arrayOf(0, 1, 4, 5, 2, 3)) {
                            delay(100)
                            tiles[i].visibility = View.INVISIBLE
                        }
                        for (i in 6 until 9) tiles[i].visibility = View.INVISIBLE
                        for (i in arrayOf(0, 6, 1, 4, 7, 5, 2, 8, 3)) {
                            delay(100)
                            tiles[i].visibility = View.VISIBLE
                        }
                        viewModel.notifyChangingEnd()
                    }
                }
                GameViewModel.Difficulty.NONE -> {
                    val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    val milliseconds = 500L
                    if (vibrator.hasVibrator()) vibrator.vibrate(milliseconds)
                    binding.squareBackground.setImageResource(R.drawable.background_error)
                    lifecycleScope.launch {
                        delay(500)
                        turnOffTiles()
                        binding.squareBackground.setImageResource(R.drawable.background_dark)
                        if(viewModel.brokeRecord.value == true)
                            findNavController().navigate(R.id.action_gameFragment_to_newRecordDialog)
                        else
                            findNavController().navigate(R.id.action_gameFragment_to_gameOverDialog)
                    }
                }
                else -> Unit // for nullability of enums in Java and GameViewModel.Difficulty.NONE
            }
        })

        for (i in 4..8) tiles[i].visibility = View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.resetGame()
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun enableTiles() {
        tiles.forEach { it.isEnabled = true }
    }

    private fun disableTiles() {
        tiles.forEach { it.isEnabled = false }
    }

    private fun turnOffTiles() {
        tiles.forEach { it.turnOFF() }
    }
}