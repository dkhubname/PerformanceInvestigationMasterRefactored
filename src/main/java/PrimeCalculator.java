import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PrimeCalculator {
    public static void main(String[] args) throws InterruptedException {

        int value = 0;
        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
        }

        if (value < 1) {
            System.out.println("Please enter a number greater 0 and less than or equal to " + Integer.MAX_VALUE);
        } else {
            for (Integer prime : getPrimes(Integer.parseInt(args[0])))
                System.out.print(prime + "\n");
        }

    }

    private static List<Integer> getPrimes(int maxPrime) throws InterruptedException {


        List<Integer> allNumbers = Stream.generate(new Supplier<Integer>() {
            int i = 1;

            @Override
            public Integer get() {
                return i++;
            }

        }).limit(maxPrime).collect(Collectors.toList());


        List<Integer> aprovedPrimeNumbers = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(maxPrime);
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executors = Executors.newFixedThreadPool(threads);
        synchronized (aprovedPrimeNumbers) {
            for (Integer candidate : allNumbers) {
                executors.submit(() -> {
                    if (isPrime(allNumbers, candidate)) {
                        aprovedPrimeNumbers.add(candidate);
                    }
                    latch.countDown();
                });
            }
        }
        latch.await();
        executors.shutdownNow();

        return aprovedPrimeNumbers;
    }

    private static boolean isPrime(List<Integer> primeNumbers, Integer candidate) {
        if (candidate == 1)
            return false;
        for (Integer j : primeNumbers.subList(1, candidate - 1)) {
            if (candidate % j == 0) {
                return false;
            }
        }
        return true;
    }
}