import org.apache.commons.math3.stat.StatUtils;
import org.jfree.data.function.Function2D;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Arrays;
import java.util.Random;

public class CalculateClass {

    //TODO: методы для генерации, проверки гипотезы, получения результатов и тд

    /**
     * Генерация случайного процесса
     * @param proc Параметры случайного процесса
     * @return Случайный процесс
     */
    public static double[] generateRandomProc(RandomProcess proc){
        double[] sample = null;

        if (proc.getWaysOfGeneration().equals("с заданным законном распределения")){
            if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
                sample = getNormalDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getDispersion(),
                        proc.getDistributionLaw().getExpectedValue()
                );
            }
            if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
                sample = getUniformDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getLeft(),
                        proc.getDistributionLaw().getRight()
                );
            }
            if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
                sample = getExponentialDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getIntensity()
                );
            }
        }
        if (proc.getWaysOfGeneration().equals("корреляционная функция")){
            if (proc.getCorrelationFunction().getKindCorrelationFunction().equals("Монотонная")){
                sample = getMonotonousCorrFunction(
                        proc.getNumberOfSamples(),
                        proc.getCorrelationFunction().getAttenuationRates(),
                        0.01
                );
            }
            if (proc.getCorrelationFunction().getKindCorrelationFunction().equals("Колебательная")){
                sample = getOscillatoryCorrFunction(
                        proc.getNumberOfSamples(),
                        proc.getCorrelationFunction().getAttenuationRates(),
                        proc.getCorrelationFunction().getOscillationFrequencyValue(),
                        0.01
                );
            }
        }

        return sample;
    }

    /**
     * Расчет теоретического мат. ожидания
     * @param proc Параметры СП
     * @return Теоретическое мат. ожидание
     */
    public static double getMathematicalExpectation(RandomProcess proc){
        var mathExp = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            mathExp = (proc.getDistributionLaw().getLeft() + proc.getDistributionLaw().getRight()) / 2;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            mathExp = proc.getDistributionLaw().getExpectedValue();
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            mathExp = 1. / proc.getDistributionLaw().getIntensity();
        }

        return mathExp;
    }

    /**
     * Расчет теоретической дисперсии
     * @param proc Параметры СП
     * @return Теоретическая дисперсия
     */
    public static double getTheoreticalDispertion(RandomProcess proc){
        var disp = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            disp = Math.pow(proc.getDistributionLaw().getRight() - proc.getDistributionLaw().getLeft(), 2) / 12;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            disp = proc.getDistributionLaw().getDispersion();
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            disp = 1. / Math.pow(proc.getDistributionLaw().getIntensity(),2);
        }

        return disp;
    }

    /**
     * Расчет теоретического коэффициента асимметрии
     * @param proc Параметры СП
     * @return Теоретический коэффициент асимметрии
     */
    public static double getTheoreticalAsymmetryCoefficient(RandomProcess proc){
        var asymCoef = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            asymCoef = 0;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            asymCoef = 0;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            asymCoef = 2;
        }

        return asymCoef;
    }

    /**
     * Расчет теоретического коэффициента эксцесса
     * @param proc Параметры СП
     * @return Теоретический коэффициент эксцесса
     */
    public static double getTheoreticalKurtosisCoefficient(RandomProcess proc){
        var mathExp = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            mathExp = -1.2;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            mathExp = 0;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            mathExp = 6;
        }

        return mathExp;
    }

    /**
     * Расчет выборочного среднего
     * @param proc Случайный процесс
     * @return Выборочное среднее
     */
    public static double getMean(double[] proc){
        return getInitialMoment(proc,1);
    }

    /**
     * Расчет выборочной несмещенной дисперсии
     * @param proc Случайный процесс
     * @return Выборочная несмещенная дисперсия
     */
    public static double getExperimentalDispertion(double[] proc){
        var n = proc.length;

        return ((double) n / (n - 1))*getCentralMoment(proc,2);
    }

    /**
     * Расчет выборочного несмещенного коэффициента асимметрии
     * @param proc Случайный процесс
     * @return Выборочный несмещенный коэффициент асимметрии
     */
    public static double getExperimentalAsymmetryCoefficient(double[] proc){
        var n = proc.length;

        return (Math.sqrt(n*(n-1))/(n-2))*getCentralMoment(proc,3)
                /Math.pow(getCentralMoment(proc,2),1.5);
    }

    /**
     * Расчет выборочного несмещенного коэффициента эксцесса
     * @param proc Случайный процесс
     * @return Выборочный несмещенный коэффициент эксцесса
     */
    public static double getExperimentalKurtosisCoefficient(double[] proc){
        var n = proc.length;

        return (Math.pow(n,2)-1)/((n-2)*(n-3))
                *(getCentralMoment(proc,4)/Math.pow(getCentralMoment(proc,2),2)
                - 3 + 6./(n+1));
    }

    /**
     * Расчет количества столбцов гистограммы по формуле Стерджесса
     * @param numOfSamples Количество отсчетов
     * @return Количество столбцов гистограммы
     */
    public static int getNumOfBins(int numOfSamples){
        return (int)Math.round(1 + 3.322*Math.log10(numOfSamples));
    }

    /**
     * Расчет количества отсчетов для графика функции распределения
     * @param numOfSamples Количество отсчетов
     * @return Количество отсчетов для графика функции распределения
     */
    public static int getNumOfDistFunctionSamples(int numOfSamples){
        return (int)Math.round(Math.exp(1)*Math.log(numOfSamples));
    }

    /**
     * Расчет количества отсчетов для графика КФ
     * @param corrFunc Параметры КФ
     * @return Количество отсчетов для графика КФ
     */
    public static int getNumOfCorrFunctionSamples(CorrelationFunction corrFunc){
        return 0; //TODO: Найти формулы для количества отсчетов, необходимого для построения корр функции
    }

    /**
     * Тест Колмогорова-Смирнова
     * @param procSample Выборка точек из СП
     * @param realFunction Теоретическая функция (ФР или КФ)
     * @param checkParams Параметры для теста
     * @param typeOfGenerating Тип генерации СП (0 - ФР, 1 - КФ)
     * @return Пройден \ Не пройден тест
     */
    public static boolean KSTest(double[] procSample, Function2D realFunction, HypothesisCheck checkParams, int typeOfGenerating){
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


        var testValue = KSTestValue(subsample, realFunction);
        var critValue = table.getCritivalValue(
                checkParams.getNumberOfDegreesOfFreedom(),
                checkParams.getSignificanceLevel()
        );

        return testValue <= critValue;
    }

    /**
     * Тест Хи-квадрат (неприменим для случая КФ)
     * @param procSample СП
     * @param realFunction Теоретическая функция
     * @param checkParams Параметры для теста
     * @return Пройден \ не пройден тест
     */
    public static boolean Chi2Test(double[] procSample, Function2D realFunction, HypothesisCheck checkParams){
        var table = new PearsonTable();

        var testValue = chi2TestValue(procSample, realFunction, checkParams.getNumberOfDegreesOfFreedom());
        var critValue = table.getCritivalValue(
                checkParams.getNumberOfDegreesOfFreedom(),
                checkParams.getSignificanceLevel()
        );

        return testValue <= critValue;
    }

    /**
     * Подготовка набора данных для визуализации эмпирической функции распределения
     * @param proc СП
     * @param lenOfSeries Количество точек графика
     * @return Набор данных для визуализации эмпирической функции распределения
     */
    public static XYSeriesCollection getDistributionFunctionData(double[] proc, int lenOfSeries){
        Arrays.sort(proc);

        var shift = proc.length / (lenOfSeries - 1);

        var series = new XYSeries("dist function");
        for (var i = 0; i < proc.length; i+=shift){
            series.add(proc[i],(double) i/proc.length);
        }
        series.add(proc[proc.length-1],1);

        return new XYSeriesCollection(series);
    }

    /**
     * Подготовка набора данных для визуализации гистограммы распределения
     * @param proc СП
     * @param numOfBins Количество столбцов гистограммы
     * @return Набор данных для визуализации гистограммы распределения
     */
    public static HistogramDataset getDensityFunctionData(double[] proc, int numOfBins){
        var hist = new HistogramDataset();
        hist.addSeries("density", proc, numOfBins);
        hist.setType(HistogramType.SCALE_AREA_TO_1);

        return hist;
    }

    /**
     * Подготовка набора данных для визуализации эмпирической корреляционной функции
     * @param proc СП
     * @param lenOfSeries Количество точек графика
     * @return Набор данных для визуализации эмпирической корреляционной функции
     */
    public static XYSeriesCollection getCorrelationFunctionData(double[] proc, int lenOfSeries){
        var series = new XYSeries("corr function");
        var shift = proc.length / (lenOfSeries - 1);

        for (var i = 0; i < proc.length; i+=shift){
            var ls = getLeftShiftedArray(proc, i);
            var rs = getRightShiftedArray(proc, i);
            series.add(i, getCorrelation(ls, rs));
        }
        series.add(proc.length - 1, 0);

        return new XYSeriesCollection(series);
    }

    /**
     * Получение обрезанного справа СП
     * @param proc СП
     * @param shift Сдвиг
     * @return Обрезанный СП
     */
    private static double[] getRightShiftedArray(double[] proc, int shift){
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
    private static double[] getLeftShiftedArray(double[] proc, int shift){
        var shiftedArray = new double[proc.length - shift];

        for (var i = shift - 1; i<proc.length; i++){
            shiftedArray[i - shift - 1] = proc[i];
        }

        return shiftedArray;
    }

    /**
     * Расчет автокорреляции СП в заданной точке
     * @param originalProcess Обрезанный СП
     * @param shiftedProcess Сдвинутый СП
     * @return Коэффициент автокорреляции
     */
    private static double getCorrelation(double[] originalProcess, double[] shiftedProcess){
        var originalMean = getMean(originalProcess);
        var shiftedMean = getMean(shiftedProcess);
        var len = originalProcess.length;

        var sumOfShifts = 0.;
        var originalSumOfSquaredDeviations = 0.;
        var shiftedSumOfSquaredDeviations = 0.;

        for (var i = 0; i < len; i++){
            sumOfShifts += (originalProcess[i] - originalMean)*(shiftedProcess[i] - shiftedMean);
            originalSumOfSquaredDeviations += Math.pow((originalProcess[i] - originalMean),2);
            shiftedSumOfSquaredDeviations += Math.pow((shiftedProcess[i] - shiftedMean), 2);
        }

        return sumOfShifts / Math.sqrt(originalSumOfSquaredDeviations) / Math.sqrt(shiftedSumOfSquaredDeviations);
    }

    /**
     * Вычисление статистики для критерия Колмогорова-Смирнова
     * @param procSample Выборка точек из СП
     * @param realFunction Теоретическая функция
     * @return Значение статистики
     */
    private static double KSTestValue(XYSeries procSample, Function2D realFunction){
        var len = procSample.getItemCount();
        var d = new double[len];

        for (var i = 0; i < len; i++){
            d[i] = Math.max(
                    (double) procSample.getY(i) - realFunction.getValue((double) procSample.getX(i)),
                    realFunction.getValue((double) procSample.getX(i)) - (double) procSample.getY(i)
            );
        }

        return StatUtils.max(d);
    }

    /**
     * Вычисление статистики для критерия Хи-квадрат
     * @param proc СП
     * @param realFunction Теоретическая функция
     * @param freedomDegrees Число степеней сводобы
     * @return Значение статистики
     */
    private static double chi2TestValue(double[] proc, Function2D realFunction, int freedomDegrees){
        var max = StatUtils.max(proc);
        var min = StatUtils.min(proc);
        Arrays.sort(proc);
        var shift = (max - min) / (freedomDegrees + 1);
        var statistic = 0.;

        while (min <= max - shift){
            var expDensity = experimentalDensity(proc, min, min +shift);
            var realDensity = realFunction.getValue(min +shift) - realFunction.getValue(min);
            if (min == max-shift){
                expDensity++;
            }
            statistic += Math.pow(expDensity - realDensity, 2)/ realDensity;
            min += shift;
        }

        return statistic * (freedomDegrees + 2);
    }

    /**
     * Вычисление экспериментальной плотности
     * @param proc СП
     * @param min Левая граница интервала
     * @param max Правая граница интервала
     * @return Экспериментальная плотность на интервале
     */
    private static double experimentalDensity(double[] proc, double min, double max){
        var counter = 0;
        for (double v : proc) {
            if (v < min) {
                continue;
            }
            if (v >= max) {
                break;
            }
            if (v >= min) {
                counter++;
            }
        }

        return (double) counter / proc.length;
    }

    /**
     * Генерация равномерного на [0;1) распределения
     * @param numOfSamples Количество отсчетов
     * @return Массив чисел, равномерно распределенных на [0;1)
     */
    private static double[] generateUniformDistribution(int numOfSamples){
        var rnd = new Random();
        return rnd.doubles(numOfSamples).toArray();
    }

    /**
     * Получение равномерного на [a;b] распределения
     * @param numOfSamples Количество отсчетов
     * @param left Левая граница равномерного распределения
     * @param right Правая граница равномерного распределения
     * @return Массив чисел, равномерно распределенных на [a;b]
     */
    private static double[] getUniformDistribution(int numOfSamples, double left, double right){
        var sample = generateUniformDistribution(numOfSamples);
        for (var i = 0; i < numOfSamples; i++) {
            sample[i] = (right-left)*sample[i] + left;
        }
        return sample;
    }

    /**
     * Получение экспоненциального распределения с заданной интенсивностью
     * @param numOfSamples Количество отсчетов
     * @param intensity Интенсивность
     * @return Массив чисел, имеющих экспоненциальное распределение
     */
    private static double[] getExponentialDistribution(int numOfSamples, double intensity){
        var sample = generateUniformDistribution(numOfSamples);
        for (var i = 0; i < numOfSamples; i++) {
            sample[i] = -(1./intensity)*Math.log(1-sample[i]);
        }
        return sample;
    }

    /**
     * Получение нормального распределения с заданным средним и дисперсией
     * @param numOfSamples Количество отсчетов
     * @param dispersion Дисперсия
     * @param mean Среднее
     * @return Массив чисел, имеющих нормальное распределение
     */
    private static double[] getNormalDistribution(int numOfSamples, double dispersion, double mean){
        var sample = getStandardDistribution(numOfSamples, 120);
        for (var i = 0; i < numOfSamples; i++) {
            sample[i] = sample[i]*Math.sqrt(dispersion)+mean;
        }

        return sample;
    }

    /**
     * Получение стандартного распределения
     * @param numOfSamples Количество отсчетов
     * @param count Размерность сумм
     * @return Массив чисел, имеющих стандартное распределение
     */
    private static double[] getStandardDistribution(int numOfSamples, int count){
        var sample = generateUniformDistribution(numOfSamples*count);
        var standard = new double[numOfSamples];
        for (var i = 0; i < numOfSamples; i++) {
            for (var j = 0; j < count; j++){
                standard[i]+=sample[count*i+j];
            }
        }

        var mean = (double)count/2;
        var std = Math.sqrt(count/12.);

        for (var i = 0; i < numOfSamples; i++) {
            standard[i] = (standard[i] - mean) / std;
        }
        return standard;
    }

    /**
     * Получение СП с монотонной КФ
     * @param numOfSamples Количество отсчетов
     * @param attenuationRate Показатель затухания
     * @param delta Шаг по временной шкале
     * @return Массив чисел с заданной монотонной КФ
     */
    private static double[] getMonotonousCorrFunction(int numOfSamples, double attenuationRate, double delta){
        var sample = new double[numOfSamples];
        var rnd = new Random();

        var gamma = attenuationRate * delta;
        var p = Math.exp(-gamma);

        var b = Math.exp(-gamma);
        var a = Math.sqrt(1-Math.pow(p,2));

        sample[0] = a*rnd.nextDouble();

        for (var i = 1; i < numOfSamples; i++) {
            sample[i] = a*rnd.nextDouble() + b*sample[i-1];
        }

        return sample;
    }

    /**
     * Получение СП с колебательной КФ
     * @param numOfSamples Количество отсчетов
     * @param attenuationRate Показатель затухания
     * @param oscillationFrequency Частота колебаний
     * @param delta Шаг по временной шкале
     * @return Массив чисел с заданной колебательной КФ
     */
    private static double[] getOscillatoryCorrFunction(int numOfSamples, double attenuationRate, double oscillationFrequency, double delta){
        var sample = new double[numOfSamples];
        var rnd = new Random();
        double x_prev, x_curr;

        var gamma = attenuationRate * delta;
        var p = Math.exp(-gamma);
        var gamma_0 = oscillationFrequency * delta;
        var alpha_0 = p*(Math.pow(p,2)-1)*Math.cos(gamma_0);
        var alpha_1 = 1 - Math.pow(p,4);

        var sub =  Math.sqrt(Math.pow(alpha_1,2)-4*Math.pow(alpha_0,2));
        var sgn = (Math.pow(alpha_1,2) > sub)?1:-1;

        var a_0 = Math.sqrt((Math.pow(alpha_1,2) + sgn*sub)/2);
        var a_1 = alpha_0/alpha_1;
        var b_1 = 2*p*Math.cos(gamma_0);
        var b_2 = -Math.pow(p,2);

        x_prev = rnd.nextDouble();
        sample[0] = x_prev = a_0*x_prev;
        x_curr = rnd.nextDouble();
        sample[1] = a_0*x_curr + x_prev*a_1 + b_1*sample[0];
        x_prev = x_curr;

        for (var i = 2; i < numOfSamples; i++) {
            x_curr = rnd.nextDouble();
            sample[i] = x_curr*a_0 + x_prev*a_1 + b_1*sample[i-1] + b_2*sample[i-2];
            x_prev = x_curr;
        }

        return sample;
    }

    /**
     * Расчет k-го начального выборочного момента
     * @param proc Случайный процесс
     * @param k Порядок момента
     * @return k-й начальный выборочный момент
     */
    private static double getInitialMoment(double[] proc, int k){
        var sum = 0.;
        for (var num:proc) {
            sum+=Math.pow(num,k);
        }

        return sum/proc.length;
    }

    /**
     * Расчет k-го центрального выборочного момента
     * @param proc Случайный процесс
     * @param k Порядок момента
     * @return k-й центральный выборочный момент
     */
    private static double getCentralMoment(double[] proc, int k){
        var sum = 0.;
        var mean = getInitialMoment(proc,1);

        for (var num:proc) {
            sum+=Math.pow((num-mean),k);
        }

        return sum/proc.length;
    }
}
