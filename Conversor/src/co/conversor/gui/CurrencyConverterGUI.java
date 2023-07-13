package co.conversor.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EtchedBorder;


public class CurrencyConverterGUI {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/daac2b06ec8dc55b734717a4/latest/";
   
    private JFrame mainFrame;
    private JComboBox<String> inputCurrencyComboBox;
    private JComboBox<String> outputCurrencyComboBox;
    private JTextField inputTextField;
    private JLabel resultadoLabel;
    
    


    public CurrencyConverterGUI() {
        mainFrame = new JFrame("Conversor de Divisas y Tiempo");
        mainFrame.setBackground(new Color(244, 246, 246));
        mainFrame.setFont(new Font("Bitstream Vera Sans", Font.PLAIN, 12));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setSize(526, 394);
        mainFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(186, 189, 182));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel currencyPanel = createCurrencyConversionPanel();
        JPanel timePanel = createTimeConversionPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(211, 215, 207));
        tabbedPane.addTab("Conversor Divisas", currencyPanel);
        tabbedPane.addTab("Conversor Tiempo", timePanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.getContentPane().add(mainPanel);
        mainFrame.setVisible(true);
    }

    private JPanel createCurrencyConversionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(244, 246, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel para seleccionar la divisa de entrada
        JPanel inputCurrencyPanel = new JPanel();
        inputCurrencyPanel.setBounds(20, 21, 219, 49);
        JLabel inputCurrencyLabel = new JLabel("De : ");
        inputCurrencyLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        inputCurrencyComboBox = new JComboBox<>(new String[]{"COP", "USD", "EUR", "GBP", "JPY", "KRW"});
        inputCurrencyComboBox.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        inputCurrencyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        inputCurrencyPanel.add(inputCurrencyLabel);
        inputCurrencyPanel.add(inputCurrencyComboBox);

        // Panel para seleccionar la divisa de salida
        JPanel outputCurrencyPanel = new JPanel();
        outputCurrencyPanel.setBounds(239, 21, 219, 49);
        JLabel outputCurrencyLabel = new JLabel("A : ");
        outputCurrencyLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        outputCurrencyComboBox = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY", "KRW", "COP"});
        outputCurrencyComboBox.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        outputCurrencyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        outputCurrencyPanel.add(outputCurrencyLabel);
        outputCurrencyPanel.add(outputCurrencyComboBox);

        // Panel para ingresar el valor a convertir
        JPanel inputPanel = new JPanel();
        inputPanel.setBounds(20, 70, 219, 95);
        JLabel inputLabel = new JLabel("Valor a cambiar :");
        inputLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        inputLabel.setBounds(25, 16, 156, 14);
        inputTextField = new JTextField(10);
        inputTextField.setFont(new Font("Bitstream Vera Sans", Font.PLAIN, 12));
        inputTextField.setBounds(25, 42, 169, 23);
        inputTextField.setBorder(null);
        inputPanel.setLayout(null);
        inputPanel.add(inputLabel);
        inputPanel.add(inputTextField);

        // Panel para mostrar el resultado de la conversión
        JPanel resultPanel = new JPanel();
        resultPanel.setBounds(239, 70, 219, 95);
        resultPanel.setLayout(null);
        JLabel resultTextLabel = new JLabel("Resultado :");
        resultTextLabel.setBounds(12, 37, 81, 15);
        resultTextLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        resultPanel.add(resultTextLabel);
        //resultPanel.setLayout(null);
        resultadoLabel = new JLabel();
        resultadoLabel.setText("0");
        resultadoLabel.setPreferredSize(new Dimension(7, 14));
        resultadoLabel.setMinimumSize(new Dimension(7, 14));
        resultadoLabel.setMaximumSize(new Dimension(7, 14));
        resultadoLabel.setToolTipText("");
        resultadoLabel.setBounds(105, 21, 102, 47);
        resultadoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        resultadoLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        resultPanel.add(resultadoLabel);
        
        
        // Botón para realizar la conversión
        JButton convertButton = new JButton("Convertir");
        convertButton.setBackground(new Color(138, 226, 52));
        convertButton.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        convertButton.setBounds(20, 177, 219, 49);
        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String inputCurrency = (String) inputCurrencyComboBox.getSelectedItem();
                    String outputCurrency = (String) outputCurrencyComboBox.getSelectedItem();
                    double inputValue = Double.parseDouble(inputTextField.getText());

                    // Obtener las tasas de cambio desde la API
                    String exchangeRateData = getExchangeRateDataFromAPI(inputCurrency);

                    // Realizar la conversión
                    double convertedValue = convertCurrency(outputCurrency, inputValue, exchangeRateData);

                    // Mostrar el resultado en la interfaz
                    resultadoLabel.setText(String.format("%.2f %s", convertedValue, outputCurrency));
                    //resultLabel.setText(String.format("funciona"));
                    
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Entrada no valida... Por favor ingresa un numero valido.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Se produjo un erros al acceder a la API. Por favor intenta mas tarde.");
                }
            }
        });

        // Botón para reiniciar las operaciones de conversión
        JButton resetButton = new JButton("Restaurar");
        resetButton.setBackground(new Color(127, 127, 127));
        resetButton.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        resetButton.setBounds(248, 177, 210, 49);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputTextField.setText("");
                resultadoLabel.setText("");
            }
        });
        panel.setLayout(null);
        panel.setLayout(null);
        panel.add(inputCurrencyPanel);
        panel.add(outputCurrencyPanel);
        panel.add(inputPanel);
        panel.add(resultPanel);
        panel.add(convertButton);
        panel.add(resetButton);

        return panel;
    }
    
