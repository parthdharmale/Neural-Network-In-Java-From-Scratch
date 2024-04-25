import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;

public class App2 {
    public static void main(String[] args) {

        List<List<Integer>> data = new ArrayList<>();
        List<Double> lastColumn = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("trainingdata.csv"), StandardCharsets.UTF_8))) {

            String line;
            br.readLine();
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

        SwingUtilities.invokeLater(() -> {
            new App2().createAndShowGUI(data, lastColumn);
        });
    }

    private void createAndShowGUI(List<List<Integer>> data, List<Double> lastColumn) {
        JFrame frame = new JFrame("Gender Prediction");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(400, 200));

        // panel.setLayout(new GridLayout(0, 2, 10, 10));

        JLabel layersLabel = new JLabel("Number of Layers:");
        JTextField layersField = new JTextField(10);
        JLabel epochsLabel = new JLabel("Number of Epochs:");
        JTextField epochsField = new JTextField(10);
        JLabel heightLabel = new JLabel("Enter the height in cm:");
        JTextField heightField = new JTextField(10);
        JLabel weightLabel = new JLabel("Enter the weight in Kg:");
        JTextField weightField = new JTextField(10);
        JButton submitButton = new JButton("Submit");

        panel.add(layersLabel);
        panel.add(layersField);
        panel.add(epochsLabel);
        panel.add(epochsField);
        panel.add(heightLabel);
        panel.add(heightField);
        panel.add(weightLabel);
        panel.add(weightField);
        panel.add(submitButton);

        submitButton.addActionListener(e -> {
            int numLayers = Integer.parseInt(layersField.getText());
            int epochs = Integer.parseInt(epochsField.getText());
            int newHeight = Integer.parseInt(heightField.getText());
            int newWeight = Integer.parseInt(weightField.getText());
            trainAndPredict(numLayers, epochs, newHeight, newWeight, data, lastColumn);
        });

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public void trainAndPredict(int numLayers, int epochs, int newHeight, int newWeight, List<List<Integer>> data,
            List<Double> lastColumn) {

        Network network500 = new Network(numLayers, epochs);

        // Train the network
        network500.train(data, lastColumn);
        double prediction = network500.predict(newHeight, newWeight);
        String result = (prediction <= 0.5) ? "Male" : "Female";
        // Display predictions
        JOptionPane.showMessageDialog(null,
                "Number Of Neurons in the network:" + numLayers * (numLayers + 1) / 2 + "\n" + "Height: " + newHeight
                        + " Weight: " + newWeight + "\n" + "Gender: " + result + " Prediction: " + prediction

        );
    }

    static class Network {
        int epochs;
        int numLayers;
        Double learnFactor;
        List<Neuron> neurons;

        public Network(int numLayers, int epochs) {
            this.numLayers = numLayers;
            this.epochs = epochs;
            this.neurons = generateNeurons(numLayers);
        }

        public List<Neuron> generateNeurons(int numLayers) {
            int numNeurons = numLayers * (numLayers + 1) / 2;
            List<Neuron> temp = new ArrayList<>(numNeurons);
            for (int i = 0; i < numNeurons; i++) {
                temp.add(new Neuron());
            }
            return temp;
        }

        public Double predict(double input1, double input2) {
            int n = numLayers * (numLayers + 1) / 2;
            int last = n - 1;
            int counter = numLayers;
            int currNeuron = counter - 1;
            double[] dp = new double[n];
            Arrays.fill(dp, 0);

            for (int i = 0; i <= last; i++) {
                if (i < numLayers) {
                    dp[i] = neurons.get(i).compute(input1, input2);
                } else {
                    dp[i] = neurons.get(i).compute(dp[i - counter], dp[i - counter + 1]);
                    currNeuron--;
                    if (currNeuron == 0) {
                        counter--;
                        currNeuron = counter - 1;
                    }
                }
            }
            return dp[last];
        }

        public void train(List<List<Integer>> data, List<Double> answers) {
            Double bestEpochLoss = null;
            for (int epoch = 0; epoch < epochs; epoch++) {
                Neuron epochNeuron = neurons.get(epoch % 6);
                epochNeuron.mutate(this.learnFactor);

                List<Double> predictions = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    predictions.add(this.predict(data.get(i).get(0), data.get(i).get(1)));
                }
                Double thisEpochLoss = Util.meanSquareLoss(answers, predictions);
                if (epoch % 50 == 0)
                    System.out.println(String.format("Epoch: %s | bestEpochLoss: %.15f | thisEpochLoss: %.15f", epoch,
                            bestEpochLoss, thisEpochLoss));
                if (bestEpochLoss == null || thisEpochLoss < bestEpochLoss) {
                    bestEpochLoss = thisEpochLoss;
                    epochNeuron.remember();
                } else {
                    epochNeuron.forget();
                }
            }
        }
    }

    static class Neuron {
        Random random = new Random();
        Double oldBias = random.nextDouble(-1, 1), bias = 4.227263774752734;
        Double oldWeight1 = random.nextDouble(-1, 1), weight1    = -8.188992192876949;
        Double oldWeight2 = random.nextDouble(-1, 1), weight2 = -4.67817295490573;

        public void mutate(Double learnFactor) {
            int propertyToChange = random.nextInt(0, 3);
            Double changeFactor = (learnFactor == null) ? random.nextDouble(-1, 1)
                    : (learnFactor * random.nextDouble(-1, 1));
            if (propertyToChange == 0) {
                this.bias += changeFactor;
            } else if (propertyToChange == 1) {
                this.weight1 += changeFactor;
            } else {
                this.weight2 += changeFactor;
            }
        }

        public void forget() {
            bias = oldBias;
            weight1 = oldWeight1;
            weight2 = oldWeight2;
        }

        public void remember() {
            oldBias = bias;
            oldWeight1 = weight1;
            oldWeight2 = weight2;
        }

        public double compute(double input1, double input2) {
            double preActivation = (this.weight1 * input1) + (this.weight2 * input2) + this.bias;// G = H*Weight1 + W * Weight2 + bias
            return Util.sigmoid(preActivation);
        }
    }

    static class Util {
        public static double sigmoid(double in) {
            return 1 / (1 + Math.exp(-in));
        }

        public static double meanSquareLoss(List<Double> correctAnswers, List<Double> predictedAnswers) {
            double sumSquare = 0;
            for (int i = 0; i < predictedAnswers.size(); i++) {
                double error = correctAnswers.get(i) - predictedAnswers.get(i);
                sumSquare += (error * error);
            }
            return sumSquare / (correctAnswers.size());
        }
    }
}
