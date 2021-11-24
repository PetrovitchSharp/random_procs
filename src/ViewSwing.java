import charts.ChartsFactory;
import distributionFunctions.ExponentialDistributionFunction;
import distributionFunctions.GaussianDistributionFunction;
import distributionFunctions.UniformDistibutionFunction;
import math.Calculations;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.function.Function2D;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import params.CorrelationFunction;
import params.DistributionLaw;
import params.HypothesisCheck;
import params.RandomProcess;
import statTests.tests.Chi2Test;
import statTests.tests.KSTest;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ViewSwing {

    ViewSwing(){
        begin();
    }

    /** Метод создания форм с вводом данных*/
    private void begin(){
        jFrame = new JFrame("Генерация случайного процесса");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(290, height));

        jPanel = new JPanel();

        getJpDataInput();
        getJpDataOutput();

        jPanel.add(jpDataInput,FlowLayout.LEFT);
        jPanel.add(jpDataOutput);

        jFrame.add(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    /** Метод создания панели для ввода данных*/
    private void getJpDataInput(){
        jpDataInput = new JPanel(new VerticalLayout());
        jpDataInput.setPreferredSize(new Dimension(290, height));
        jpDataInput.setBackground(color);
        jpDataInput.setBorder(BorderFactory.createTitledBorder(etched, "Параметры случайного процесса"));

        jpDataInput.add(new JLabel("<html> <br>количество отсчетов</html>"));
        numberOfSamples = new JTextField(26);
        numberOfSamples.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!numberOfSamples.getText().isEmpty() && !CheckParameters.isNumberOfSamples(numberOfSamples.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_NUMBER_OF_SAMPLES);
                    numberOfSamples.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpDataInput.add(numberOfSamples);

        //вызов методов для создания панелей для ввода параметров кф/закона распределения
        getJpCorrelationFunction();
        getJpDistributionLaw();

        jpDataInput.add(new JLabel("способ генерации случайных величин"));
        String[] sWaysOfGeneration = {"корреляционная функция", "с заданным законном распределения"};
        rbCorrelationFunction = new JRadioButton(sWaysOfGeneration[0],true);
        rbDistributionLaw = new JRadioButton(sWaysOfGeneration[1],false);
        rbDistributionLaw.setBackground(color);
        rbCorrelationFunction.setBackground(color);
        ButtonGroup waysOfGeneration = new ButtonGroup();
        waysOfGeneration.add(rbCorrelationFunction);
        waysOfGeneration.add(rbDistributionLaw);

        //слушатель для выбора кф
        rbCorrelationFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCF = false; generateDL = false;
                if (rbCorrelationFunction.isSelected()) {
                    jpCorrelationFunction.setPreferredSize(jpDistributionLaw.getPreferredSize());
                    jpCorrelationFunction.setVisible(true);
                    jpDistributionLaw.setVisible(false);
                    jpDistributionLaw.setPreferredSize(new Dimension(0,0));
                    significanceCriterion.setModel(new DefaultComboBoxModel(new String[]{"критерий согласия Колмогорова"}));
                }
            }
        });
        //слушатель для выбора закона распределения
        rbDistributionLaw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCF = false; generateDL = false;
                if (rbDistributionLaw.isSelected()) {
                    jpDistributionLaw.setPreferredSize(jpCorrelationFunction.getPreferredSize());
                    jpDistributionLaw.setVisible(true);
                    jpCorrelationFunction.setVisible(false);
                    jpCorrelationFunction.setPreferredSize(new Dimension(0,0));
                    significanceCriterion.setModel(new DefaultComboBoxModel(new String[]{"критерий согласия Пирсона", "критерий согласия Колмогорова"}));
                }
            }
        });

        jpDataInput.add(rbCorrelationFunction);
        jpDataInput.add(rbDistributionLaw);
        jpDataInput.add(jpCorrelationFunction);
        jpDataInput.add(jpDistributionLaw);

        //вызов метода для создания панели проверки гипотезы
        getJpCheck();
        jpDataInput.add(jpCheck);

        JButton result = new JButton("Расчитать характеристики");
        result.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!generateCF && !generateDL)
                        throw  new Exception(ExceptionMessage.EXCEPTION_NOT_GENERATE_RP);
                    clean();
                    getTable();
                    jFrame.setSize(new Dimension(1010,700));
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,exception.getMessage());
                }
            }
        });
        jpDataInput.add(result);
    }

    /** Метод создания панели для проверки гипотезы*/
    private void getJpCheck(){
        jpCheck = new JPanel(new VerticalLayout());
        jpCheck.setPreferredSize(new Dimension(250, 210));
        jpCheck.setBorder(BorderFactory.createTitledBorder(etched, "Проверка гипотезы"));
        jpCheck.add(new JLabel("<html> <br>критерий значимости </html>"));

        significanceCriterion = new JComboBox(new String[]{"критерий согласия Колмогорова"});
        significanceCriterion.setPreferredSize(dimensionForJCombobox);
        significanceCriterion.setEditable(true);
        jpCheck.add(significanceCriterion);

        jpCheck.add(new JLabel("уровень значимости"));
        String[] sSignificanceLevel = {"0.001", "0.01", "0.025", "0.05", "0.1", "0.2"};
        JComboBox significanceLevel = new JComboBox(sSignificanceLevel);
        significanceLevel.setPreferredSize(dimensionForJCombobox);
        significanceLevel.setEditable(true);
        jpCheck.add(significanceLevel);

        jpCheck.add(new JLabel("количество степеней свободы"));
        JTextField numberOfDegreesOfFreedom = new JTextField(23);
        numberOfDegreesOfFreedom.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!numberOfDegreesOfFreedom.getText().isEmpty()
                        && !CheckParameters.isNumberOfDegreesOfFreedom(numberOfDegreesOfFreedom.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_NUMBER_OF_DEGREES_OF_FREEDOM);
                    numberOfDegreesOfFreedom.setText("");
                }
            }
        });
        jpCheck.add(numberOfDegreesOfFreedom);

        JButton check = new JButton("Проверить гипотезу");
        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if (numberOfDegreesOfFreedom.getText() == null)
                        throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                    if (!generateCF && !generateDL)
                        throw  new Exception(ExceptionMessage.EXCEPTION_NOT_GENERATE_RP);

                    hypothesisCheck = new HypothesisCheck();
                    hypothesisCheck.setNumberOfDegreesOfFreedom(Integer.parseInt(numberOfDegreesOfFreedom.getText()));
                    hypothesisCheck.setSignificanceCriterion(significanceCriterion.getSelectedItem().toString());
                    hypothesisCheck.setSignificanceLevel(Double.parseDouble(significanceLevel.getSelectedItem().toString()));

                    boolean check = false;
                    if (randomProcess.getWaysOfGeneration().equals("с заданным законном распределения")) {
                        Function2D distributionFunction;
                        if (randomProcess.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")) {
                            System.out.println("**1");
                            distributionFunction = new UniformDistibutionFunction(randomProcess.getDistributionLaw().getLeft(), randomProcess.getDistributionLaw().getRight());
                        } else if (randomProcess.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
                            System.out.println("**2");
                            distributionFunction = new GaussianDistributionFunction(randomProcess.getDistributionLaw().getExpectedValue(), randomProcess.getDistributionLaw().getDispersion());
                        } else{System.out.println("**3");
                            distributionFunction = new ExponentialDistributionFunction(randomProcess.getDistributionLaw().getIntensity());
                        }
                        if (hypothesisCheck.getSignificanceCriterion().equals("критерий согласия Колмогорова")){
                            System.out.println("***1");
                            check = KSTest.ksTest(proc,distributionFunction,
                                    hypothesisCheck,
                                    0,
                                    randomProcess);
                        } else{
                            System.out.println("***2");
                            check = Chi2Test.chi2Test(proc, distributionFunction, hypothesisCheck);
                        }
                    } else if (hypothesisCheck.getSignificanceCriterion().equals("критерий согласия Колмогорова")){
                        if (randomProcess.getCorrelationFunction().getKindCorrelationFunction().equals("Монотонная")) {
                            System.out.println("*1");
                            ExponentialDistributionFunction distributionFunction = new ExponentialDistributionFunction(randomProcess.getCorrelationFunction().getAttenuationRates());
                            check = KSTest.ksTest(proc,distributionFunction,
                                    hypothesisCheck,
                                    1,
                                    randomProcess);
                        }else{
                            System.out.println("*2");
                            UniformDistibutionFunction distributionFunction = new UniformDistibutionFunction(randomProcess.getCorrelationFunction().getAttenuationRates(),randomProcess.getCorrelationFunction().getOscillationFrequencyValue());
                            check = KSTest.ksTest(proc, distributionFunction,
                                    hypothesisCheck,
                                    1,
                                    randomProcess);;
                        }

                    }
                    if (check){
                        JOptionPane.showMessageDialog(null,"Гипотеза прошла проверку");
                    } else{
                        JOptionPane.showMessageDialog(null,"Гипотеза не прошла проверку");
                    }
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,"Ошибка: " + exception.getMessage());
                }
            }
        });

        jpCheck.add(check);
    }

    /** Метод создания панели корреляционной функции*/
    private void getJpCorrelationFunction(){
        jpCorrelationFunction = new JPanel(new VerticalLayout());
        jpCorrelationFunction.setPreferredSize(new Dimension(250,210));
        jpCorrelationFunction.setBorder(BorderFactory.createTitledBorder(etched, "Параметры корреляционной функции"));

        jpCorrelationFunction.add(new JLabel("<html> <br> вид корреляционной функции</html>"));
        String[] sKindCorrelationFunction = { "Монотонная","Колебательная"};
        JComboBox kindCorrelationFunction = new JComboBox(sKindCorrelationFunction);
        kindCorrelationFunction.setPreferredSize(dimensionForJCombobox);
        kindCorrelationFunction.setEditable(true);

        //создание панели для монотонной кф
        JPanel jpMonotone = new JPanel(new VerticalLayout());
        jpMonotone.add(new JLabel("значение частоты колебания"));
        JTextField oscillationFrequencyValue  = new JTextField(20);
        oscillationFrequencyValue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!oscillationFrequencyValue.getText().isEmpty()
                        && !CheckParameters.isNumberFrom0_1to10(oscillationFrequencyValue.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_OSCILLATION_FREQUENCY_VALUE);
                    oscillationFrequencyValue.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpMonotone.add(oscillationFrequencyValue);
        jpMonotone.setVisible(false);

        kindCorrelationFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (kindCorrelationFunction.getSelectedItem().toString().equals(sKindCorrelationFunction[1])){
                    jpMonotone.setVisible(true);
                    jpMonotone.setPreferredSize(new Dimension(jpCorrelationFunction.getWidth(),50));
                } else{
                    jpMonotone.setVisible(false);
                    jpMonotone.setPreferredSize(new Dimension(0,0));
                }
                generateCF = false; generateDL = false;
            }
        });
        jpCorrelationFunction.add(kindCorrelationFunction);

        jpCorrelationFunction.add(new JLabel("показатель затухания"));
        JTextField attenuation_rates = new JTextField(20);
        attenuation_rates.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!attenuation_rates.getText().isEmpty()
                        && !CheckParameters.isNumberFrom0_1to10(attenuation_rates.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_ATTENUATION_RATES);
                    attenuation_rates.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpCorrelationFunction.add(attenuation_rates);
        jpCorrelationFunction.add(jpMonotone);

        JButton generation = new JButton("Сгенерировать");
        generation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (attenuation_rates.getText().isEmpty()
                            || (kindCorrelationFunction.getSelectedItem().toString().equals(sKindCorrelationFunction[1])
                            && oscillationFrequencyValue.getText().isEmpty())
                            || numberOfSamples.getText().isEmpty())
                        throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                    correlationFunction = new CorrelationFunction();
                    correlationFunction.setKindCorrelationFunction(kindCorrelationFunction.getSelectedItem().toString());
                    correlationFunction.setAttenuationRates(Double.parseDouble(attenuation_rates.getText()));
                    correlationFunction.setOscillationFrequencyValue(!oscillationFrequencyValue.getText().isEmpty()?Double.parseDouble(oscillationFrequencyValue.getText()):null);

                    randomProcess.setCorrelationFunction(correlationFunction);
                    randomProcess.setWaysOfGeneration("корреляционная функция");
                    randomProcess.setNumberOfSamples(numberOfSamples.getText().isEmpty() ? null : Integer.parseInt(numberOfSamples.getText()));

                    proc = RandomProcGenerator.generateRandomProc(randomProcess);
                    JOptionPane.showMessageDialog(null,"СП успешно сгенерирован!");
                    generateCF = true;
                    generateDL = false;
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,"CП не сгенерирован.\nОшибка:\n" + exception.getMessage());
                }
            }
        });
        jpCorrelationFunction.add(generation);
        jpCorrelationFunction.setVisible(true);
    }

    /** Метод создания панели закона распределения*/
    private void getJpDistributionLaw(){
        jpDistributionLaw = new JPanel(new VerticalLayout());
        jpDistributionLaw.setBorder(BorderFactory.createTitledBorder(etched, "Параметры для закона распределения"));
        jpDistributionLaw.add(new JLabel("<html> <br>вид распределения </html>"));
        String[] sKindDistributionLaw = {"Нормальное", "Равномерное","Показательное"};
        JComboBox kindDistributionLaw = new JComboBox(sKindDistributionLaw);
        kindDistributionLaw.setPreferredSize(dimensionForJCombobox);
        kindDistributionLaw.setEditable(true);
        jpDistributionLaw.add(kindDistributionLaw);

        JPanel jpNormal = getJPNormal();
        jpNormal.setVisible(true);

        JPanel jpUniform = getJPUniform();
        jpUniform.setVisible(false);

        JPanel jpExponential = getJPExponential();
        jpExponential.setVisible(false);

        kindDistributionLaw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[0])){
                    setVisibleAndHiddenPanels(jpExponential,jpUniform,jpNormal);
                } else if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[1])){
                    setVisibleAndHiddenPanels(jpNormal,jpExponential,jpUniform);
                } else{
                    setVisibleAndHiddenPanels(jpNormal,jpUniform, jpExponential);
                }
                generateCF = false; generateDL = false;
            }
        });
        jpDistributionLaw.add(jpNormal);
        jpDistributionLaw.add(jpUniform);
        jpDistributionLaw.add(jpExponential);

        JButton generation = new JButton("Сгенерировать");
        generation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (numberOfSamples.getText().isEmpty())
                        throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                    distributionLaw = new DistributionLaw();
                    distributionLaw.setKindDistributionLaw(kindDistributionLaw.getSelectedItem().toString());

                    if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[0])){

                        if (((JTextField)jpNormal.getComponent(1)).getText().isEmpty()
                                && ((JTextField)jpNormal.getComponent(3)).getText().isEmpty())
                            throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                        distributionLaw.setExpectedValue(Double.parseDouble(((JTextField)jpNormal.getComponent(1)).getText()));
                        distributionLaw.setDispersion(Double.parseDouble(((JTextField)jpNormal.getComponent(3)).getText()));
                    } else if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[1])){

                        if (((JTextField)jpUniform.getComponent(1)).getText().isEmpty()
                                || ((JTextField)jpUniform.getComponent(3)).getText().isEmpty())
                            throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                        if (Double.parseDouble(((JTextField)jpUniform.getComponent(1)).getText())
                                < Double.parseDouble(((JTextField)jpUniform.getComponent(3)).getText()))
                            throw new Exception(ExceptionMessage.EXCEPTION_LEFT_IS_MORE_RIGHT);

                        distributionLaw.setRight(Double.parseDouble(((JTextField)jpUniform.getComponent(1)).getText()));
                        distributionLaw.setLeft(Double.parseDouble(((JTextField)jpUniform.getComponent(3)).getText()));
                    } else{

                        if (((JTextField)jpExponential.getComponent(1)).getText().isEmpty())
                            throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                        distributionLaw.setIntensity(Double.parseDouble(((JTextField)jpExponential.getComponent(1)).getText()));
                    }

                    randomProcess.setDistributionLaw(distributionLaw);
                    randomProcess.setWaysOfGeneration("с заданным законном распределения");
                    randomProcess.setNumberOfSamples(numberOfSamples.getText().isEmpty() ? null : Integer.parseInt(numberOfSamples.getText()));

                    proc = RandomProcGenerator.generateRandomProc(randomProcess);
                    JOptionPane.showMessageDialog(null,"СП успешно сгенерирован!");
                    generateCF = false;
                    generateDL = true;
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,"CП не сгенерирован.\nОшибка:\n" + exception.getMessage());
                }
            }
        });

        jpDistributionLaw.add(generation);
        jpDistributionLaw.setVisible(false);
        jpDistributionLaw.setPreferredSize(new Dimension(0,0));
    }

    /** Метод для скрытия и показа панелей
     * @param hiddenPanel1 - скрытая панель
     * @param hiddenPanel2 - скрытая панель
     * @param visiblePanel - видимая панель
     **/
    private static void setVisibleAndHiddenPanels(JPanel hiddenPanel1, JPanel hiddenPanel2, JPanel visiblePanel){
        hiddenPanel1.setVisible(false);
        hiddenPanel1.setPreferredSize(new Dimension(0,0));

        hiddenPanel2.setVisible(false);
        hiddenPanel2.setPreferredSize(new Dimension(0,0));

        visiblePanel.setVisible(true);
        visiblePanel.setPreferredSize(new Dimension(400,90));
    }

    /** Метод создания панели нормального распределения
     * @return панель для нормального закона распределения*/
    private JPanel getJPNormal(){
        JPanel jpNormal = new JPanel(new VerticalLayout());

        jpNormal.add(new JLabel("математическое ожидание"));
        JTextField expected_value = new JTextField(20);
        expected_value.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!expected_value.getText().isEmpty() && !CheckParameters.isNumberFrom_100to100(expected_value.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_EXPECTED_VALUE);
                    expected_value.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpNormal.add(expected_value);

        jpNormal.add(new JLabel("дисперсия"));
        JTextField dispersion = new JTextField(20);
        dispersion.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!dispersion.getText().isEmpty() && !CheckParameters.isNumberFrom0_1to10(dispersion.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_DISPERSION);
                    dispersion.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpNormal.setPreferredSize(new Dimension(400,90));
        jpNormal.add(dispersion);
        return jpNormal;
    }

    /** Метод создания панели равномерного распределения
     * @return панель для равномерного закона распределения*/
    private JPanel getJPUniform(){
        JPanel jpUniform = new JPanel(new VerticalLayout());

        jpUniform.add(new JLabel("правая граница"));
        JTextField right = new JTextField(20);
        right.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!right.getText().isEmpty() && !CheckParameters.isNumberFrom_100to100(right.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_RIGHT);
                    right.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpUniform.add(right);

        jpUniform.add(new JLabel("левая граница"));
        JTextField left = new JTextField(20);
        left.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!left.getText().isEmpty() && !CheckParameters.isNumberFrom_100to100(left.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_LEFT);
                    left.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpUniform.add(left);
        jpUniform.setPreferredSize(new Dimension(0,0));
        return jpUniform;
    }

    /** Метод создания панели показательного распределения
     * @return панель для показательного закона распределения*/
    private JPanel getJPExponential(){
        JPanel jpExponential = new JPanel(new VerticalLayout());

        jpExponential.add(new JLabel("интенсивность"));
        JTextField intensity = new JTextField(20);
        intensity.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!intensity.getText().isEmpty() && !CheckParameters.isIntensity(intensity.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_INTENSITY);
                    intensity.setText("");
                }
                generateCF = false; generateDL = false;
            }
        });
        jpExponential.add(intensity);
        jpExponential.setPreferredSize(new Dimension(0,0));
        return jpExponential;
    }

    /** Метод создания панели для вывода данных*/
    private void getJpDataOutput(){
        jpDataOutput = new JPanel();
        jpDataOutput.setPreferredSize(new Dimension(width - 290, 660));

        GridLayout gridLayout = new GridLayout(2,2,5,15);
        jpDataOutput.setLayout(gridLayout);

        getJpNumeralCharacteristics();
        getJpOutput();

        jpDataOutput.add(jpNumericalCharacteristics);
        jpDataOutput.add(jpDistributionFunction);
        jpDataOutput.add(jpProbabilityDensity);
        jpDataOutput.add(jpCorrelationFunctionOutput);
        jpDataOutput.setVisible(false);
    }

    /** Метод создания панели c таблицей*/
    private void getJpNumeralCharacteristics(){
        jpNumericalCharacteristics = new JPanel();
        jpNumericalCharacteristics.setBorder(BorderFactory.createTitledBorder(etched, "Числовые характеристики"));

        DefaultTableModel model = new DefaultTableModel();

        jtNumericalCharacteristic = new JTable(model);
        jtNumericalCharacteristic.setFillsViewportHeight(true);

        //Создаем панель прокрутки и включаем в ее состав нашу таблицу
        JScrollPane jscrlp = new JScrollPane(jtNumericalCharacteristic);
        jscrlp.setPreferredSize(new Dimension(300,100));

        jpNumericalCharacteristics.add(jscrlp);
        jpNumericalCharacteristics.setBackground(color);
    }

    private void getJpOutput(){
        jpDistributionFunction = new JPanel();
        jpDistributionFunction.setBorder(BorderFactory.createTitledBorder(etched));
        jpDistributionFunction.setBackground(color);

        jpProbabilityDensity = new JPanel();
        jpProbabilityDensity.setBorder(BorderFactory.createTitledBorder(etched));
        jpProbabilityDensity.setBackground(color);

        jpCorrelationFunctionOutput = new JPanel();
        jpCorrelationFunctionOutput.setBorder(BorderFactory.createTitledBorder(etched));
        jpCorrelationFunctionOutput.setBackground(color);

    }

    /** Метод создания панели для получения таблицы*/
    private void getTable(){

        Object[][] data;
        Object[] headers;
        if (randomProcess.getWaysOfGeneration().equals("с заданным законном распределения")){
            headers = new String[]{"Момент", "Теоретическое значение", "Эмпирическое значение"};
            //Массив содержащий информацию для таблицы
            data = new Object[][]{
                    {"1", Calculations.getMathematicalExpectation(randomProcess), Calculations.getMean(proc)},
                    {"2", Calculations.getTheoreticalDispertion(randomProcess), Calculations.getExperimentalDispertion(proc)},
                    {"3", Calculations.getTheoreticalAsymmetryCoefficient(randomProcess), Calculations.getExperimentalAsymmetryCoefficient(proc)},
                    {"4", Calculations.getTheoreticalKurtosisCoefficient(randomProcess), Calculations.getExperimentalKurtosisCoefficient(proc)}
            };
        } else{
            headers = new String[]{"Момент","Эмпирическое значение"};
            data = new Object[][]{
                    {"1", Calculations.getMean(proc)},
                    {"2", Calculations.getExperimentalDispertion(proc)},
                    {"3", Calculations.getExperimentalAsymmetryCoefficient(proc)},
                    {"4", Calculations.getExperimentalKurtosisCoefficient(proc)}
            };
        }

        DefaultTableModel modelNew = (DefaultTableModel)jtNumericalCharacteristic.getModel();
        modelNew.setDataVector(data,headers);

        jTPDistributionFunction = new JTabbedPane();
        jTPDistributionFunction.setBackground(color);
        jTPDistributionFunction.add("эмпирическая", getChartPanel(ChartsFactory.getExperimentalDistributionFunctionChart_1(proc)));
        if (randomProcess.getWaysOfGeneration().equals("с заданным законном распределения")){
            jTPDistributionFunction.add("теоретическая", getLineChartPanel(ChartsFactory.getTheoreticalDistributionFunctionChart(distributionLaw,proc),
                    "Теоретическая функция распределения",
                    "Значение случ. величины",
                    "F(x)"
            ));
        }
        jpDistributionFunction.add(jTPDistributionFunction);

        jTPDensity = new JTabbedPane();
        jTPDensity.setBackground(color);
        jTPDensity.add("эмпирическая", getChartPanel(ChartsFactory.getExperimentalDensityFunctionHistogram(proc)));
        if (randomProcess.getWaysOfGeneration().equals("с заданным законном распределения")){
            jTPDensity.add("теоретическая", getLineChartPanel(ChartsFactory.getTheoreticalDensityFunctionChart(distributionLaw,proc),
                    "Теоретическая плотность распределения",
                    "Значение случ. величины",
                    "Частота"
            ));
        }
        jpProbabilityDensity.add(jTPDensity);

        jTPCorrFunc = new JTabbedPane();
        jTPCorrFunc.setBackground(color);
        if (randomProcess.getWaysOfGeneration().equals("корреляционная функция")){
            jTPCorrFunc.add("эмпирическая", getLineChartPanel(ChartsFactory.getExperimentalCorrelationFunctionChart_1(proc,correlationFunction),
                    "Эмпирическая корреляционная функция",
                    "Лаг КФ",
                    "Коэф. автокорр."
            ));
            jTPCorrFunc.add("теоретическая",getLineChartPanel(ChartsFactory.getTheoreticalCorrelationFunctionChart(proc,correlationFunction),
                    "Теоретическая корреляционная функция",
                    "Лаг КФ",
                    "Коэф. автокорр."
            ));
        } else{
            jpCorrelationFunctionOutput.add(getLineChartPanel(ChartsFactory.getExperimentalCorrelationFunctionChart(proc),
                    "Эмпирическая корреляционная функция",
                    "Лаг КФ",
                    "Коэф. автокорр."
            ));
        }
        jpCorrelationFunctionOutput.add(jTPCorrFunc);

        jpDataOutput.setVisible(true);
    }

    private void clean(){
        jPanel.removeAll();
        jPanel.repaint();
        jPanel.revalidate();

        getJpDataOutput();
        jPanel.add(jpDataInput);
        jPanel.add(jpDataOutput);

        jFrame.setSize(new Dimension(width,height));
        GridLayout gridLayout = new GridLayoutNew(1,2,5,15);
        jPanel.setLayout(gridLayout);
        jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

    }

    private ChartPanel getChartPanel(JFreeChart jFreeChart){
        return new ChartPanel(jFreeChart) {
            public Dimension getPreferredSize() {
                return new Dimension(305, 280);
            }
        };
    }

    private ChartPanel getStepChartPanel(XYSeries xySeries, String tittle, String X, String Y){
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(xySeries);
        return getChartPanel(ChartFactory.createXYStepChart(
                tittle,
                X,
                Y,
                xySeriesCollection
        ));
    }

    private ChartPanel getLineChartPanel(XYSeries xySeries, String tittle, String X, String Y){
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(xySeries);
        return getChartPanel(ChartFactory.createXYLineChart(
                tittle,
                X,
                Y,
                xySeriesCollection
        ));
    }

    public static void main(String[] args) {
        new ViewSwing();
    }

    /**поле основного JFrame*/
    private static JFrame jFrame;
    /**поле основной панели*/
    private static JPanel jPanel;
    /**поле панели ввода данных*/
    private static JPanel jpDataInput;
    /**поле панели вывода данных*/
    private static JPanel jpDataOutput = new JPanel();
    /**поле панели проверки гипотезы*/
    private static JPanel jpCheck;
    /**поле панели корреляционной функции*/
    private static JPanel jpCorrelationFunction;
    /**поле панели закона распределения*/
    private static JPanel jpDistributionLaw;
    private static JPanel jpDistributionFunction;
    private static JPanel jpProbabilityDensity;
    private static JPanel jpCorrelationFunctionOutput;
    private static JTabbedPane jTPDistributionFunction;
    private static JTabbedPane jTPCorrFunc;
    private static JTabbedPane jTPDensity;
    private static JRadioButton rbCorrelationFunction;
    private static JRadioButton rbDistributionLaw;
    private static Boolean generateCF = false;
    private static Boolean generateDL = false;
    private static JTextField numberOfSamples;
    private static JComboBox significanceCriterion;
    /**поле генерации процесса*/
    private static RandomProcess randomProcess = new RandomProcess();
    /**поле корреляционной функции*/
    private static CorrelationFunction correlationFunction;
    /**поле закона распределения*/
    private static DistributionLaw distributionLaw;
    /**поле генерации проверки гипотезы*/
    private static HypothesisCheck hypothesisCheck;
    /**поле сгенерированного процесса*/
    private static double[] proc;
    /**поле панели для числовых характеристик*/
    private static JPanel jpNumericalCharacteristics;
    /**поле таблицы числовых характеристик*/
    private static JTable jtNumericalCharacteristic;
    /**поле памки для панелей*/
    private static final Border etched = BorderFactory.createEtchedBorder();
    /**поле цвета*/
    private static final Color color = new Color(219, 220, 155);
    /**поле размера для комбобокс*/
    private static final Dimension dimensionForJCombobox = new Dimension(221,25);
    /**поле ширины для фрейма*/
    private static final int width = 1000;
    /**поле высоты для фрейма*/
    private static final int height = 660;

    private class GridLayoutNew extends GridLayout{
        public GridLayoutNew(int rows, int cols, int hgap, int vgap) {
            super(rows,cols,hgap,vgap);
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                for (int i = 0; i < parent.getComponentCount(); i++) {
                    // Определение предпочтительного размера компонента
                    Dimension pref = parent.getComponent(i).getPreferredSize();
                    // Размещение компонента на экране
                    parent.getComponent(i).setBounds(290*i, 0, pref.width-10, pref.height-10);

                }

            }
        }
    }
}

