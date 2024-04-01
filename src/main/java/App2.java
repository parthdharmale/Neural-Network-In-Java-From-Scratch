
// import com.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.lang.Math;

public class App2 {
    public static void main(String[] args) {
        App2 app = new App2();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of layers: ");
        int numLayers = scanner.nextInt();
        System.out.print("Enter the number of epochs you want to train the model on: ");
        int epochs = scanner.nextInt();
        scanner.close();
        app.trainAndPredict(numLayers, epochs);
    }

    public void trainAndPredict(int numLayers, int epochs) {

        // System.out.println(numLayers);
        List<List<Integer>> data = new ArrayList<List<Integer>>();
        data.add(Arrays.asList(115, 66));//Female
        data.add(Arrays.asList(175, 78));//Male
        data.add(Arrays.asList(205, 72));//Male
        data.add(Arrays.asList(120, 67));//Female
        data.add(Arrays.asList(120, 52));//Female
        data.add(Arrays.asList(185, 87));//Male
        data.add(Arrays.asList(199, 77));//Male
        data.add(Arrays.asList(105, 60));//Female
        data.add(Arrays.asList(150, 66));//Female
        data.add(Arrays.asList(160, 78));//Male
        data.add(Arrays.asList(189, 76));//Male
        data.add(Arrays.asList(142, 60));//Female

        List<Double> answers = Arrays.asList(1.0, 0.0, 0.0, 1.0, 1.0,0.0,0.0,1.0,1.0,0.0,0.0,1.0);

        Network network500 = new Network(numLayers, epochs);

        // Train the network
        network500.train(data, answers);

        // Network network1000 = new Network(numLayers, 1000);
        // network1000.train(data, answers);

        System.out.println("");

        System.out.println(String.format("  male, 167, 73: network500: %.10f ", network500.predict(170, 73)));
        System.out.println(String.format("female, 105, 67: network500: %.10f ", network500.predict(105, 61)));
        System.out.println(String.format("female, 120, 72: network500: %.10f ", network500.predict(120, 72)));
        System.out.println(String.format(" male, 143, 67: network500: %.10f ", network500.predict(173, 77)));
        System.out.println(String.format(" male, 150, 66: network500: %.10f ", network500.predict(170, 66)));

    }

    class Network {
        int epochs = 0; // 1000;.
        int numLayers = 4;
        Double learnFactor = null;
        private List<Neuron> neurons;

        public Network(int numLayers, int epochs) {
            this.numLayers = numLayers;
            this.epochs = epochs;
            this.neurons = generateNeurons(numLayers);
        }

        // Generate
        // Neurons-------------------------------------------------------------------------------------

        public List<Neuron> generateNeurons(int numLayers) {
            int numNeurons = numLayers * (numLayers + 1) / 2;
            List<Neuron> temp = new ArrayList<>(numNeurons);
            for (int i = 0; i < numNeurons; i++) {
                temp.add(new Neuron());
            }
            System.out.println(numLayers);
            System.out.println(numNeurons);
            return temp;
        }

        public Network(int epochs, Double learnFactor) {
            this.epochs = epochs;
            this.learnFactor = learnFactor;
        }

        // Predict
        // Method-------------------------------------------------------------------------------------

        public Double predict(double input1, double input2) {
            int numLayers = this.numLayers;
            // System.out.println(numLayers);
            // System.out.println("We are inside predict function now");

            int n = numLayers * (numLayers + 1) / 2; // Number of neurons

            int last = n - 1; // Index of the Last neuron in array
            int counter = numLayers; // Index of the layer from right.

            int currNeuron = counter - 1; // Number of neurons left to process in current layer

            double[] dp = new double[n]; // DP array to store intermediate results
            Arrays.fill(dp, 0);

            for (int i = 0; i <= last; i++) {
                // If the index of current neuron is < num of layers, it means the neuron belongs to input layer hence takes direct user input
                if (i < numLayers) {
                    
                    dp[i] = neurons.get(i).compute(input1, input2);
                } // Else it takes input from two neurons from the previous layer
                else {
                    dp[i] = neurons.get(i).compute(dp[i - counter], dp[i - counter + 1]);

                    currNeuron--;
                    if (currNeuron == 0) {
                        // System.out.println("We are in Layer " + (numLayers - counter + 2));
                        counter--;
                        currNeuron = counter - 1;
                    }
                }
            }
            return dp[last];
        }

