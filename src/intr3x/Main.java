package intr3x;

import intr3x.engine.Engine;
import intr3x.engine.Engine3D;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();

        Engine engine = new Engine3D(500, 900);
        engine.start();
    }
}
