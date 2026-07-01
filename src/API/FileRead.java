package API;

import java.io.*;

public class FileRead
{
    public void readCSV()
    {
        String line;
        String[] pathNames = {"questions/geography.csv",
                              "questions/gossip.csv",
                              "questions/hiddenQuestion.csv",
                              "questions/history.csv",
                              "questions/top5.csv"} ;

        for(String filename : pathNames)
        {
            String category = new File(filename).getName().replaceFirst("[.][^.]+$", "");

            InputStream is = FileRead.class.getClassLoader().getResourceAsStream(filename);

            if (is == null)
            {
                throw new IllegalArgumentException("File not found: " + filename);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is)))
            {
                int lineNumber=0;

                while ((line = br.readLine()) != null)
                {

                    lineNumber++;

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    //χωρίζονται απο ===
                    String[] values = line.split("===", 3);

                    if (values.length < 3) {
                        System.out.println("Invalid line in " + filename + " at line " + lineNumber + ": " + line);
                        continue;
                    }
                    try
                    {
                        new Question(values[0].trim(),
                                     values[1].trim().replace("\\n","\n"),
                                     Integer.parseInt(values[2].trim()),
                                     category);
                    }
                    catch (NumberFormatException e)
                    {
                        new Question(values[0].trim(),
                                     values[1].trim().replace("\\n","\n"),
                                     category);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}