        // Backpropagation
        // ----------------------------------------------------------------------------
        public void backpropagation(List<List<Integer>> data, List<Double> answers, double learningRate) {
            for (int i = 0; i < 4; i++) {
                double input1 = data.get(i).get(0);
                double input2 = data.get(i).get(1);
                double predictedOutput = predict(input1, input2);
                double actualOutput = answers.get(i);
                double error = actualOutput - predictedOutput;

                // Update weights and biases using backpropagation algorithm
                for (Neuron neuron : neurons) {
                    double derivative = Util.sigmoidDeriv(predictedOutput);
                    double delta = error * derivative;
                    neuron.bias += learningRate * delta;
                    neuron.weight1 += learningRate * delta * input1;
                    neuron.weight2 += learningRate * delta * input2;
                }
            }
        }

        // Train
        // Method-------------------------------------------------------------------------------------

        public void train(List<List<Integer>> data, List<Double> answers) {
            int numLayers = this.numLayers;

            int n = numLayers * (numLayers + 1) / 2;
            System.out.println(epochs);
            Double bestEpochLoss = null;
            for (int epoch = 0; epoch < epochs; epoch++) {
                // adapt neuron
                Neuron epochNeuron = neurons.get(epoch % 6);
                epochNeuron.mutate(this.learnFactor);

                List<Double> predictions = new ArrayList<Double>();
                for (int i = 0; i < 4; i++) {
                    predictions.add(i, this.predict(data.get(i).get(0), data.get(i).get(1)));
                }
                Double thisEpochLoss = Util.meanSquareLoss(answers, predictions);

                if (epoch % 50 == 0)
                    System.out.println(String.format("Epoch: %s | bestEpochLoss: %.15f | thisEpochLoss: %.15f", epoch,
                            bestEpochLoss, thisEpochLoss));

                if (bestEpochLoss == null) {
                    bestEpochLoss = thisEpochLoss;
                    epochNeuron.remember();
                } else {
                    if (thisEpochLoss < bestEpochLoss) {
                        bestEpochLoss = thisEpochLoss;
                        epochNeuron.remember();
                    } else {
                        epochNeuron.forget();
                    }
                }
            }
        }

    }

    class Neuron {
        Random random = new Random();
  
        private Double oldBias = random.nextDouble(-1, 1), bias = 4.227263774752734;
        public Double oldWeight1 = random.nextDouble(-1, 1), weight1 = -8.188992192876949;
        private Double oldWeight2 = random.nextDouble(-1, 1), weight2 = -4.67817295490573;

        public String toString() {
            return String.format(
                    "oldBias: %.15f | bias: %.15f | oldWeight1: %.15f | weight1: %.15f | oldWeight2: %.15f | weight2: %.15f",
                    this.oldBias, this.bias, this.oldWeight1, this.weight1, this.oldWeight2, this.weight2);
        }

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
            ;
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
            // this.input1 = input1; this.input2 = input2;
            double preActivation = (this.weight1 * input1) + (this.weight2 * input2) + this.bias;
            double output = Util.sigmoid(preActivation);
            return output;
        }
    }

    class Util {
        public static double sigmoid(double in) {
            return 1 / (1 + Math.exp(-in));
        }

        public static double sigmoidDeriv(double in) {
            double sigmoid = Util.sigmoid(in);
            return sigmoid * (1 - in);
        }

        /** Assumes array args are same length */
        public static Double meanSquareLoss(List<Double> correctAnswers, List<Double> predictedAnswers) {
            double sumSquare = 0;
            for (int i = 0; i < predictedAnswers.size(); i++) {
                double error = correctAnswers.get(i) - predictedAnswers.get(i);
                sumSquare += (error * error);
            }
            return sumSquare / (correctAnswers.size());
        }
    }
}
