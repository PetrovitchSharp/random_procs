package statTests.tests;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.data.function.Function2D;
import org.jfree.data.xy.XYSeries;
import params.HypothesisCheck;
import statTests.tables.KSTable;

import static charts.DataFactory.*;

/**
 * Методы, реализующие критерий Колмогорова-Смирнова
 */
public class KSTest {
    /**
     * Тест Колмогорова-Смирнова
     * @param procSample Выборка точек из СП
     * @param realFunction Теоретическая функция (ФР или КФ)
     * @param checkParams Параметры для теста
     * @param typeOfGenerating Тип генерации СП (0 - ФР, 1 - КФ)
     * @return Пройден \ Не пройден тест
     */
    public static boolean ksTest(double[] procSample, Function2D realFunction, HypothesisCheck checkParams, int typeOfGenerating){
        var table = new KSTable();
        XYSeries subsample;

        if (typeOfGenerating == 0) {
            subsample = getDistributionFunctionData(
                    procSample,
                    checkParams.getNumberOfDegreesOfFreedom())
                    .getSeries(0);
        }
        else {
            subsample = getCorrelationFunctionData(
                    procSample,
                    checkParams.getNumberOfDegreesOfFreedom())
                    .getSeries(0);
        }


        var testValue = ksTestValue(subsample, realFunction);
        var critValue = table.getCritivalValue(
                checkParams.getNumberOfDegreesOfFreedom(),
                checkParams.getSignificanceLevel()
        );

        return testValue <= critValue;
    }

    /**
     * Вычисление статистики для критерия Колмогорова-Смирнова
     * @param procSample Выборка точек из СП
     * @param realFunction Теоретическая функция
     * @return Значение статистики
     */
    private static double ksTestValue(XYSeries procSample, Function2D realFunction){
        var len = procSample.getItemCount();
        var d = new double[len];

        for (var i = 0; i < len; i++){
            d[i] = Math.max(
                    (double) procSample.getY(i) - realFunction.getValue((double) procSample.getX(i)),
                    realFunction.getValue((double) procSample.getX(i)) - (double) procSample.getY(i)
            );
        }

        return StatUtils.max(d) * Math.sqrt(len);
    }
}
