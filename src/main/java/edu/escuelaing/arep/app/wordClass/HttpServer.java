package edu.escuelaing.arep.app.wordClass;

import static java.lang.System.out;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class HttpServer {
    public static void main(String[] arg) throws ClassNotFoundException{
        @SuppressWarnings("rawtypes")
        Class c = Class.forName("edu.escuelaing.arep.app.Alumno");
        Field[] campos = c.getDeclaredFields();
        printMembers(campos, "campos: ");

        @SuppressWarnings("rawtypes")
        Constructor[] constructores = c.getConstructors();
        printMembers(constructores, "constructores:  ");

        
        Method[] metodos = c.getMethods();
        printMembers(metodos, "metodos");

        //suma(a,b);
    }

public static void suma (int a , int b){
   System.out.println(a+b);
}

    @SuppressWarnings("rawtypes")
    private static void printMembers(Member[] mbrs, String s) {
        out.format("%s:%n", s);
        for (Member mbr : mbrs) {
            if (mbr instanceof Field){
                out.format(" %s%n", ((Field)mbr).toGenericString());
            }else if (mbr instanceof Constructor){
                out.format(" %s%n", ((Constructor)mbr).toGenericString());
            }else if (mbr instanceof Method){
                out.format(" %s%n", ((Method)mbr).toGenericString());
            }  
        }
        if (mbrs.length == 0)
        out.format(" -- No %s --%n", s);
        out.format("%n");
    }
}