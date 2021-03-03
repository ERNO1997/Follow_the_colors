package cu.erno.followthecolors.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlin.math.sqrt

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val brokeRecord = MutableLiveData<Boolean>()
    val record = MutableLiveData<Int>()
    val score = MutableLiveData<Int>()
    val difficulty = MutableLiveData<Difficulty>()
    val gameState = MutableLiveData<GameState>()

    private val preferences: SharedPreferences =
        application.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    val appVersion = application.packageManager
        .getPackageInfo(application.packageName, PackageManager.GET_CONFIGURATIONS)
        .versionName

    init {
        record.value = preferences.getInt("record", 0)
        score.value = 0
        brokeRecord.value = false
        difficulty.value =
            Difficulty.EASY
        gameState.value =
            GameState.START
    }

    private var sequenceSize = 1
    private var generatedNumbers: List<Int> = emptyList()
    private var currentNumberPosition = -1

    fun generateSequence(): List<Int> {
        gameState.value =
            GameState.GENERATING
        if (difficulty.value == Difficulty.NONE) {
            generatedNumbers = emptyList()
            currentNumberPosition = -1
            return emptyList()
        } else {
            val list = mutableListOf<Int>()
            while (list.size < sequenceSize) {
                val randomNumber =
                    (Math.random() * (difficulty.value ?: Difficulty.EASY).amountOfTiles).toInt()
                if (list.isEmpty() || (list.isNotEmpty() && list[list.size - 1] != randomNumber)) {
                    list.add(randomNumber)
                }
            }
            generatedNumbers = list
            currentNumberPosition = 0
            return list
        }
    }

    private fun increaseScore() {
        score.value = (score.value ?: 0) + 1
        score.value?.also { score ->
            if (score > record.value ?: Int.MAX_VALUE) {
                record.value = score
                brokeRecord.value = true
            }
            sequenceSize = sqrt(score.toDouble()).toInt() + 1
            when {
                score < SCORE_REQUIRED_FOR_MEDIUM_LEVEL -> {
                    if (difficulty.value != Difficulty.EASY) difficulty.value =
                        Difficulty.EASY
                }
                score < SCORE_REQUIRED_FOR_HARD_LEVEL -> {
                    if (difficulty.value != Difficulty.MEDIUM) {
                        difficulty.value =
                            Difficulty.MEDIUM
                        gameState.value =
                            GameState.CHANGING
                        return
                    }
                }
                else -> {
                    if (difficulty.value != Difficulty.HARD) {
                        difficulty.value =
                            Difficulty.HARD
                        gameState.value =
                            GameState.CHANGING
                        return
                    }
                }
            }
            gameState.value =
                GameState.NEXT
        }
    }

    fun tileWasPressed(number: Int) {
        if (currentNumberPosition == -1) return
        if (generatedNumbers[currentNumberPosition] == number) {
            currentNumberPosition++
            if (currentNumberPosition == sequenceSize) {
                increaseScore()
            }
        } else {
            lose()
        }
    }

    private fun lose() {
        record.value?.let { record -> 
            preferences.edit().putInt(RECORD_KEY, record).commit()
        }
        gameState.value =
            GameState.LOSE
        difficulty.value =
            Difficulty.NONE
    }

    fun notifyGenerationEnd() {
        gameState.value =
            GameState.GO_AHEAD
    }

    fun notifyChangingEnd() {
        gameState.value =
            GameState.NEXT
    }

    fun notifyStart() {
        gameState.value =
            GameState.START
    }

    fun resetGame() {
        record.value?.let { record ->
            preferences.edit().putInt(RECORD_KEY, record).commit()
        }
        gameState.value =
            GameState.START
        difficulty.value =
            Difficulty.EASY
        score.value = 0
        sequenceSize = 1
        brokeRecord.value = false
    }

    enum class Difficulty(val amountOfTiles: Int) {
        EASY(4),
        MEDIUM(6),
        HARD(9),
        NONE(-1)
    }

    enum class GameState {
        START,
        GENERATING,
        GO_AHEAD,
        NEXT,
        CHANGING,
        LOSE
    }

    companion object {
        private const val SCORE_REQUIRED_FOR_MEDIUM_LEVEL = 10
        private const val SCORE_REQUIRED_FOR_HARD_LEVEL = 25
        private const val RECORD_KEY = "record"
    }
}