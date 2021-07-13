package ai;

import ai.uni.PathFinderAlgorithm;
import ai.uni.Runner;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        Runner.run(null, "tmp/levels/level9.txt");
        Runner.run(PathFinderAlgorithm.BBFS, "tmp/input/test4.txt");
    }
}
