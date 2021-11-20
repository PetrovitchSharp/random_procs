package math;

/**
 * Утилитные методы
 */
public class Utils {
    /**
     * Получение обрезанного справа СП
     * @param proc СП
     * @param shift Сдвиг
     * @return Обрезанный СП
     */
    public static double[] getRightShiftedArray(double[] proc, int shift){
        var shiftedArray = new double[proc.length - shift];

        if (proc.length - shift >= 0) System.arraycopy(proc, 0, shiftedArray, 0, proc.length - shift);

        return shiftedArray;
    }

    /**
     * Получение сдвинутого слева СП
     * @param proc СП
     * @param shift Сдвиг
     * @return Сдвинутый СП
     */
    public static double[] getLeftShiftedArray(double[] proc, int shift){
        var shiftedArray = new double[proc.length - shift];

        if (proc.length - shift >= 0) System.arraycopy(proc, shift, shiftedArray, 0, proc.length - shift);

        return shiftedArray;
    }
}
