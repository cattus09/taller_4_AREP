package edu.escuelaing.arep.app.wordClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays; 

public class InvokeMain {
    public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            Class<?> c = Class.forName(args[0]);
            @SuppressWarnings("rawtypes")
            Class[] argTypes = new Class[] { int.class, int.class };
            Method main = c.getDeclaredMethod("main", argTypes);
            String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
            System.out.format("invoking %s.main()%n", c.getName());
            main.invoke(null, Integer.parseInt(mainArgs[0]), Integer.parseInt(mainArgs[1]));
            // production code should handle these exceptions more gracefully
        } catch (ClassNotFoundException x) {
            x.printStackTrace();
        } catch (NoSuchMethodException x) {
            x.printStackTrace();
        }
    }
}