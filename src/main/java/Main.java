
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
public class Main {
    public static void main(String[] args) {
        List<String[]> data = new ArrayList<>();
        List<Integer> lastColumn = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("testdata.csv"))) {
            String line;
            while ((line = br.readLine())!= null) {
                String[] values = line.split(",");
                data.add(new String[]{values[0], values[1]});
                lastColumn.add(Integer.parseInt(values[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the List to a 2D array
        String[][] dataArray = new String[data.size()][2];
        for (int i = 0; i < data.size(); i++) {
            dataArray[i] = data.get(i);
        }

        // Print the arrays
        for (String[] row : dataArray) {
            System.out.println(Arrays.toString(row));
        }
        for (int value : lastColumn) {
            System.out.println(value);
        }
    }
}