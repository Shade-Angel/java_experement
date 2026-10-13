package academy.fiveletters.console;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class ConsoleInput implements UserInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public static ConsoleInput fromSystemIn() {
        return new ConsoleInput(new Scanner(System.in, StandardCharsets.UTF_8));
    }

    @Override
    public boolean hasLine() {
        return scanner.hasNextLine();
    }

    @Override
    public String readLine() {
        return scanner.nextLine().trim();
    }

    public void close() {
        scanner.close();
    }
}
