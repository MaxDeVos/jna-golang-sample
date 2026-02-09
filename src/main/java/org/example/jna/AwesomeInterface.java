package org.example.jna;

import com.sun.jna.Library;
import com.sun.jna.WString;
import org.example.jna.gotype.GoSlice;
import org.example.jna.gotype.GoString;

/**
 * Demo interface (interface-mapping)
 * <p>
 * Direct mapping performs better for primitive types (including Pointer),
 * while interface mapping has a slight advantage when dealing with complex types.
 *
 * @author 唐家林 on 2021-01-28.
 */
public interface AwesomeInterface extends Library {

    /**
     * Add two numbers and return the result.
     *
     * @param a the first number to add
     * @param b the second number to add
     * @return the result of adding the two numbers
     */
    long add(long a, long b);

    /**
     * Compute the cosine of a given angle.
     *
     * @param val the angle value
     * @return the cosine of the angle
     */
    double cosine(double val);

    /**
     * Sort an array.
     *
     * @param arrays the numbers to be sorted
     */
    void sort(GoSlice arrays);

    /**
     * Print the given value to the console.
     *
     * @param value the value to be printed
     * @return the total number of times this method has been called
     */
    long print(GoString value);

    /**
     * Echo a string.
     *
     * @param value the input string
     * @return the same string as the input
     */
    String echoString(GoString value);

    /**
     * Echo a string.
     *
     * @param value the input string
     * @return the same string as the input
     */
    WString echoWString(GoString value);

    /**
     * Echo a string.
     *
     * @param value the input string
     * @return the same string as the input
     */
    GoString.ByReference echoGoString(GoString value);


}
