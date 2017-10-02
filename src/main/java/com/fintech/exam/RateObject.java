package com.fintech.exam;

/**
 * Represents information about conversion rate for specific currency.
 *
 * @author Aleksandr Khasanov
 */
class RateObject
{
    private String name;

    private double rate;

    /**
     * Creates new instance of {@link RateObject}.
     *
     * @param name target currency name
     * @param rate conversion rate
     */
    public RateObject(String name, double rate)
    {
        this.name = name;
        this.rate = rate;
    }

    /**
     * Get currency name.
     *
     * @return currency name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set currency name.
     *
     * @param name currency name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get conversion rate.
     *
     * @return conversion rate
     */
    public double getRate()
    {
        return rate;
    }

    /**
     * Set conversion rate.
     *
     * @param rate conversion rate
     */
    public void setRate(double rate)
    {
        this.rate = rate;
    }
}
