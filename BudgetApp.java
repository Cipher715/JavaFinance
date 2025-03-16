import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class BudgetApp {
    private JFrame frame;
    private JTextField amountField;
    private JComboBox<String> categoryBox;
    private JTextArea recordArea;
    private JLabel totalLabel;
    private String delimiter = "$";
    private double totalIncome, totalExpense;
    private ArrayList<String> transactions = new ArrayList<>();


    public BudgetApp() {
        //For window operation
        frame = new JFrame("Java Finance");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set window size
        frame.setSize(660, 400);
        frame.setLayout(new FlowLayout());

        //Field for input amount 
        amountField = new JTextField(10);

        //Categroy to record income/expense
        String[] categories = {"Salary", "Bonus", "Food", "Transportation", "Entertainment", "Other"};
        categoryBox = new JComboBox<>(categories);

        //Button for submitting income/expense
        JButton incomeButton = new JButton("Add Income");
        JButton expenseButton = new JButton("Add Expence");

        //Field for showing records.
        recordArea = new JTextArea(10, 30);
        //Set editable state to false.
        recordArea.setEditable(false);

        incomeButton.addActionListener(e -> addTransaction("Income"));
        expenseButton.addActionListener(e -> addTransaction("Expence"));

        //Field for showing total counts.
        totalLabel = new JLabel("Total: Income $0 | Expence $0 | Balance $0", JLabel.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));


        //Layout the components
        frame.add(new JLabel("Amount:"));
        frame.add(amountField);
        frame.add(categoryBox);
        frame.add(incomeButton);
        frame.add(expenseButton);
        frame.add(new JScrollPane(recordArea));
        frame.add(totalLabel);

        //Load from text file.
        loadTransactions();
        //Update total amount.
        updateTotalLabel();

        //Make the window visible.
        frame.setVisible(true);
    }

    private void addTransaction(String type) {
        //Get the input amount from the textbox
        String amountText = amountField.getText();

        //Reruen error when the amount wasn't number.
        if (!amountText.matches("\\d+")) {
            JOptionPane.showMessageDialog(frame, "Please enter proper numeral.");
            return;
        }

        //Record the input depends on the record type
        String category = (String) categoryBox.getSelectedItem();
        String record;
        if(type.equals("Income")){
            record = type + ": " + category + " + " + "$" + amountText;
            totalIncome += Integer.parseInt(amountText);
        }else{
            record = type + ": " + category + " - " + "$" + amountText;
            totalExpense += Integer.parseInt(amountText);
        }

        //Add the record to the array
        transactions.add(record);
        recordArea.append(record + "\n");
        amountField.setText("");
        //Update total label
        updateTotalLabel();
    }


    //File operation
    private void loadTransactions() {
        //Try block
        try (BufferedReader reader = new BufferedReader(new FileReader("budget_data.txt"))) {
            String line;
            //iterate through all the lines recorded on the file.
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
                recordArea.append(line + "\n");
                //Calculate incomes and expenses.
                if(line.contains("Income")){
                    int index = line.indexOf(delimiter);
                    String income = line.substring(index + 1);
                    totalIncome += Integer.parseInt(income);
                } else if(line.contains("Expence")){
                    int index = line.indexOf(delimiter);
                    String expence = line.substring(index + 1);
                    totalExpense += Integer.parseInt(expence);
                }
            }
        } catch (IOException e) {
            System.out.println("No Data");
        }
    }

    //File save action
    public void saveTransactions() {
        //Try Block
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("budget_data.txt"))) {
            //For all transactions, record the text to a file.
            for (String record : transactions) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Update total balance, update the display text
    private void updateTotalLabel() {
        double balance = totalIncome - totalExpense;
        totalLabel.setText(String.format("Total: Income $%.0f | Expense $%.0f | Balance $%.0f", totalIncome, totalExpense, balance));
    }

    public static void main(String[] args) {
        BudgetApp app = new BudgetApp();
        Runtime.getRuntime().addShutdownHook(new Thread(app::saveTransactions));
    }
}