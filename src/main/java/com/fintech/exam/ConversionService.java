package com.fintech.exam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * Currency conversion service class.
 *
 * @author Aleksandr Khasanov
 */
public class ConversionService
{
    private static final String EXIT_KEY_WORD = "EXIT";

    private static final ConversionService INSTANCE = new ConversionService();

    private static final String[] SUPPORTED_CURRENCIES = new String[] { "EUR", "USD", "JPY", "BGN", "CZK", "DKK", "GBP",
            "HUF", "PLN", "RON", "SEK", "CHF", "NOK", "HRK", "RUB", "TRY", "AUD", "BRL", "CAD", "CNY", "HKD", "IDR",
            "ILS", "INR", "KRW", "MXN", "MYR", "NZD", "PHP", "SGD", "THB", "ZAR" };

    private static final Set<String> supportedCurrencies =  new HashSet<>(Arrays.asList(SUPPORTED_CURRENCIES));

    private Map<String, CurrencyData> localCache = new HashMap<>();

    private ConversionService()
    {
    }

    /**
     * Get instance of {@link ConversionService}
     *
     * @return instance of {@link ConversionService}
     */
    public static ConversionService getInstance()
    {
        return INSTANCE;
    }

    /**
     * Main entry point.
     *
     * @param args comand line arguments
     */
    public static void main(String[] args)
    {
        getInstance().startService();
    }

    /**
     * Starts conversion service.
     */
    public void startService()
    {
        System.out.println("Hello! Welcome to currency converter!");
        System.out.println("Type 'EXIT' in any field to exit program.");
        System.out.println();

        uploadCachedData();

        Scanner inScanner = new Scanner(System.in);

        while (true)
        {
            System.out.println("Enter from currency:");
            String fromCurrency = inScanner.nextLine().toUpperCase();
            if (EXIT_KEY_WORD.equals(fromCurrency))
            {
                return;
            }

            System.out.println("Enter to currency:");
            String toCurrency = inScanner.nextLine().toUpperCase();
            if (EXIT_KEY_WORD.equals(toCurrency))
            {
                return;
            }

            Pattern pattern = Pattern.compile("[A-Z]+");
            boolean isCorrectFromCurrencyFormat = pattern.matcher(fromCurrency).matches();
            boolean isCorrectToCurrencyFormat = pattern.matcher(toCurrency).matches();

            if (!isCorrectFromCurrencyFormat || !isCorrectToCurrencyFormat)
            {
                System.out.println("Wrong input data format!");
                StringBuilder builder = new StringBuilder();
                builder.append("Following data has inappropriate format: ");
                if (!isCorrectFromCurrencyFormat)
                {
                    builder.append(fromCurrency);
                }

                if (!isCorrectToCurrencyFormat)
                {
                    if (!isCorrectFromCurrencyFormat)
                    {
                        builder.append(", ");
                    }

                    builder.append(toCurrency);
                }

                System.out.println(builder.toString());
                System.out.println();
                continue;
            }

            boolean isFromCurrencySupported = supportedCurrencies.contains(fromCurrency);
            boolean isToCurrencySupported = supportedCurrencies.contains(toCurrency);

            if (!isFromCurrencySupported || !isToCurrencySupported)
            {
                StringBuilder builder = new StringBuilder();
                builder.append("Sorry, following currencies are not supported: ");
                if (!isFromCurrencySupported)
                {
                    builder.append(fromCurrency);
                }

                if (!isToCurrencySupported)
                {
                    if (!isFromCurrencySupported)
                    {
                        builder.append(", ");
                    }

                    builder.append(toCurrency);
                }

                System.out.println(builder.toString());
                System.out.println();
                continue;
            }

            convert(fromCurrency, toCurrency);
            System.out.println();
        }
    }

    /*
     * Asynchronously load cached data from text file.
     */
    private void uploadCachedData()
    {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<?> future = executor.submit(new UploadCachedDataTask(localCache));

        System.out.print("Initial data loading: ");
        while (!future.isDone())
        {
            System.out.print(".");
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                // no actions
            }
        }
        System.out.println();
        System.out.println();
    }

    /*
     * Convert currency.
     */
    private void convert(String fromCurrency, String toCurrency)
    {
        if (fromCurrency.equals(toCurrency))
        {
            printRate(fromCurrency, toCurrency, 1.0);
            return;
        }

        String key = fromCurrency + "/" + toCurrency;

        CurrencyData cachedData = localCache.get(key);

        if (cachedData != null)
        {
            LocalDate cachedDate = cachedData.getDate();
            LocalDate currentDate = LocalDate.now();

            if (currentDate.compareTo(cachedDate) <= 0)
            {
                // already have cached data for current date
                printRate(fromCurrency, toCurrency, cachedData.getRate());
                return;
            }
            else // if (currentDate.compareTo(cachedDate) > 0)
            {
                // According to the service documentation: "The rates are updated daily around 4PM CET."
                //
                // There may be a situation when data is requested before 4pm of the day.
                // For example if data is requested on 25.11.2017 3:00pm CET
                // the response will be provided with 'date' 24.11.2017
                //
                // In this case we should return cached value without request to server.

                if (cachedDate.until(currentDate, ChronoUnit.DAYS) <= 1)
                {
                    LocalTime lastUpdateTime = cachedData.getLastUpdateTime();

                    if (lastUpdateTime != null)
                    {
                        LocalTime currentTime = LocalTime.now();

                        // rates are updated hourly
                        if (currentTime.getHour() <= lastUpdateTime.getHour())
                        {
                            printRate(fromCurrency, toCurrency, cachedData.getRate());
                            return;
                        }
                    }
                }
            }
        }

        // Did not manage to use cached data.
        // Let's make a request to server.

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Double> response = executor.submit(new CurrencyConversionTask(fromCurrency, toCurrency, localCache));

        while (!response.isDone())
        {
            System.out.print(".");

            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                // no actions
            }
        }
        System.out.println();

        Double rate = null;

        try
        {
            rate = response.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            // no actions here
        }

        if (rate != null)
        {
            printRate(fromCurrency, toCurrency, rate);
        }
        else
        {
            System.out.println("Ooops...something went wrong while currency conversion");

            if (cachedData != null)
            {
                System.out.print("The last conversion rate was: ");
                printRate(fromCurrency, toCurrency, cachedData.getRate());
            }
        }
    }

    /*
     * Print currency conversion rate.
     */
    private void printRate(String fromCurrency, String toCurrency, Double rate)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fromCurrency).append(" => ").append(toCurrency).append(" : ").append(rate);
        System.out.println(stringBuilder.toString());
    }
}