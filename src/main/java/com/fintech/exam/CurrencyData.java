package com.fintech.exam;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Useful information about currency conversion.
 *
 * @author Aleksandr Khasanov
 */
class CurrencyData
{
    private double rate;

    private LocalDate date;

    private LocalTime lastUpdateTime;

    /**
     * Get currency conversion rate.
     *
     * @return currency conversion rate
     */
    public double getRate()
    {
        return rate;
    }

    /**
     * Set currency conversion rate.
     *
     * @param rate currency conversion rate
     */
    public void setRate(double rate)
    {
        this.rate = rate;
    }

    /**
     * Get conversion rate date.
     *
     * @return conversion date.
     */
    public LocalDate getDate()
    {
        return date;
    }

    /**
     * Set conversion rate date.
     *
     * @param date conversion rate date
     */
    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    /**
     * Get time of last attempt of conversion rate update.
     *
     * @return time of last attempt of conversion rate update
     */
    public LocalTime getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    /**
     * Set time of last attempt of conversion rate update.
     *
     * @param lastUpdateTime time of last attempt of conversion rate update
     */
    public void setLastUpdateTime(LocalTime lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
    }
}
