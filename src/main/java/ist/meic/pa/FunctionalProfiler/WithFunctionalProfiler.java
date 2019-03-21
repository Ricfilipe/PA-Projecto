/*
 * Main class of our program, responsible for receiving the classes we want to check the
 * number of reads and writes, creates the necessary tools for the program to work.
 */
package ist.meic.pa.FunctionalProfiler;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;

public class WithFunctionalProfiler {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) throws Throwable {
        if (args.length < 1) {

        } else {
            Translator translator = new ProfilerTranslator();
            ClassPool pool = ClassPool.getDefault();
            pool.importPackage("ist.meic.pa.FunctionalProfiler");
            Loader classLoader = new Loader();
            classLoader.addTranslator(pool, translator);
            String[] restArgs = new String[args.length - 1];
            System.arraycopy(args, 1, restArgs, 0, restArgs.length);
            classLoader.run(args[0], restArgs);
        }
    }
}
