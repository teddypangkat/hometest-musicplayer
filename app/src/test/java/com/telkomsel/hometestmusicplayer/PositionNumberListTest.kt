package com.telkomsel.hometestmusicplayer

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.telkomsel.hometestmusicplayer.ui.main.view.MainActivity
import com.telkomsel.hometestmusicplayer.util.PositionNumberList
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class PositionNumberListTest {

    @Test
    fun nextPositionNumber_isCorrect(){
        //number position next must +1 from current position
        assertEquals(2, PositionNumberList.nextPosition(1))
    }

    @Test
    fun prevPositionNumber_isCorrect(){
        //number position next must -1 from current position
        assertEquals(2, PositionNumberList.prevPosition(3))
    }
}
