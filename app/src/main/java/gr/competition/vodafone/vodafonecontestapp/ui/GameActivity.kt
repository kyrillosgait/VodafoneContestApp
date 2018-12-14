package gr.competition.vodafone.vodafonecontestapp.ui

import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import gr.competition.vodafone.vodafonecontestapp.R
import gr.competition.vodafone.vodafonecontestapp.db.AppDatabase
import gr.competition.vodafone.vodafonecontestapp.db.GiftDao
import gr.competition.vodafone.vodafonecontestapp.model.Box
import kotlinx.android.synthetic.main.activity_game.*
import org.jetbrains.anko.startActivity

class GameActivity : AppCompatActivity() {

    private var selectedBox: Int = 0
    private lateinit var giftDao: GiftDao
    private var boxes = mutableSetOf(1, 2, 3, 4, 5, 6)
    private var boxesList = mutableListOf<Box>()
    private val youWon = "Κέρδισες"
    private var boxName = ""
    private var youLost = "Λυπάμαι αλλά δεν ήσουν τυχερός αυτή τη φορά"
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        giftDao = AppDatabase.getInstance(this).giftDao()

        selectRandomGifts()

        numberOfTriesTextView.text = String.format(getString(R.string.tries_left), getRetries())

        // Boxes onClickListeners
        firstBox.setOnClickListener { onBoxClicked(1) }
        secondBox.setOnClickListener { onBoxClicked(2) }
        thirdBox.setOnClickListener { onBoxClicked(3) }
        fourthBox.setOnClickListener { onBoxClicked(4) }
        fifthBox.setOnClickListener { onBoxClicked(5) }
        sixthBox.setOnClickListener { onBoxClicked(6) }
        giftAnimation.setOnClickListener { startActivity<HistoryActivity>("GIFT_NAME" to boxName) }
        sadAnimation.setOnClickListener { finish() }
    }

    private fun selectRandomGifts() {

        var boxesSet = mutableSetOf(1, 2, 3, 4, 5, 6)

        // Add boxes
        for (i in 1..6) {
            val box = Box(i)
            boxesList.add(box)
        }

        // Assign gifts to 4 random out of 6 boxes
        for (i in 1..4) {
            val randomBox = boxesSet.random()
            for (box in boxesList) {
                if (box.id == randomBox) {
                    box.categoryId = i
                    box.name = giftDao.selectRandomGiftFromCategory(i)
                    changeTextViewNameToGift(i, box.name)
                }
            }
            boxesSet.remove(randomBox)
        }
    }

    private fun changeTextViewNameToGift(textViewNumber: Int, name: String) {
        when (textViewNumber) {
            1 -> firstRewardTextView.text = name
            2 -> secondRewardTextView.text = name
            3 -> thirdRewardTextView.text = name
            4 -> fourthRewardTextView.text = name
        }
    }

    private fun onBoxClicked(boxNumber: Int) {

        boxes.remove(boxNumber)

        if (selectedBox == 0) {
            selectedBox = boxNumber

            when (boxNumber) {
                1-> firstBox.setBackgroundColor(resources.getColor(R.color.colorAccent2))
                2 -> secondBox.setBackgroundColor(resources.getColor(R.color.colorAccent2))
                3 -> thirdBox.setBackgroundColor(resources.getColor(R.color.colorAccent2))
                4 -> fourthBox.setBackgroundColor(resources.getColor(R.color.colorAccent2))
                5 -> fifthBox.setBackgroundColor(resources.getColor(R.color.colorAccent2))
                6 -> sixthBox.setBackgroundColor(resources.getColor(R.color.colorAccent2))
            }

            odigiesTextView.text = "Πάτα στα κουτιά για να αποκαλύψεις τα δώρα που κρύβουν"

        } else {

            if (boxNumber != selectedBox) {
                hideBox(boxNumber)

                if (boxes.size > 0) {

                    for (box in boxesList) {
                        if (box.id == boxNumber) {
                            strikeThroughReward(box.name)
                        }
                    }

                } else {

                    for (box in boxesList) {
                        if (box.id == selectedBox) {
                            boxName = box.name
                        }
                    }

                    if (boxName.isNotEmpty()) {
                        odigiesTextView.text = "$youWon $boxName"
                        giftAnimation.visibility = View.VISIBLE
                    } else {
                        odigiesTextView.text = youLost
                        sadAnimation.visibility = View.VISIBLE
                    }

                    if (getRetries() > 0) {
                        updateRetries(getRetries() - 1)
                    }
                }
            }

        }
    }

    private fun strikeThroughReward(name: String) {
        when (name) {
            firstRewardTextView.text -> firstRewardTextView.paintFlags = firstRewardTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            secondRewardTextView.text -> secondRewardTextView.paintFlags = secondRewardTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            thirdRewardTextView.text -> thirdRewardTextView.paintFlags = thirdRewardTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            fourthRewardTextView.text -> fourthRewardTextView.paintFlags = fourthRewardTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    private fun hideBox(boxNumber: Int) {
        when (boxNumber) {
            1 -> firstBox.visibility = View.INVISIBLE
            2 -> secondBox.visibility = View.INVISIBLE
            3 -> thirdBox.visibility = View.INVISIBLE
            4 -> fourthBox.visibility = View.INVISIBLE
            5 -> fifthBox.visibility = View.INVISIBLE
            6 -> sixthBox.visibility = View.INVISIBLE
        }
    }

    private fun getRetries(): Int {
        prefs = this.getSharedPreferences(packageName, 0);
        return prefs!!.getInt(MainActivity.RETRIES_KEY, 0)
    }

    private fun updateRetries(newAmountOfRetries: Int) {
        val editor = prefs?.edit()
        editor?.putInt(MainActivity.RETRIES_KEY, newAmountOfRetries)
        editor?.apply()
        numberOfTriesTextView.text = String.format(getString(R.string.tries_left), getRetries())
    }

}


