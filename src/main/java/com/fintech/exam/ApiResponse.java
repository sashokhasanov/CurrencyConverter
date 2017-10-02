package com.fintech.exam;

/**
 * Represents api response for http service <a href="http://fixer.io/">http://fixer.io/</a>.
 *
 * @author Aleksandr Khasanov
 */
class ApiResponse
{
    private String base;

    private String date;

    private RateObject rates;

    /**
     * Get base currency name.
     *
     * @return base currency name
     */
    public String getBase()
    {
        return base;
    }

    /**
     * Set base currency name.
     *
     * @param base base currency name
     */
    public void setBase(String base)
    {
        this.base = base;
    }

    /**
     * Get date.
     *
     * @return date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Set date.
     *
     * @param date date
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * Get conversion rate info.
     *
     * @return {@link RateObject}
     */
    public RateObject getRates()
    {
        return rates;
    }

    /**
     * Set conversion rate info.
     *
     * @param rates {@link RateObject}
     */
    public void setRates(RateObject rates)
    {
        this.rates = rates;
    }
}
