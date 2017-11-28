package io.mokshjn.cosmo.utils

import java.util.*

/**
 * Created by moksh on 28/11/17.
 */
class CalendarUtil {

    private val calendar: Calendar

    /**
     * Returns the time elapsed so far today in milliseconds.
     *
     * @return Time elapsed today in milliseconds.
     */
    // Time elapsed so far today
    val elapsedToday: Long
        get() = ((calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)) * MS_PER_MINUTE
                + (calendar.get(Calendar.SECOND) * 1000).toLong()
                + calendar.get(Calendar.MILLISECOND).toLong())

    /**
     * Returns the time elapsed so far this week in milliseconds.
     *
     * @return Time elapsed this week in milliseconds.
     */
    // Today + days passed this week
    val elapsedWeek: Long
        get() {
            var elapsed = elapsedToday

            val passedWeekdays = calendar.get(Calendar.DAY_OF_WEEK) - 1 - calendar.firstDayOfWeek
            if (passedWeekdays > 0) {
                elapsed += passedWeekdays * MS_PER_DAY
            }

            return elapsed
        }

    /**
     * Returns the time elapsed so far this month in milliseconds.
     *
     * @return Time elapsed this month in milliseconds.
     */
    // Today + rest of this month
    val elapsedMonth: Long
        get() = elapsedToday + (calendar.get(Calendar.DAY_OF_MONTH) - 1) * MS_PER_DAY

    /**
     * Returns the time elapsed so far this year in milliseconds.
     *
     * @return Time elapsed this year in milliseconds.
     */
    // Today + rest of this month + previous months until January
    val elapsedYear: Long
        get() {
            var elapsed = elapsedMonth

            var month = calendar.get(Calendar.MONTH) - 1
            val year = calendar.get(Calendar.YEAR)
            while (month > Calendar.JANUARY) {
                elapsed += getDaysInMonth(year, month) * MS_PER_DAY

                month--
            }

            return elapsed
        }

    init {
        this.calendar = Calendar.getInstance()
    }

    /**
     * Returns the time elapsed so far this month and the last numMonths months in milliseconds.
     *
     * @param numMonths Additional number of months prior to the current month to calculate.
     * @return Time elapsed this month and the last numMonths months in milliseconds.
     */
    fun getElapsedMonths(numMonths: Int): Long {
        // Today + rest of this month
        var elapsed = elapsedMonth

        // Previous numMonths months
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        for (i in 0 until numMonths) {
            month--

            if (month < Calendar.JANUARY) {
                month = Calendar.DECEMBER
                year--
            }

            elapsed += getDaysInMonth(year, month) * MS_PER_DAY
        }

        return elapsed
    }

    /**
     * Gets the number of days for the given month in the given year.
     *
     * @param year  The year.
     * @param month The month (1 - 12).
     * @return The days in that month/year.
     */
    private fun getDaysInMonth(year: Int, month: Int): Int {
        val monthCal = GregorianCalendar(calendar.get(Calendar.YEAR), month, 1)
        return monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    companion object {
        private val MS_PER_MINUTE = (60 * 1000).toLong()
        private val MS_PER_DAY = 24 * 60 * MS_PER_MINUTE
    }
}