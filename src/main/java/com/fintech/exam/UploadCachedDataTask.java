package com.fintech.exam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link Runnable} that performs data upload form file.
 *
 * @author Aleksandr Khasanov
 */
class UploadCachedDataTask implements Runnable
{
    private static final String CACHE_FILE_NAME = "cache.txt";

    private static final String SEPARATOR = ",";

    private Map<String, CurrencyData> localCache = new HashMap<>();

    /**
     * Creates new instance of {@link UploadCachedDataTask}
     *
     * @param localCache storage of cached data
     */
    public UploadCachedDataTask(Map<String, CurrencyData> localCache)
    {
        this.localCache = localCache;
    }

    @Override
    public void run()
    {
        loadCachedData();
        try
        {
            // just for data upload visualization
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            //  no actions here
        }
    }

    /**
     * Upload data from text file.
     */
    private void loadCachedData()
    {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(CACHE_FILE_NAME)))
        {
            localCache.clear();

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null)
            {
                String[] cachedData = currentLine.split(SEPARATOR);

                if (cachedData.length != 3)
                {
                    continue;
                }

                String key = cachedData[0];
                LocalDate date = LocalDate.parse(cachedData[1]);
                Double rate = Double.valueOf(cachedData[2]);

                CurrencyData value = new CurrencyData();
                value.setRate(rate);
                value.setDate(date);

                localCache.put(key, value);
            }
        }
        catch (IOException e)
        {
            // no actions
        }
    }
}
