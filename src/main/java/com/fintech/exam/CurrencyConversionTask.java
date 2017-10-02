package com.fintech.exam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;

/**
 * Task for performing async currency conversion.
 *
 * @author Aleksandr Khasanov
 */
class CurrencyConversionTask implements Callable<Double>
{
    private static final String CACHE_FILE_NAME = "cache.txt";

    private static final String SEPARATOR = ",";

    private String fromCurrency;

    private String toCurrency;

    private Map<String, CurrencyData> localCache;

    /**
     * Creates new instance of {@link CurrencyConversionTask}
     *
     * @param fromCurrency currency to convert from. Must not be {@code null}.
     * @param toCurrency currency to convert to. Must not be {@code null}.
     * @param localCache data cache. Must not be {@code null}.
     */
    public CurrencyConversionTask(String fromCurrency, String toCurrency, Map<String, CurrencyData> localCache)
    {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.localCache = localCache;
    }

    @Override
    public Double call() throws Exception
    {
        LocalTime updateTime = LocalTime.now();
        ApiResponse response = requestData(fromCurrency, toCurrency);

        if (response != null)
        {
            CurrencyData value = new CurrencyData();
            value.setDate(LocalDate.parse(response.getDate()));
            value.setRate(response.getRates().getRate());
            value.setLastUpdateTime(updateTime);

            String key = fromCurrency + "/" + toCurrency;
            localCache.put(key, value);

            exportCachedData();

            return response.getRates().getRate();
        }

        return null;
    }

    /*
     * Request data from http service.
     */
    private ApiResponse requestData(String fromCurrency, String toCurrency)
    {
        String query = "http://api.fixer.io/latest?base=" + fromCurrency + "&symbols=" + toCurrency;

        URL url = null;
        try
        {
            url = new URL(query);
        }
        catch (MalformedURLException e)
        {
            // query has a wrong format
            // no actions here
        }

        if (url == null)
        {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try
        {
            try
            {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            catch (IOException e)
            {
                // no actions here
            }

            if (urlConnection == null)
            {
                return null;
            }

            try
            {
                inputStream = urlConnection.getInputStream();
            }
            catch (IOException e)
            {
                // no actions here
            }

            if (inputStream == null)
            {
                return null;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new GsonBuilder().registerTypeAdapter(RateObject.class, new RatesDeserializer()).create();
            return gson.fromJson(bufferedReader, ApiResponse.class);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    // no actions here
                }
            }

            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
    }

    /*
     * Export cached data to text file.
     */
    private void exportCachedData()
    {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(CACHE_FILE_NAME))))
        {
            for (Map.Entry<String, CurrencyData> entry : localCache.entrySet())
            {
                StringJoiner joiner = new StringJoiner(SEPARATOR);
                joiner.add(entry.getKey());
                joiner.add(entry.getValue().getDate().toString());
                joiner.add(Double.valueOf(entry.getValue().getRate()).toString());

                printWriter.println(joiner.toString());
            }
        }
        catch (IOException e)
        {
            // no actions here
        }
    }
}
