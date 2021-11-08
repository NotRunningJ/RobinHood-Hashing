package lvc.cds;

import java.util.Random;


public final class App {
    private static Random r = new Random();

    public static void main(String[] args) {
        // testing how effective the robinhood hashing is
        tableComparison();

        /**
         * The robinhood hashing strategy seems to greatly improve how close our load factor
         * can get to 100% without seeing much or any negative effects on how many different 
         * elements we have to search through before finding the one we are looking for.
         * 
         * With that said, the ideal load factor for a regular LinearProbedHashTable 
         * would be between 80 and 90% as searches are still fast in those load
         * factors, and depending on the items being stored, about 5 searches is not bad.
         * anything less than 80% capacity for a linear table is a waste of space
         * because it only takes about 1 probe to find items at 80% capacity.
         * An ideal load factor for the RobinHoodHashTable is in the 95-99% range. Even at 99% 
         * capacity, its searches still only take about 5 probes to find the item.
         * Anything less than 95% on the robin hood table is a waste of space because it 
         * only probes once on average with a load factor of 95%.
         */
    }


    /**
     * testing both tables with different load factors on number of probes
     * when calling find
     */
    public static void tableComparison() {
        // size and load factor of the tables
        final int SIZE = 1_000_000;
        double[] loads = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.95, 0.99};

        System.out.println("Table for the get() method probes");
        System.out.println("Load Factor" + "\t" + "Avg Linear Probes" + "\t" + "Avg Robin probes");

        // prints a table of load factor and respective number of probes to find values for the two tables
        for (int i = 0; i < loads.length; i++) {
            int linearCount = 0;
            int robinCount = 0;
            LinearProbedHashTable<Integer, String> lpht = new LinearProbedHashTable<>((int)(SIZE/loads[i])+1, loads[i]);
            RobinHoodHashTable<Integer, String> rht = new RobinHoodHashTable<>((int)(SIZE/loads[i])+1, loads[i]);
            int[] targets = new int[SIZE];

            for (int j = 0; j < SIZE; ++j) {
                int arr = r.nextInt();
                lpht.put(arr, "Boo");
                rht.put(arr, "Boo");
                targets[j] = arr;
            }

            for (int k = 0; k < 100_000; ++k) {
                int arr = r.nextInt();
                linearCount += lpht.getProbed(arr);
                robinCount += rht.getProbed(arr);
            }

            double linearAVG = 1.0* linearCount/SIZE;
            double robinAVG = 1.0* robinCount/SIZE;
            System.out.println(loads[i] + "\t\t" + linearAVG + "\t\t\t" + robinAVG);
        }
    }

}
