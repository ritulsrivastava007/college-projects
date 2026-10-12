import java.util.Scanner;
public class growthdecay {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Exponential Growth/Decay Model ===");
        System.out.println("Formula: P(t) = P0 * e^(k * t)");
        System.out.println("k > 0 for growth, k < 0 for decay");

        System.out.print("Enter initial amount P0: ");
        double P0 = scanner.nextDouble();

        System.out.print("Enter rate constant k: ");
        double k = scanner.nextDouble();

        System.out.print("Enter time t: ");
        double t = scanner.nextDouble();

        double Pt = P0 * Math.exp(k * t);

        System.out.printf("Initial value: %.2f%n", P0);
        System.out.printf("Final value at t = %.2f: %.2f%n", t, Pt);

        if (k > 0) {
            System.out.println("This is a GROWTH model (e.g., population growth)");
        } else {
            System.out.println("This is a DECAY model (e.g., radioactive decay)");
        }

        System.out.print("Enter number of time steps for simulation: ");
        int steps = scanner.nextInt();
        System.out.println("Time | Amount");
        System.out.println("-----|--------");
        for (int i = 0; i <= steps; i++) {
            double currentTime = (double) i * t / steps;
            double currentValue = P0 * Math.exp(k * currentTime);
            System.out.printf("%.1f  | %.2f%n", currentTime, currentValue);
        }

        scanner.close();
        System.out.println("\nSimulation complete.");
    }
}
