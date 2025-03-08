// Import các thư viện cần thiết cho giao diện và xử lý sự kiện
import java.awt.*;                      
import java.awt.event.*;                
import javax.swing.*;                   
import javax.swing.border.EmptyBorder; 

public class MySimpleCalculator {
    // Các thành phần giao diện
    private JFrame frame;                          // Cửa sổ chính của ứng dụng
    private JTextField displayField;               // Ô hiển thị biểu thức và kết quả
    private DefaultListModel<String> historyModel; // Model lưu trữ lịch sử các phép tính
    private JList<String> historyList;             // Danh sách hiển thị lịch sử các phép tính

    // Các biến lưu trữ dữ liệu tính toán
    private double operand1 = 0;       // Toán hạng thứ nhất
    private double operand2 = 0;       // Toán hạng thứ hai
    private double calculationResult = 0; // Kết quả của phép tính
    private String operation = "";     // Phép toán đang được chọn (ví dụ: +, -, *, /, x^y, √, %)
    private boolean operatorEntered = false; // Cờ đánh dấu đã nhập toán tử hay chưa

    // Hàm khởi tạo của lớp Calculator
    public MySimpleCalculator() {
        initialize(); // Gọi hàm khởi tạo giao diện và các thành phần
        // Đăng ký KeyEventDispatcher để xử lý các sự kiện bàn phím toàn cục
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            // Phương thức dispatchKeyEvent xử lý các sự kiện bàn phím
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                // Nếu sự kiện là phím được nhấn (KEY_PRESSED)
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = e.getKeyCode(); // Lấy mã phím được nhấn
                    if (keyCode == KeyEvent.VK_BACK_SPACE) { // Nếu nhấn Backspace
                        backspace(); // Gọi hàm xóa ký tự cuối
                        return true;
                    } else if (keyCode == KeyEvent.VK_ENTER) { // Nếu nhấn Enter
                        calculate(); // Gọi hàm tính toán
                        return true;
                    }
                }
                // Nếu sự kiện là phím gõ (KEY_TYPED)
                else if (e.getID() == KeyEvent.KEY_TYPED) {
                    char ch = e.getKeyChar(); // Lấy ký tự được gõ
                    // Nếu ký tự là số hoặc dấu chấm
                    if (Character.isDigit(ch) || ch == '.') {
                        appendText(String.valueOf(ch)); // Thêm ký tự vào display
                        return true;
                    }
                    // Nếu ký tự là các toán tử cơ bản: +, -, *, /
                    else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                        operatorAction(String.valueOf(ch)); // Xử lý nhập toán tử
                        return true;
                    }
                    // Nếu ký tự là '^', dùng cho phép lũy thừa (x^y)
                    else if (ch == '^') {
                        operatorAction("x^y");
                        return true;
                    }
                    // Nếu ký tự là '%', gọi hàm tính phần trăm
                    else if (ch == '%') {
                        percentageAction();
                        return true;
                    }
                    // Nếu ký tự là 'r' hoặc 'R', nhập căn bậc hai (√)
                    else if (ch == 'r' || ch == 'R') {
                        displayField.setText(displayField.getText() + "\u221A");
                        operation = "\u221A";
                        return true;
                    }
                }
                // Nếu không xử lý, trả về false để sự kiện được chuyển tiếp
                return false;
            }
        });
    }
    
    // Hàm khởi tạo giao diện người dùng
    private void initialize() {
        // Tạo cửa sổ chính (JFrame) và thiết lập các thuộc tính cơ bản
        frame = new JFrame("My Simple Calculator");         // Đặt tiêu đề cửa sổ
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Đóng ứng dụng khi cửa sổ đóng
        frame.setLayout(new BorderLayout(5, 5));              // Sử dụng BorderLayout với khoảng cách 5 pixel
        frame.setSize(600, 600);                              // Đặt kích thước cửa sổ
        frame.setLocationRelativeTo(null);                  // Căn giữa cửa sổ trên màn hình

        // --- Tạo và cấu hình ô hiển thị (display) ---
        displayField = new JTextField();                    // Tạo ô hiển thị
        displayField.setFont(new Font("Tahoma", Font.BOLD, 20)); // Đặt font cho display
        displayField.setEditable(false);                    // Không cho phép chỉnh sửa trực tiếp
        displayField.setHorizontalAlignment(SwingConstants.RIGHT); // Căn phải hiển thị kết quả
        displayField.setPreferredSize(new Dimension(0, 50));  // Đặt chiều cao cố định cho display
        // Thêm KeyListener để bắt phím Enter (nếu cần)
        displayField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculate();
                }
            }
        });
        // Tạo panel chứa display và đặt khoảng cách biên
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        displayPanel.add(displayField, BorderLayout.CENTER);

        // --- Tạo panel cho các nút cơ bản (số và toán tử) ---
        // Sử dụng GridLayout với 5 hàng, 4 cột, và khoảng cách 5 pixel giữa các nút
        JPanel basicPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        basicPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Row 1: Nút Clear (C), Backspace (B) và 2 placeholder
        JButton btnClear = createButton("C", e -> clearDisplay()); // Nút xóa toàn bộ
        JButton btnBackspace = createButton("B", e -> backspace());  // Nút xóa ký tự cuối
        basicPanel.add(btnClear);
        basicPanel.add(btnBackspace);
        basicPanel.add(new JLabel()); // Placeholder không hiển thị
        basicPanel.add(new JLabel());
        btnClear.setBackground(new Color(255,160,122));
        btnBackspace.setBackground(new Color(255,160,122));

        // Row 2: Nút số 7, 8, 9 và nút chia (/)
        basicPanel.add(createButton("7", e -> appendText("7")));
        basicPanel.add(createButton("8", e -> appendText("8")));
        basicPanel.add(createButton("9", e -> appendText("9")));
        basicPanel.add(createButton("/", e -> operatorAction("/")));

        // Row 3: Nút số 4, 5, 6 và nút nhân (*)
        basicPanel.add(createButton("4", e -> appendText("4")));
        basicPanel.add(createButton("5", e -> appendText("5")));
        basicPanel.add(createButton("6", e -> appendText("6")));
        basicPanel.add(createButton("*", e -> operatorAction("*")));

        // Row 4: Nút số 1, 2, 3 và nút trừ (-)
        basicPanel.add(createButton("1", e -> appendText("1")));
        basicPanel.add(createButton("2", e -> appendText("2")));
        basicPanel.add(createButton("3", e -> appendText("3")));
        basicPanel.add(createButton("-", e -> operatorAction("-")));

        // Row 5: Nút số 0, dấu chấm (.), nút bằng (=) và nút cộng (+)
        basicPanel.add(createButton("0", e -> appendText("0")));
        basicPanel.add(createButton(".", e -> appendDecimal()));
        basicPanel.add(createButton("=", e -> calculate()));
        basicPanel.add(createButton("+", e -> operatorAction("+")));

        // --- Tạo panel cho các phép toán nâng cao ---
        // Sử dụng GridLayout với 1 hàng, 3 cột
        JPanel advancedPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        advancedPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        // Sử dụng createButton để tạo các nút nâng cao với cùng kiểu dáng (nhỏ hơn chút)
        advancedPanel.add(createButton("x^y", e -> operatorAction("x^y")));
        advancedPanel.add(createButton("\u221A", e -> {
            displayField.setText(displayField.getText() + "\u221A");
            operation = "\u221A";
        }));
        advancedPanel.add(createButton("%", e -> percentageAction()));

        // --- Gộp panel các nút cơ bản và nâng cao vào cùng một panel ---
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(basicPanel, BorderLayout.CENTER);
        buttonsPanel.add(advancedPanel, BorderLayout.SOUTH);

        // --- Tạo panel bên trái chứa display và các nút ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(displayPanel, BorderLayout.NORTH);
        leftPanel.add(buttonsPanel, BorderLayout.CENTER);

        // --- Tạo panel cho lịch sử tính toán ---
        historyModel = new DefaultListModel<>();           // Khởi tạo model cho lịch sử
        historyList = new JList<>(historyModel);             // Tạo JList hiển thị lịch sử
        JScrollPane historyScrollPane = new JScrollPane(historyList); // Đặt JList vào thanh cuộn
        historyScrollPane.setPreferredSize(new Dimension(200, 0));    // Đặt chiều rộng cố định cho phần lịch sử

        // Tạo panel cho lịch sử với nhãn "Calculation History"
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel historyLabel = new JLabel("Calculation History");
        historyLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        historyPanel.add(historyLabel, BorderLayout.NORTH);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        // --- Gắn các panel vào cửa sổ chính (frame) ---
        frame.add(leftPanel, BorderLayout.CENTER);
        frame.add(historyPanel, BorderLayout.EAST);

        // Hiển thị cửa sổ ứng dụng
        frame.setVisible(true);
        // Sau khi cửa sổ hiển thị, tự động focus vào display để người dùng có thể nhập từ bàn phím ngay
        displayField.requestFocusInWindow();
    }

    // Hàm tạo nút bấm với nhãn và sự kiện đã định nghĩa
    // Tất cả các phím bấm sử dụng font 16 và kích thước ưu tiên cố định 60x60 pixel
    private JButton createButton(String label, ActionListener action) {
        JButton btn = new JButton(label);                     // Tạo đối tượng JButton với nhãn label
        btn.setFont(new Font("Tahoma", Font.BOLD, 16));         // Đặt font cho nút (16 điểm)
        btn.setPreferredSize(new Dimension(60, 60));            // Đặt kích thước ưu tiên cho nút (60x60 pixel)
        btn.addActionListener(action);                          // Gắn sự kiện xử lý khi nút được nhấn
        return btn;                                             // Trả về nút đã tạo
    }
    
    // Hàm thêm ký tự vào display (nối với nội dung hiện có)
    private void appendText(String text) {
        displayField.setText(displayField.getText() + text);
    }

    // Hàm thêm dấu thập phân (chỉ cho phép duy nhất 1 dấu trong phần số hiện tại)
    private void appendDecimal() {
        String currentText = displayField.getText(); // Lấy nội dung hiện tại của display
        if (!currentText.contains(".")) {            // Nếu chưa có dấu chấm nào
            if (currentText.isEmpty()) {
                displayField.setText("0.");          // Nếu display trống, bắt đầu với "0."
            } else {
                displayField.setText(currentText + "."); // Ngược lại, nối dấu chấm vào cuối
            }
        }
    }

    // Hàm xóa toàn bộ nội dung trong display và reset các biến tính toán
    private void clearDisplay() {
        displayField.setText("");    // Xóa toàn bộ nội dung display
        operatorEntered = false;     // Reset cờ đã nhập toán tử
        operation = "";              // Xóa phép toán đã nhập
        operand1 = 0;                // Reset toán hạng thứ nhất
        operand2 = 0;                // Reset toán hạng thứ hai
    }

    // Hàm xóa ký tự cuối cùng trong display; nếu ký tự bị xóa là toán tử, reset cờ và phép toán
    private void backspace() {
        String text = displayField.getText(); // Lấy chuỗi hiện tại từ display
        if (!text.isEmpty()) {                // Nếu display không rỗng
            char lastChar = text.charAt(text.length() - 1); // Lấy ký tự cuối cùng
            // Nếu ký tự cuối cùng là toán tử, reset cờ và phép toán
            if (lastChar == '+' || lastChar == '-' || lastChar == '*' ||
                lastChar == '/' || lastChar == '^' || lastChar == '\u221A') {
                operatorEntered = false;
                operation = "";
            }
            // Cập nhật display sau khi xóa ký tự cuối
            displayField.setText(text.substring(0, text.length() - 1));
        }
    }

    // Hàm xử lý khi người dùng nhập toán tử (từ bàn phím hoặc nút)
    private void operatorAction(String op) {
        if (!operatorEntered && !displayField.getText().isEmpty()) {
            try {
                // Lấy giá trị trong display làm toán hạng thứ nhất
                operand1 = Double.parseDouble(displayField.getText());
            } catch (NumberFormatException e) {
                showError("Invalid input!"); // Hiển thị lỗi nếu giá trị không hợp lệ
                return;
            }
            operation = op;         // Lưu phép toán được nhập
            operatorEntered = true; // Đánh dấu đã nhập toán tử
            // Nếu phép toán là "x^y", hiển thị dưới dạng "^"; ngược lại hiển thị phép toán như ban đầu
            String opSymbol = op.equals("x^y") ? "^" : op;
            appendText(opSymbol);   // Nối ký hiệu toán tử vào display
        }
    }

    // Hàm xử lý phép tính phần trăm (chia cho 100)
    private void percentageAction() {
        try {
            if (!displayField.getText().isEmpty()) {
                double number = Double.parseDouble(displayField.getText()); // Chuyển đổi nội dung display thành số
                calculationResult = number / 100; // Tính phần trăm
                String resultString = formatResult(calculationResult); // Định dạng kết quả
                historyModel.addElement(formatResult(number) + " % = " + resultString); // Thêm biểu thức vào lịch sử
                displayField.setText(resultString); // Hiển thị kết quả lên display
            }
        } catch (NumberFormatException ex) {
            showError("Invalid input!"); // Hiển thị lỗi nếu chuyển đổi không thành công
        }
    }

    // Hàm thực hiện tính toán khi nhấn "=" hoặc Enter
    private void calculate() {
        try {
            // Nếu phép toán là căn bậc hai (√)
            if (operation.equals("\u221A")) {
                String fullText = displayField.getText(); // Lấy toàn bộ biểu thức từ display
                int index = fullText.indexOf("\u221A");     // Tìm vị trí ký hiệu căn (√)
                if (index != -1 && fullText.length() > index + 1) {
                    String numberPart = fullText.substring(index + 1); // Lấy phần số sau ký hiệu căn
                    double number = Double.parseDouble(numberPart);      // Chuyển đổi thành số
                    if (number < 0) { // Không thể tính căn của số âm
                        throw new ArithmeticException("Cannot take square root of a negative number!");
                    }
                    calculationResult = Math.sqrt(number); // Tính căn bậc hai
                    String resultStr = formatResult(calculationResult); // Định dạng kết quả
                    historyModel.addElement("\u221A" + formatResult(number) + " = " + resultStr); // Thêm vào lịch sử
                    displayField.setText(resultStr); // Hiển thị kết quả
                    operatorEntered = false;         // Reset cờ nhập toán tử
                    operation = "";                  // Xóa phép toán hiện tại
                    operand1 = calculationResult;    // Hỗ trợ chaining: kết quả trở thành toán hạng thứ nhất
                }
                return; // Kết thúc hàm sau khi xử lý căn bậc hai
            }
            
            // Nếu đã nhập toán tử (biểu thức dạng: operand1 operator operand2)
            if (operatorEntered) {
                String fullText = displayField.getText(); // Lấy toàn bộ biểu thức từ display
                String opSymbol = operation.equals("x^y") ? "^" : operation; // Xác định ký hiệu của phép toán
                int opIndex = fullText.indexOf(opSymbol); // Tìm vị trí của ký hiệu toán tử
                if (opIndex == -1) {
                    showError("Operator not found in expression!"); // Thông báo lỗi nếu không tìm thấy
                    return;
                }
                // Lấy phần số thứ hai (sau ký hiệu toán tử)
                String operand2Str = fullText.substring(opIndex + opSymbol.length());
                if (operand2Str.isEmpty()) {
                    showError("Enter second operand!"); // Thông báo lỗi nếu chưa nhập số thứ hai
                    return;
                }
                try {
                    operand2 = Double.parseDouble(operand2Str); // Chuyển đổi phần số thứ hai thành giá trị số
                } catch (NumberFormatException e) {
                    showError("Invalid second operand!"); // Thông báo lỗi nếu chuyển đổi không thành công
                    return;
                }
                // Thực hiện phép tính dựa trên phép toán đã chọn
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
                        if (operand2 == 0) { // Kiểm tra chia cho 0
                            throw new ArithmeticException("Division by zero!");
                        }
                        calculationResult = operand1 / operand2;
                        break;
                    case "x^y":
                        calculationResult = Math.pow(operand1, operand2);
                        break;
                    default:
                        return; // Nếu phép toán không xác định, thoát hàm
                }
                // Tạo chuỗi biểu thức dạng "operand1 operator operand2 = result"
                String expression = formatResult(operand1) + " " + opSymbol + " " 
                                    + formatResult(operand2) + " = " + formatResult(calculationResult);
                historyModel.addElement(expression);         // Thêm biểu thức vào lịch sử
                displayField.setText(formatResult(calculationResult)); // Hiển thị kết quả lên display
                operatorEntered = false; // Reset cờ nhập toán tử
                operation = "";          // Xóa phép toán đã nhập
                operand1 = calculationResult; // Hỗ trợ chaining: kết quả cho phép tính tiếp theo
            }
        } catch (NumberFormatException ex) {
            showError("Invalid input!"); // Hiển thị lỗi nếu chuyển đổi số không thành công
        } catch (ArithmeticException ex) {
            showError(ex.getMessage());  // Hiển thị lỗi nếu xảy ra lỗi số học (ví dụ: chia cho 0)
        }
    }

    // Hàm định dạng kết quả: nếu kết quả là số nguyên, bỏ phần ".0"
    private String formatResult(double value) {
        return (value == (long) value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    // Hàm hiển thị thông báo lỗi qua hộp thoại
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, "Error: " + message);
    }

    // Hàm main để chạy ứng dụng
    public static void main(String[] args) {
        // Tạo giao diện trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new MySimpleCalculator());
    }
}
