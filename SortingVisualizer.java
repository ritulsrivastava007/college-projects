import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortingVisualizer extends JFrame {
    private static final int DEFAULT_SIZE = 35;

    private int[] values;
    private final List<Step> steps = new ArrayList<>();
    private int stepIndex = 0;
    private int comparisons = 0;
    private int swaps = 0;
    private boolean running = false;

    private final JComboBox<String> algorithmBox = new JComboBox<>(
            new String[]{"Bubble Sort", "Selection Sort", "Merge Sort", "Quick Sort"});
    private final JSlider sizeSlider = new JSlider(10, 100, DEFAULT_SIZE);
    private final JSlider speedSlider = new JSlider(1, 100, 55);
    private final JLabel statusLabel = new JLabel("Generate an array, then press Start.");
    private final JLabel statsLabel = new JLabel("Comparisons: 0    Swaps/Writes: 0");
    private final JLabel complexityLabel = new JLabel("Time complexity: —");
    private final JButton startButton = new JButton("Start");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton resetButton = new JButton("Reset");
    private final JButton generateButton = new JButton("Generate New Array");
    private final SortPanel sortPanel = new SortPanel();
    private Timer timer;

    public SortingVisualizer() {
        super("Sorting Algorithm Visualizer");
        values = randomArray(DEFAULT_SIZE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 580));
        setSize(1100, 720);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(new Color(245, 247, 251));
        setContentPane(root);

        JLabel heading = new JLabel("Sorting Algorithm Visualizer");
        heading.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel subtitle = new JLabel("Watch four classic sorting algorithms work step by step.");
        subtitle.setForeground(new Color(90, 98, 112));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        controls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 234)),
                new EmptyBorder(10, 10, 10, 10)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        controls.add(new JLabel("Algorithm:"), c);
        c.gridx = 1;
        algorithmBox.setPreferredSize(new Dimension(150, 28));
        controls.add(algorithmBox, c);

        c.gridx = 2;
        controls.add(new JLabel("Array size:"), c);
        c.gridx = 3;
        sizeSlider.setPreferredSize(new Dimension(150, 35));
        sizeSlider.setMajorTickSpacing(30);
        sizeSlider.setPaintTicks(true);
        controls.add(sizeSlider, c);

        c.gridx = 4;
        controls.add(new JLabel("Speed:"), c);
        c.gridx = 5;
        speedSlider.setPreferredSize(new Dimension(130, 35));
        speedSlider.setInverted(true);
        speedSlider.setToolTipText("Left is slower; right is faster.");
        controls.add(speedSlider, c);

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 2;
        generateButton.setFocusPainted(false);
        controls.add(generateButton, c);
        c.gridx = 2;
        c.gridwidth = 1;
        startButton.setFocusPainted(false);
        controls.add(startButton, c);
        c.gridx = 3;
        pauseButton.setFocusPainted(false);
        pauseButton.setEnabled(false);
        controls.add(pauseButton, c);
        c.gridx = 4;
        resetButton.setFocusPainted(false);
        controls.add(resetButton, c);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(titlePanel, BorderLayout.NORTH);
        top.add(controls, BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH);

        sortPanel.setPreferredSize(new Dimension(900, 420));
        root.add(sortPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(4, 2, 0, 2));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        complexityLabel.setForeground(new Color(75, 82, 96));
        bottom.add(statusLabel);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(statsLabel);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(complexityLabel);
        root.add(bottom, BorderLayout.SOUTH);

        timer = new Timer(delayFromSpeed(), e -> advanceOneStep());

        generateButton.addActionListener(e -> generateNewArray());
        startButton.addActionListener(e -> startSorting());
        pauseButton.addActionListener(e -> togglePause());
        resetButton.addActionListener(e -> resetVisualization());
        algorithmBox.addActionListener(e -> updateComplexity());
        sizeSlider.addChangeListener(e -> {
            if (!sizeSlider.getValueIsAdjusting() && !running) {
                values = randomArray(sizeSlider.getValue());
                steps.clear();
                stepIndex = 0;
                comparisons = 0;
                swaps = 0;
                sortPanel.setValues(values, -1, -1, new boolean[values.length]);
                updateStats();
                statusLabel.setText("Array size updated. Press Start to visualize.");
            }
        });
        speedSlider.addChangeListener(e -> {
            if (timer != null) timer.setDelay(delayFromSpeed());
        });

        sortPanel.setValues(values, -1, -1, new boolean[values.length]);
        updateComplexity();
        updateStats();
    }

    private int delayFromSpeed() {
        return 205 - (speedSlider.getValue() * 2);
    }

    private int[] randomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) array[i] = 10 + random.nextInt(91);
        return array;
    }

    private void generateNewArray() {
        stopTimer();
        running = false;
        values = randomArray(sizeSlider.getValue());
        steps.clear();
        stepIndex = 0;
        comparisons = 0;
        swaps = 0;
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        sortPanel.setValues(values, -1, -1, new boolean[values.length]);
        statusLabel.setText("New random array generated.");
        updateStats();
    }

    private void startSorting() {
        if (running) return;
        if (isSorted(values)) {
            values = randomArray(sizeSlider.getValue());
            sortPanel.setValues(values, -1, -1, new boolean[values.length]);
        }

        steps.clear();
        stepIndex = 0;
        comparisons = 0;
        swaps = 0;

        int[] working = values.clone();
        boolean[] sorted = new boolean[working.length];
        String algorithm = (String) algorithmBox.getSelectedItem();

        switch (algorithm) {
            case "Selection Sort" -> buildSelectionSort(working, sorted);
            case "Merge Sort" -> buildMergeSort(working, sorted);
            case "Quick Sort" -> buildQuickSort(working, sorted);
            default -> buildBubbleSort(working, sorted);
        }

        for (int i = 0; i < sorted.length; i++) sorted[i] = true;
        addStep(working, -1, -1, sorted, "Sorting complete!");

        running = true;
        startButton.setEnabled(false);
        generateButton.setEnabled(false);
        algorithmBox.setEnabled(false);
        sizeSlider.setEnabled(false);
        pauseButton.setEnabled(true);
        pauseButton.setText("Pause");
        statusLabel.setText("Sorting " + algorithm + "...");
        timer.setDelay(delayFromSpeed());
        timer.start();
    }

    private void togglePause() {
        if (!running) return;
        if (timer.isRunning()) {
            timer.stop();
            pauseButton.setText("Resume");
            statusLabel.setText("Paused. Press Resume to continue.");
        } else {
            timer.start();
            pauseButton.setText("Pause");
            statusLabel.setText("Sorting in progress...");
        }
    }

    private void resetVisualization() {
        stopTimer();
        running = false;
        steps.clear();
        stepIndex = 0;
        comparisons = 0;
        swaps = 0;
        values = randomArray(sizeSlider.getValue());
        sortPanel.setValues(values, -1, -1, new boolean[values.length]);
        startButton.setEnabled(true);
        generateButton.setEnabled(true);
        algorithmBox.setEnabled(true);
        sizeSlider.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        statusLabel.setText("Reset complete. Press Start to begin.");
        updateStats();
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
    }

    private void advanceOneStep() {
        if (stepIndex >= steps.size()) {
            finishSorting();
            return;
        }
        Step step = steps.get(stepIndex++);
        comparisons = step.comparisons;
        swaps = step.swaps;
        sortPanel.setValues(step.array, step.a, step.b, step.sorted);
        statusLabel.setText(step.message);
        updateStats();
        if (stepIndex >= steps.size()) finishSorting();
    }

    private void finishSorting() {
        stopTimer();
        running = false;
        startButton.setEnabled(true);
        generateButton.setEnabled(true);
        algorithmBox.setEnabled(true);
        sizeSlider.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        statusLabel.setText("Finished! The array is sorted.");
    }

    private void updateStats() {
        statsLabel.setText("Comparisons: " + comparisons + "    Swaps/Writes: " + swaps);
    }

    private void updateComplexity() {
        String algorithm = (String) algorithmBox.getSelectedItem();
        String complexity;
        if ("Bubble Sort".equals(algorithm)) {
            complexity = "Time complexity: Best O(n) with early exit | Average/Worst O(n²) | Space O(1)";
        } else if ("Selection Sort".equals(algorithm)) {
            complexity = "Time complexity: Best/Average/Worst O(n²) | Space O(1)";
        } else if ("Merge Sort".equals(algorithm)) {
            complexity = "Time complexity: Best/Average/Worst O(n log n) | Space O(n)";
        } else {
            complexity = "Time complexity: Average O(n log n) | Worst O(n²) | Space O(log n) average";
        }
        complexityLabel.setText(complexity);
    }

    private boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) return false;
        }
        return true;
    }

    private void addStep(int[] array, int a, int b, boolean[] sorted, String message) {
        steps.add(new Step(array, a, b, sorted, comparisons, swaps, message));
    }

    private void buildBubbleSort(int[] a, boolean[] sorted) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean changed = false;
            for (int j = 0; j < n - i - 1; j++) {
                comparisons++;
                addStep(a, j, j + 1, sorted, "Bubble Sort: comparing adjacent bars.");
                if (a[j] > a[j + 1]) {
                    int temp = a[j]; a[j] = a[j + 1]; a[j + 1] = temp;
                    swaps++;
                    changed = true;
                    addStep(a, j, j + 1, sorted, "Out of order — swap the two values.");
                }
            }
            sorted[n - i - 1] = true;
            addStep(a, -1, n - i - 1, sorted, "Largest remaining value is in its final position.");
            if (!changed) break;
        }
        if (n > 0) sorted[0] = true;
    }

    private void buildSelectionSort(int[] a, boolean[] sorted) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                addStep(a, min, j, sorted, "Selection Sort: compare the current minimum with the next value.");
                if (a[j] < a[min]) {
                    min = j;
                    addStep(a, i, min, sorted, "A new minimum value was found.");
                }
            }
            if (min != i) {
                int temp = a[i]; a[i] = a[min]; a[min] = temp;
                swaps++;
                addStep(a, i, min, sorted, "Place the smallest remaining value at the start of the unsorted section.");
            }
            sorted[i] = true;
            addStep(a, i, -1, sorted, "The next position is now sorted.");
        }
        if (n > 0) sorted[n - 1] = true;
    }

    private void buildMergeSort(int[] a, boolean[] sorted) {
        mergeSort(a, 0, a.length - 1, sorted);
        for (int i = 0; i < sorted.length; i++) sorted[i] = true;
    }

    private void mergeSort(int[] a, int left, int right, boolean[] sorted) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        addStep(a, left, right, sorted, "Merge Sort: divide the current range into two halves.");
        mergeSort(a, left, mid, sorted);
        mergeSort(a, mid + 1, right, sorted);
        merge(a, left, mid, right, sorted);
    }

    private void merge(int[] a, int left, int mid, int right, boolean[] sorted) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) {
            comparisons++;
            addStep(a, i, j, sorted, "Merge Sort: compare the front values of both halves.");
            if (a[i] <= a[j]) temp[k++] = a[i++];
            else temp[k++] = a[j++];
        }
        while (i <= mid) temp[k++] = a[i++];
        while (j <= right) temp[k++] = a[j++];
        for (int x = 0; x < temp.length; x++) {
            a[left + x] = temp[x];
            swaps++;
            addStep(a, left + x, -1, sorted, "Write the next value into the merged range.");
        }
        addStep(a, left, right, sorted, "The current range has been merged in sorted order.");
    }

    private void buildQuickSort(int[] a, boolean[] sorted) {
        quickSort(a, 0, a.length - 1, sorted);
        for (int i = 0; i < sorted.length; i++) sorted[i] = true;
    }

    private void quickSort(int[] a, int low, int high, boolean[] sorted) {
        if (low > high) return;
        if (low == high) {
            sorted[low] = true;
            addStep(a, low, -1, sorted, "This partition contains one value and is already sorted.");
            return;
        }
        int pivotIndex = partition(a, low, high, sorted);
        sorted[pivotIndex] = true;
        addStep(a, pivotIndex, -1, sorted, "Pivot placed in its final position.");
        quickSort(a, low, pivotIndex - 1, sorted);
        quickSort(a, pivotIndex + 1, high, sorted);
    }

    private int partition(int[] a, int low, int high, boolean[] sorted) {
        int pivot = a[high];
        int i = low - 1;
        addStep(a, high, -1, sorted, "Quick Sort: choose the last value as the pivot.");
        for (int j = low; j < high; j++) {
            comparisons++;
            addStep(a, j, high, sorted, "Compare the current value with the pivot.");
            if (a[j] <= pivot) {
                i++;
                if (i != j) {
                    int temp = a[i]; a[i] = a[j]; a[j] = temp;
                    swaps++;
                    addStep(a, i, j, sorted, "Move a value smaller than or equal to the pivot to the left.");
                }
            }
        }
        if (i + 1 != high) {
            int temp = a[i + 1]; a[i + 1] = a[high]; a[high] = temp;
            swaps++;
            addStep(a, i + 1, high, sorted, "Place the pivot between the two partitions.");
        }
        return i + 1;
    }

    private static class Step {
        final int[] array;
        final int a, b, comparisons, swaps;
        final boolean[] sorted;
        final String message;

        Step(int[] array, int a, int b, boolean[] sorted, int comparisons, int swaps, String message) {
            this.array = array.clone();
            this.a = a;
            this.b = b;
            this.sorted = sorted.clone();
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.message = message;
        }
    }

    private class SortPanel extends JPanel {
        private int[] displayValues = new int[0];
        private int highlightedA = -1;
        private int highlightedB = -1;
        private boolean[] sorted = new boolean[0];

        SortPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 234)),
                    new EmptyBorder(12, 12, 12, 12)));
        }

        void setValues(int[] values, int a, int b, boolean[] sortedFlags) {
            displayValues = values.clone();
            highlightedA = a;
            highlightedB = b;
            sorted = sortedFlags.clone();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int left = 18, right = 18, top = 35, bottom = 42;
            int chartWidth = Math.max(1, width - left - right);
            int chartHeight = Math.max(1, height - top - bottom);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(55, 63, 79));
            g2.drawString("Array values", left, 22);

            if (displayValues.length == 0) {
                g2.dispose();
                return;
            }

            int max = 1;
            for (int value : displayValues) max = Math.max(max, value);
            double slot = (double) chartWidth / displayValues.length;
            int barWidth = Math.max(2, (int) Math.floor(slot * 0.78));

            for (int i = 0; i < displayValues.length; i++) {
                int barHeight = (int) ((displayValues[i] / (double) max) * chartHeight);
                int x = left + (int) (i * slot) + (int) ((slot - barWidth) / 2);
                int y = top + chartHeight - barHeight;

                Color barColor = new Color(77, 139, 245);
                if (sorted != null && i < sorted.length && sorted[i]) barColor = new Color(43, 174, 115);
                if (i == highlightedA || i == highlightedB) barColor = new Color(244, 156, 66);

                g2.setColor(barColor);
                g2.fillRoundRect(x, y, barWidth, barHeight, 5, 5);

                if (displayValues.length <= 40 && barWidth >= 12) {
                    g2.setColor(new Color(55, 63, 79));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    String text = String.valueOf(displayValues[i]);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(text, x + (barWidth - fm.stringWidth(text)) / 2, Math.max(top + 12, y - 4));
                }
            }

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(new Color(90, 98, 112));
            g2.drawString("Blue: unsorted   Orange: comparing/swapping   Green: sorted", left, height - 13);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use the default Swing look and feel if the system look and feel is unavailable.
            }
            new SortingVisualizer().setVisible(true);
        });
    }
}
