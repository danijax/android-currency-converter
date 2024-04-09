package com.danijax.paypayxchange.util

import com.danijax.paypayxchange.utils.fromTimeStamp
import com.danijax.paypayxchange.utils.getTimeElapsedInMinutes
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime

class DateUtilsTest {

    @Test
    fun `parse unix time stamp to correct date`(){
        val timStamp = 1697983223L
        val correctDate = LocalDateTime.parse("2023-10-22T15:00:23")
        val parsed  = timStamp.fromTimeStamp()
        assertThat("parsed date correct", correctDate.equals(parsed))
    }

    @Test
    fun `find time interval between two dates in minutes`(){
        val earlyDate = LocalDateTime.parse("2023-10-22T15:00:00")
        val recent = LocalDateTime.parse("2023-10-22T15:30:00")
        val intervalInMinutes = earlyDate.getTimeElapsedInMinutes(recent)
        println(intervalInMinutes)
        Assert.assertEquals(30L, intervalInMinutes)
    }

}