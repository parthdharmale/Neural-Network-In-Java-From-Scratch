// import java.io.BufferedReader;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.List;

// public class Main {
//     public static void main(String[] args) {
//         List<Integer[]> data = new ArrayList<>();
//         List<Integer> lastColumn = new ArrayList<>();

//         try {
//             FileInputStream fis = new FileInputStream("testdata.csv");
//             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
//             BufferedReader br = new BufferedReader(isr);

//             String line;
//             line = br.readLine();
//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(",");
//                 // for(String v : values) System.out.println(v); // Debugging purposes only!
//                 data.add(new Integer[] { Integer.valueOf(values[0]), Integer.valueOf(values[1]) });
//                 lastColumn.add(Integer.parseInt(values[2]));
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         Integer[][] dataArray = new Integer[data.size()][2];
//         for (int i = 0; i < data.size(); i++) {
//             dataArray[i] = data.get(i);
//         }

//     }
// }
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<List<Integer>> data = new ArrayList<>();
        List<Double> lastColumn = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("trainingdata.csv"), StandardCharsets.UTF_8))) {

            String line;
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<Integer> row = new ArrayList<>();
                row.add(Integer.valueOf(values[0]));
                row.add(Integer.valueOf(values[1]));
                data.add(row);
                lastColumn.add(Double.parseDouble(values[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Integer[][] dataArray = new Integer[data.size()][2];
        for (int i = 0; i < data.size(); i++) {
            List<Integer> row = data.get(i);
            dataArray[i] = row.toArray(new Integer[0]);
        }

    }
}
