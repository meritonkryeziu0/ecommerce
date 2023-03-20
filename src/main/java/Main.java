import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main extends Quarkus {
    public static void main(String ... args) {
        Quarkus.run(args);
    }
}