//creacion de conversion segundos a minutos, horas, dias, semanas, meses y años.    

    private JPanel createTimeConversionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(244, 246, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel para ingresar el valor en segundos
        JPanel inputPanel = new JPanel();
        inputPanel.setBounds(20, 12, 439, 32);
        JLabel secondsLabel = new JLabel("Segundos :");
        secondsLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        secondsLabel.setBounds(5, 7, 96, 14);
        JTextField secondsTextField = new JTextField(10);
        secondsTextField.setBorder(null);
        secondsTextField.setBounds(131, 2, 308, 27);
        inputPanel.setLayout(null);
        inputPanel.add(secondsLabel);
        inputPanel.add(secondsTextField);

        // Panel para mostrar los resultados de la conversión
        JPanel resultPanel = new JPanel();
        resultPanel.setBounds(20, 42, 439, 153);
        JLabel hoursLabel = new JLabel("Horas:");
        hoursLabel.setBounds(12, 39, 68, 12);
        hoursLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        
        JLabel minutesLabel = new JLabel("Minutos:");
        minutesLabel.setBounds(12, 12, 68, 23);
        minutesLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        
        JLabel daysLabel = new JLabel("Dias:");
        daysLabel.setBounds(12, 63, 51, 15);
        daysLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        
        JLabel weeksLabel = new JLabel("Semanas:");
        weeksLabel.setBounds(12, 86, 80, 14);
        weeksLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        
        JLabel monthsLabel = new JLabel("Meses:");
        monthsLabel.setBounds(12, 108, 68, 14);
        monthsLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        
        JLabel yearsLabel = new JLabel("Años:");
        yearsLabel.setBounds(12, 130, 68, 14);
        yearsLabel.setFont(new Font("Bitstream Vera Sans", Font.BOLD, 12));
        
        JLabel hoursResultLabel = new JLabel();
        hoursResultLabel.setBounds(110, 39, 101, 19);
        
        JLabel minutesResultLabel = new JLabel();
        minutesResultLabel.setBounds(110, 12, 101, 19);
        
        JLabel daysResultLabel = new JLabel();
        daysResultLabel.setBackground(new Color(0, 0, 0));
        daysResultLabel.setBounds(110, 59, 138, 19);
        
        JLabel weeksResultLabel = new JLabel();
        weeksResultLabel.setBounds(110, 77, 165, 23);
        
        JLabel monthsResultLabel = new JLabel();
        monthsResultLabel.setBounds(110, 97, 133, 25);
        
        JLabel yearsResultLabel = new JLabel();
        yearsResultLabel.setBounds(110, 122, 138, 31);
        resultPanel.setLayout(null);
        resultPanel.add(hoursLabel);
        resultPanel.add(hoursResultLabel);
        
        resultPanel.add(minutesLabel);
        resultPanel.add(minutesResultLabel);
        
        resultPanel.add(daysLabel);
        resultPanel.add(daysResultLabel);
        
        resultPanel.add(weeksLabel);
        resultPanel.add(weeksResultLabel);
        
        resultPanel.add(monthsLabel);
        resultPanel.add(monthsResultLabel);
        
        resultPanel.add(yearsLabel);
        resultPanel.add(yearsResultLabel);

        // Botón para realizar la conversión
        JButton convertButton = new JButton("Convertir");
        convertButton.setBackground(new Color(138, 226, 52));
        convertButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        convertButton.setBorder(null);
        convertButton.setBounds(20, 207, 439, 32);
        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    long seconds = Long.parseLong(secondsTextField.getText());

                    // Realizar la conversión
                    long minutes = seconds / 60;
                    
                    long hours = seconds /3600;
                    
                    long days = seconds / 86400;
                    long weeks = seconds / 604800;
                    long months = seconds / 2592000;
                    long years = seconds / 31556952;

                    // Mostrar los resultados en la interfaz
                    hoursResultLabel.setText(String.valueOf(hours));
                    minutesResultLabel.setText(String.valueOf(minutes));
                    daysResultLabel.setText(String.valueOf(days));
                    weeksResultLabel.setText(String.valueOf(weeks));
                    monthsResultLabel.setText(String.valueOf(months));
                    yearsResultLabel.setText(String.valueOf(years));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Entrada invalida. Por favor ingresa un numero en segundos.");
                }
            }
        });

        // Botón para reiniciar las operaciones de conversión
        JButton resetButton = new JButton("Restaurar");
        resetButton.setBackground(new Color(127, 127, 127));
        resetButton.setBorder(null);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetButton.setBounds(20, 244, 439, 32);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                secondsTextField.setText("");
                hoursResultLabel.setText("");
                minutesResultLabel.setText("");
                daysResultLabel.setText("");
                weeksResultLabel.setText("");
                monthsResultLabel.setText("");
                yearsResultLabel.setText("");
            }
        });
        panel.setLayout(null);
        panel.setLayout(null);
        panel.add(inputPanel);
        panel.add(resultPanel);
        panel.add(convertButton);
        panel.add(resetButton);

        return panel;
    }

    private static String getExchangeRateDataFromAPI(String baseCurrency) throws IOException {
        String apiUrl = API_URL + baseCurrency;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    private static double convertCurrency(String outputCurrency, double value, String exchangeRateData) {
        // Parsear los datos de las tasas de cambio desde la API
        double exchangeRate = parseExchangeRate(exchangeRateData, outputCurrency);

        // Realizar la conversión
        return value * exchangeRate;
    }
    
    

    private static double parseExchangeRate(String exchangeRateData, String currencyCode) {
        // Extraer la tasa de cambio para el código de divisa especificado desde la respuesta de la API
        int startIndex = exchangeRateData.indexOf(currencyCode) + 5;
        int endIndex = exchangeRateData.indexOf(",", startIndex);
        String exchangeRateString = exchangeRateData.substring(startIndex, endIndex);
        return Double.parseDouble(exchangeRateString);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CurrencyConverterGUI();
            }
        });
    }
}
