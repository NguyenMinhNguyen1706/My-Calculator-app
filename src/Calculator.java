import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;

public class Calculator {

    // Khung chính của ứng dụng (cửa sổ)
    public JFrame calculatorFrame;
    // Ô hiển thị nhập liệu và kết quả
    public JTextField displayField;
    // Model lưu trữ lịch sử các phép tính (dạng chuỗi)
    public DefaultListModel<String> calculationHistoryModel;
    // Danh sách hiển thị lịch sử các phép tính
    public JList<String> calculationHistoryList;
    
    // Biến lưu trữ các toán hạng và kết quả của phép tính
    double operand1;              // Số thứ nhất
    double operand2;              // Số thứ hai
    double calculationResult;     // Kết quả của phép tính
    // Biến lưu phép toán được chọn (ví dụ: "+", "-", "*", "/", "x^y", "√", "%")
    String operation;
    
    // Phương thức main: Điểm vào của chương trình
    public static void main(String[] args) {
        // Dùng EventQueue để đảm bảo các thao tác giao diện được xử lý đúng luồng
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Tạo đối tượng ScientificCalculator và hiển thị cửa sổ
                    Calculator calculator = new Calculator();
                    calculator.calculatorFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace(); // In ra lỗi nếu có
                }
            }
        });
    }
    
    // Constructor: Gọi phương thức khởi tạo giao diện
    public Calculator() {
        initComponents();
    }
    
    // Phương thức khởi tạo giao diện và các thành phần
    private void initComponents() {
        // Tạo khung chính và thiết lập các thuộc tính cơ bản
        calculatorFrame = new JFrame();                          // Tạo cửa sổ chính
        calculatorFrame.setTitle("My Simple Calculator");         // Đặt tiêu đề cửa sổ
        calculatorFrame.setBounds(100, 100, 600, 600);             // Đặt vị trí và kích thước cửa sổ
        calculatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Đóng chương trình khi cửa sổ đóng
        calculatorFrame.getContentPane().setLayout(null);         // Sử dụng layout rỗng (absolute layout)
        
        // Tạo label tiêu đề và thêm vào khung
        JLabel titleLabel = new JLabel("My Simple Calculator");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 25));     // Đặt font, kích thước và kiểu chữ
        titleLabel.setBounds(10, 10, 350, 30);                     // Đặt vị trí, kích thước của label
        calculatorFrame.getContentPane().add(titleLabel);          // Thêm label vào khung
        
        // Tạo ô nhập liệu để hiển thị số và kết quả
        displayField = new JTextField();
        displayField.setFont(new Font("Tahoma", Font.BOLD, 20));   // Đặt font cho ô nhập
        displayField.setBounds(10, 50, 350, 40);                    // Đặt vị trí và kích thước ô nhập
        calculatorFrame.getContentPane().add(displayField);        // Thêm ô nhập vào khung
        displayField.setColumns(10);                               // Số cột (không bắt buộc)
        
        // Thêm KeyListener để xử lý nhập từ bàn phím vào ô displayField
        displayField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();  // Lấy ký tự vừa gõ
                // Nếu gõ các ký tự phép toán (+, -, *, /, %) và ô nhập không rỗng
                if ((keyChar == '+' || keyChar == '-' || keyChar == '*' ||
                     keyChar == '/' || keyChar == '%') && displayField.getText().length() > 0) {
                    try {
                        // Chuyển nội dung ô nhập thành số và lưu vào operand1
                        operand1 = Double.parseDouble(displayField.getText());
                        // Gán phép toán theo ký tự vừa gõ
                        if (keyChar == '+') {
                            operation = "+";
                        } else if (keyChar == '-') {
                            operation = "-";
                        } else if (keyChar == '*') {
                            operation = "*";
                        } else if (keyChar == '/') {
                            operation = "/";
                        } else if (keyChar == '%') {
                            operation = "%";
                        }
                        displayField.setText("");  // Xóa ô nhập để người dùng nhập số thứ hai
                        e.consume();               // Ngăn ký tự phép toán hiển thị trên ô nhập
                    } catch (NumberFormatException ex) {
                        // Nếu nội dung không phải là số, hiển thị thông báo lỗi
                        JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                    }
                }
                // Nếu gõ ký tự '^', thiết lập phép lũy thừa (x^y)
                if (keyChar == '^' && displayField.getText().length() > 0) {
                    try {
                        operand1 = Double.parseDouble(displayField.getText());
                        operation = "x^y";       // Đặt phép toán là lũy thừa
                        displayField.setText(""); // Xóa ô nhập
                        e.consume();              // Ngăn ký tự '^' hiển thị
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                    }
                }
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                // Nếu nhấn phím Enter, thực hiện tính toán
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculate();
                }
            }
        });
        
        // ----- Các nút thao tác trên giao diện -----
        
        // Nút Clear ("C") để xóa toàn bộ ô nhập
        JButton clearButton = new JButton("C");
        clearButton.setFont(new Font("Tahoma", Font.PLAIN, 18));   // Đặt font cho nút
        clearButton.setBounds(10, 100, 80, 40);                     // Vị trí và kích thước nút
        clearButton.addActionListener(new ActionListener() {      // Xử lý sự kiện khi nhấn nút
            public void actionPerformed(ActionEvent e) {
                displayField.setText("");                         // Xóa nội dung ô nhập
            }
        });
        calculatorFrame.getContentPane().add(clearButton);          // Thêm nút vào khung
        
        // Nút Backspace ("B") để xóa ký tự cuối cùng trong ô nhập
        JButton backspaceButton = new JButton("B");
        backspaceButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        backspaceButton.setBounds(100, 100, 80, 40);
        backspaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String currentText = displayField.getText();      // Lấy nội dung hiện tại của ô nhập
                if (currentText.length() > 0) {                     // Nếu có ký tự, xóa ký tự cuối
                    displayField.setText(currentText.substring(0, currentText.length() - 1));
                }
            }
        });
        calculatorFrame.getContentPane().add(backspaceButton);
        
        // ----- Nút số và các phép toán cơ bản -----
        // Nút "7"
        JButton btn7 = new JButton("7");
        btn7.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn7.setBounds(10, 150, 60, 40);
        btn7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "7"); // Thêm số 7 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn7);
        
        // Nút "8"
        JButton btn8 = new JButton("8");
        btn8.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn8.setBounds(80, 150, 60, 40);
        btn8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "8"); // Thêm số 8 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn8);
        
        // Nút "9"
        JButton btn9 = new JButton("9");
        btn9.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn9.setBounds(150, 150, 60, 40);
        btn9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "9"); // Thêm số 9 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn9);
        
        // Nút chia ("/") cho phép chia số (phép toán nhị phân)
        JButton divisionButton = new JButton("/");
        divisionButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        divisionButton.setBounds(220, 150, 60, 40);
        divisionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Lấy số hiện tại trong ô nhập và lưu vào operand1
                    operand1 = Double.parseDouble(displayField.getText());
                    operation = "/";            // Đặt phép toán là chia
                    displayField.setText("");   // Xóa ô nhập để nhập số thứ hai
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                }
            }
        });
        calculatorFrame.getContentPane().add(divisionButton);
        
        // Nút "4"
        JButton btn4 = new JButton("4");
        btn4.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn4.setBounds(10, 200, 60, 40);
        btn4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "4"); // Thêm số 4 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn4);
        
        // Nút "5"
        JButton btn5 = new JButton("5");
        btn5.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn5.setBounds(80, 200, 60, 40);
        btn5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "5"); // Thêm số 5 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn5);
        
        // Nút "6"
        JButton btn6 = new JButton("6");
        btn6.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn6.setBounds(150, 200, 60, 40);
        btn6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "6"); // Thêm số 6 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn6);
        
        // Nút nhân ("*") cho phép nhân số
        JButton multiplicationButton = new JButton("*");
        multiplicationButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        multiplicationButton.setBounds(220, 200, 60, 40);
        multiplicationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    operand1 = Double.parseDouble(displayField.getText());
                    operation = "*";            // Đặt phép toán là nhân
                    displayField.setText("");   // Xóa ô nhập
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                }
            }
        });
        calculatorFrame.getContentPane().add(multiplicationButton);
        
        // Nút "1"
        JButton btn1 = new JButton("1");
        btn1.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn1.setBounds(10, 250, 60, 40);
        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "1"); // Thêm số 1 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn1);
        
        // Nút "2"
        JButton btn2 = new JButton("2");
        btn2.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn2.setBounds(80, 250, 60, 40);
        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "2"); // Thêm số 2 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn2);
        
        // Nút "3"
        JButton btn3 = new JButton("3");
        btn3.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn3.setBounds(150, 250, 60, 40);
        btn3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "3"); // Thêm số 3 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn3);
        
        // Nút trừ ("-") cho phép trừ số
        JButton subtractionButton = new JButton("-");
        subtractionButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        subtractionButton.setBounds(220, 250, 60, 40);
        subtractionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    operand1 = Double.parseDouble(displayField.getText());
                    operation = "-";            // Đặt phép toán là trừ
                    displayField.setText("");   // Xóa ô nhập
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                }
            }
        });
        calculatorFrame.getContentPane().add(subtractionButton);
        
        // Nút "0"
        JButton btn0 = new JButton("0");
        btn0.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn0.setBounds(10, 300, 60, 40);
        btn0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "0"); // Thêm số 0 vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(btn0);
        
        // Nút dấu chấm (".") để nhập số thập phân
        JButton decimalButton = new JButton(".");
        decimalButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        decimalButton.setBounds(80, 300, 60, 40);
        decimalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayField.setText(displayField.getText() + "."); // Thêm dấu chấm vào ô nhập
            }
        });
        calculatorFrame.getContentPane().add(decimalButton);
        
        // Nút "=" để thực hiện tính toán
        JButton equalsButton = new JButton("=");
        equalsButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        equalsButton.setBounds(150, 300, 60, 40);
        equalsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculate();  // Gọi phương thức tính toán khi nhấn "="
            }
        });
        calculatorFrame.getContentPane().add(equalsButton);
        
        // Nút "+" để thực hiện phép cộng
        JButton additionButton = new JButton("+");
        additionButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        additionButton.setBounds(220, 300, 60, 40);
        additionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    operand1 = Double.parseDouble(displayField.getText());
                    operation = "+";            // Đặt phép toán là cộng
                    displayField.setText("");   // Xóa ô nhập
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                }
            }
        });
        calculatorFrame.getContentPane().add(additionButton);
        
        // ----- Các phép toán nâng cao -----
        // Nút lũy thừa ("x^y")
        JButton exponentiationButton = new JButton("x^y");
        exponentiationButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        exponentiationButton.setBounds(10, 350, 80, 40);
        exponentiationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    operand1 = Double.parseDouble(displayField.getText());
                    operation = "x^y";         // Đặt phép toán là lũy thừa
                    displayField.setText("");   // Xóa ô nhập để nhập số thứ hai
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                }
            }
        });
        calculatorFrame.getContentPane().add(exponentiationButton);
        
        // Nút căn bậc hai ("√")
        // Lưu ý: Khi bấm nút này, chỉ đặt phép toán là "√" và xóa ô nhập.
        // Sau đó người dùng nhập số cần tính căn và nhấn "=" để tính.
        JButton squareRootButton = new JButton("\u221A");
        squareRootButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        squareRootButton.setBounds(100, 350, 80, 40);
        squareRootButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                operation = "√";             // Đặt phép toán là căn bậc hai
                displayField.setText("");      // Xóa ô nhập để nhập số cần tính căn
            }
        });
        calculatorFrame.getContentPane().add(squareRootButton);
        
        // Nút phần trăm ("%")
        JButton percentageButton = new JButton("%");
        percentageButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        percentageButton.setBounds(190, 350, 80, 40);
        percentageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double number = Double.parseDouble(displayField.getText());
                    calculationResult = number / 100;  // Tính phần trăm của số
                    String resultString = formatResult(calculationResult);
                    // Thêm vào lịch sử (ví dụ: "16 % = 0.16")
                    calculationHistoryModel.addElement(formatResult(number) + " % = " + resultString);
                    displayField.setText(resultString); // Hiển thị kết quả lên ô nhập
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
                }
            }
        });
        calculatorFrame.getContentPane().add(percentageButton);
        
        // ----- Phần hiển thị Lịch sử Tính toán -----
        // Tạo model cho lịch sử
        calculationHistoryModel = new DefaultListModel<>();
        // Tạo JList sử dụng model trên
        calculationHistoryList = new JList<>(calculationHistoryModel);
        // Đặt thanh cuộn cho danh sách lịch sử
        JScrollPane historyScrollPane = new JScrollPane(calculationHistoryList);
        historyScrollPane.setBounds(370, 50, 200, 340);
        calculatorFrame.getContentPane().add(historyScrollPane);
        
        // Label cho phần lịch sử tính toán
        JLabel historyLabel = new JLabel("Calculation History");
        historyLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        historyLabel.setBounds(370, 10, 200, 30);
        calculatorFrame.getContentPane().add(historyLabel);
    }
    
    // Phương thức thực hiện tính toán khi nhấn "=" hoặc Enter
    private void calculate() {
        try {
            // Nếu phép toán là căn bậc hai ("√"), thực hiện phép toán đơn (unary operation)
            if (operation.equals("√")) {
                double number = Double.parseDouble(displayField.getText());
                // Nếu số âm, báo lỗi vì không thể tính căn của số âm
                if (number < 0) {
                    throw new ArithmeticException("Cannot take square root of a negative number!");
                }
                calculationResult = Math.sqrt(number); // Tính căn bậc hai
                String numberString = formatResult(number);
                String resultString = formatResult(calculationResult);
                calculationHistoryModel.addElement("√" + numberString + " = " + resultString);
                displayField.setText(resultString); // Hiển thị kết quả lên ô nhập
                return; // Kết thúc phương thức cho phép toán đơn
            }
            
            // Nếu không phải căn bậc hai, thực hiện phép toán nhị phân:
            // Lấy số thứ hai từ ô nhập
            operand2 = Double.parseDouble(displayField.getText());
            
            // Xử lý các phép toán nhị phân theo biến operation
            switch (operation) {
                case "+":
                    calculationResult = operand1 + operand2;
                    break;
                case "-":
                    calculationResult = operand1 - operand2;
                    break;
                case "*":
                    calculationResult = operand1 * operand2;
                    break;
                case "/":
                    if (operand2 == 0) {
                        throw new ArithmeticException("Division by zero!");
                    }
                    calculationResult = operand1 / operand2;
                    break;
                case "x^y":
                    calculationResult = Math.pow(operand1, operand2);
                    break;
                case "%":
                    calculationResult = operand1 % operand2;
                    break;
                default:
                    return; // Nếu phép toán không hợp lệ, thoát khỏi hàm
            }
            
            // Định dạng các giá trị (loại bỏ ".0" nếu số là số nguyên)
            String operand1Str = formatResult(operand1);
            String operand2Str = formatResult(operand2);
            String resultStr = formatResult(calculationResult);
            
            // Nếu phép toán là lũy thừa ("x^y"), hiển thị ký hiệu "^" trong lịch sử
            String operationSymbol;
            if (operation.equals("x^y")) {
                operationSymbol = "^";
            } else {
                operationSymbol = operation;
            }
            
            calculationHistoryModel.addElement(operand1Str + operationSymbol + operand2Str + " = " + resultStr);
            displayField.setText(resultStr); // Hiển thị kết quả lên ô nhập
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(calculatorFrame, "Error: Invalid input!");
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(calculatorFrame, "Error: " + ex.getMessage());
        }
    }
    
    private String formatResult(double value) {
        // Nếu giá trị bằng với phần nguyên của nó, trả về chuỗi của phần nguyên
        if (value == (long) value) {
            return String.valueOf((long) value);
        } else {
            return String.valueOf(value); // Ngược lại, trả về chuỗi đầy đủ của số
        }
    }
}
