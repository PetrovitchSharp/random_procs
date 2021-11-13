import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ViewSwing {

    ViewSwing(){
        begin();
    }

    /** Метод создания форм с вводом данных*/
    private void begin(){
        jFrame = new JFrame("Генерация случайного процесса");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(800, 660));

        jPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(1,2,5,15);
        jPanel.setLayout(gridLayout);

        getJpDataInput();
        getJpDataOutput();

        jPanel.add(jpDataInput);
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
        JTextField numberOfSamples = new JTextField(26);
        numberOfSamples.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                if (!numberOfSamples.getText().isEmpty() && !CheckParameters.isNumberOfSamples(numberOfSamples.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_NUMBER_OF_SAMPLES);
                    numberOfSamples.setText("");
                }
            }
        });
        jpDataInput.add(numberOfSamples);

        //вызов методов для создания панелей для ввода параметров кф/закона распределения
        getJpCorrelationFunction();
        getJpDistributionLaw();

        jpDataInput.add(new JLabel("способ генерации случайных величин"));
        String[] sWaysOfGeneration = {"корреляционная функция", "с заданным законном распределения"};
        JRadioButton rbCorrelationFunction = new JRadioButton(sWaysOfGeneration[0],true);
        JRadioButton rbDistributionLaw = new JRadioButton(sWaysOfGeneration[1],false);
        rbDistributionLaw.setBackground(color);
        rbCorrelationFunction.setBackground(color);
        ButtonGroup waysOfGeneration = new ButtonGroup();
        waysOfGeneration.add(rbCorrelationFunction);
        waysOfGeneration.add(rbDistributionLaw);

        //слушатель для выбора кф
        rbCorrelationFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbCorrelationFunction.isSelected()) {
                    jpCorrelationFunction.setPreferredSize(jpDistributionLaw.getPreferredSize());
                    jpCorrelationFunction.setVisible(true);
                    jpDistributionLaw.setVisible(false);
                    jpDistributionLaw.setPreferredSize(new Dimension(0,0));
                }
            }
        });
        //слушатель для выбора закона распределения
        rbDistributionLaw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbDistributionLaw.isSelected()) {
                    jpDistributionLaw.setPreferredSize(jpCorrelationFunction.getPreferredSize());
                    jpDistributionLaw.setVisible(true);
                    jpCorrelationFunction.setVisible(false);
                    jpCorrelationFunction.setPreferredSize(new Dimension(0,0));
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
                    if (numberOfSamples.getText().isEmpty()
                            || (rbCorrelationFunction.isSelected() && correlationFunction == null)
                            || (rbDistributionLaw.isSelected() && distributionLaw == null))
                        throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                    randomProcess = new RandomProcess();
                    randomProcess.setCorrelationFunction(correlationFunction);
                    randomProcess.setDistributionLaw(distributionLaw);
                    randomProcess.setHypothesisCheck(hypothesisCheck);
                    randomProcess.setWaysOfGeneration(rbDistributionLaw.isSelected() ? sWaysOfGeneration[1] : sWaysOfGeneration[0]);
                    randomProcess.setNumberOfSamples(numberOfSamples.getText().isEmpty() ? null : Integer.parseInt(numberOfSamples.getText()));

                    proc =  CalculateClass.generateRandomProc(randomProcess);
                    getTable();
                    //TODO: вызов метода рассчета результата, вызов метода для вывода информации
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

        String[] sSignificanceCriterion = {"критерий согласия Пирсона", "критерий согласия Колмогорова"};
        JComboBox significanceCriterion = new JComboBox(sSignificanceCriterion);
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

                    hypothesisCheck = new HypothesisCheck();
                    hypothesisCheck.setNumberOfDegreesOfFreedom(Integer.parseInt(numberOfDegreesOfFreedom.getText()));
                    hypothesisCheck.setSignificanceCriterion(significanceCriterion.getSelectedItem().toString());
                    hypothesisCheck.setSignificanceLevel(Double.parseDouble(significanceLevel.getSelectedItem().toString()));

                    //TODO: вызов метода проверки гипотезы, вывод сообщения
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
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
                            && oscillationFrequencyValue.getText().isEmpty()))
                        throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                    correlationFunction = new CorrelationFunction();
                    correlationFunction.setKindCorrelationFunction(kindCorrelationFunction.getSelectedItem().toString());
                    correlationFunction.setAttenuationRates(Double.parseDouble(attenuation_rates.getText()));
                    correlationFunction.setOscillationFrequencyValue(!oscillationFrequencyValue.getText().isEmpty()?Double.parseDouble(oscillationFrequencyValue.getText()):null);

                    //TODO: вызов метода генерации, вывод окошка с результатами
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
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
                                && ((JTextField)jpUniform.getComponent(3)).getText().isEmpty())
                            throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                        if (Double.parseDouble(((JTextField)jpUniform.getComponent(1)).getText())
                                > Double.parseDouble(((JTextField)jpUniform.getComponent(3)).getText()))
                            throw new Exception(ExceptionMessage.EXCEPTION_LEFT_IS_MORE_RIGHT);

                        distributionLaw.setRight(Double.parseDouble(((JTextField)jpUniform.getComponent(1)).getText()));
                        distributionLaw.setLeft(Double.parseDouble(((JTextField)jpUniform.getComponent(3)).getText()));
                    } else{

                        if (((JTextField)jpExponential.getComponent(1)).getText().isEmpty())
                            throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                        distributionLaw.setIntensity(Double.parseDouble(((JTextField)jpExponential.getComponent(1)).getText()));
                    }

                    //TODO: вызов метода генерации, вывод окошка с результатами
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
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
            }
        });
        jpExponential.add(intensity);
        jpExponential.setPreferredSize(new Dimension(0,0));
        return jpExponential;
    }

    /** Метод создания панели для вывода данных*/
    private void getJpDataOutput(){
        jpDataOutput = new JPanel();
        jpDataOutput.setPreferredSize(new Dimension(width - jpDataInput.getWidth(), height));

        GridLayout gridLayout = new GridLayout(2,2,5,15);
        jpDataOutput.setLayout(gridLayout);

        jpNumericalCharacteristics = new JPanel();
        jpNumericalCharacteristics.setBorder(BorderFactory.createTitledBorder(etched, "Числовые характеристики"));

        //Массив содержащий заголоки таблицы
        Object[] headers = new String[]{"Момент", "Теоретическое значение", "Эмпирическое значение"};

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(headers);

        jtNumericalCharacteristic = new JTable(model);
        jtNumericalCharacteristic.setFillsViewportHeight(true);

        //Создаем панель прокрутки и включаем в ее состав нашу таблицу
        JScrollPane jscrlp = new JScrollPane(jtNumericalCharacteristic);
        jscrlp.setPreferredSize(new Dimension(350,100));

        jpNumericalCharacteristics.add(jscrlp);

        jpDataOutput.add(jpNumericalCharacteristics);
        jpDataOutput.setVisible(false);
    }

    /** Метод создания панели для получения таблицы*/
    private void getTable(){

        Object[][] data;
        Object[] headers;
        if (randomProcess.getWaysOfGeneration().equals("с заданным законном распределения")){
            headers = new String[]{"Момент", "Теоретическое значение", "Эмпирическое значение"};
            //Массив содержащий информацию для таблицы
            data = new Object[][]{
                    {"1", CalculateClass.getMathematicalExpectation(randomProcess), CalculateClass.getMean(proc)},
                    {"2", CalculateClass.getTheoreticalDispertion(randomProcess), CalculateClass.getExperimentalDispertion(proc)},
                    {"3", CalculateClass.getTheoreticalAsymmetryCoefficient(randomProcess), CalculateClass.getExperimentalAsymmetryCoefficient(proc)},
                    {"4", CalculateClass.getTheoreticalKurtosisCoefficient(randomProcess), CalculateClass.getExperimentalKurtosisCoefficient(proc)}
            };
        } else{
            headers = new String[]{"Момент","Эмпирическое значение"};
            data = new Object[][]{
                    {"1", CalculateClass.getMean(proc)},
                    {"2", CalculateClass.getExperimentalDispertion(proc)},
                    {"3", CalculateClass.getExperimentalAsymmetryCoefficient(proc)},
                    {"4", CalculateClass.getExperimentalKurtosisCoefficient(proc)}
            };
        }

        DefaultTableModel modelNew = (DefaultTableModel)jtNumericalCharacteristic.getModel();
        modelNew.setDataVector(data,headers);

        jpDataOutput.setVisible(true);
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
    /**поле генерации процесса*/
    private static RandomProcess randomProcess;
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
    private static final int width = 800;
    /**поле высоты для фрейма*/
    private static final int height = 660;

}

