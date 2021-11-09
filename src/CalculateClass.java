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
                sample = getExponentialDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getIntensity()
                );
            }
            if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательноее")){
                sample = getUniformDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getLeft(),
                        proc.getDistributionLaw().getRight()
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
