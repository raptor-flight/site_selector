package com.raptor.ai.site.core;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    public CSVParser() {
        super();
    }

    public List<String> getResults(final String dataSet ) {
       final List<String> record = new ArrayList<>(120);
       InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dataSet);

        try ( final BufferedReader reader = new BufferedReader(new
                InputStreamReader(resourceAsStream))) {
            String line = null;
            int rowIndex = 0;
            while ((line = reader.readLine()) != null) {
                if ( rowIndex == 0 ) {
                    rowIndex++;
                } else {
                    record.add(line.trim());
                }

            }
       } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
       } catch (IOException e) {
           throw new RuntimeException(e);
       } finally {
            if ( resourceAsStream != null ) {
                try {
                    resourceAsStream.close();
                } catch (final IOException ioex) {
                    throw new RuntimeException(ioex);
                }
            }
        }

       return record;
    }
}
