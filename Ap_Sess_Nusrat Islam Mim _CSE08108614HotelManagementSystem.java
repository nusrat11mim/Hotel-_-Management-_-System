import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

// ===== ROOM CLASS =====
class Room implements Serializable {
    int roomNumber;
    String roomType;
    double pricePerNight;
    boolean isBooked;
    String guestName;
    String guestPhone;  // Add phone for unique identification
    int daysBooked;
    double extraCharges;

    Room(int roomNumber, String roomType, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.isBooked = false;
        this.guestName = "";
        this.guestPhone = "";
        this.daysBooked = 0;
        this.extraCharges = 0;
    }

    double calculateBill() {
        double baseBill = pricePerNight * daysBooked + extraCharges;
        double discount = (daysBooked >= 5) ? 0.10 * baseBill : 0;
        double tax = 0.12 * (baseBill - discount);
        return baseBill - discount + tax;
    }
}

// ===== EMPLOYEE CLASS =====
class Employee implements Serializable {
    String name;
    String designation;
    double salary;

    Employee(String name, String designation, double salary) {
        this.name = name;
        this.designation = designation;
        this.salary = salary;
    }

    void display() {
        System.out.println(name + " | designation: " + designation + " | Salary: $" + salary);
    }
}

// ===== DATA STORAGE CLASS =====
class HotelData implements Serializable {
    List<Room> rooms;
    List<Employee> employees;
    double totalIncome;
    
    HotelData(List<Room> rooms, List<Employee> employees, double totalIncome) {
        this.rooms = new ArrayList<>(rooms);
        this.employees = new ArrayList<>(employees);
        this.totalIncome = totalIncome;
    }
}

// ===== LOGIN WINDOW =====
class LoginWindow extends JFrame {
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private HotelManagementSystemGUI mainGUI;
    
    public LoginWindow(HotelManagementSystemGUI mainGUI) {
        this.mainGUI = mainGUI;
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Hotel Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // Login Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Exit");
        
        loginButton.addActionListener(e -> login());
        cancelButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        // Enter key for login
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            mainGUI.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ===== MAIN GUI WINDOW =====
class HotelManagementSystemGUI extends JFrame {
    private List<Room> rooms;
    private List<Employee> employees;
    private double totalIncome;
    private static final String DATA_FILE = "hotel_data.ser";
    private static final int MAX_ROOMS_PER_GUEST = 5; // Maximum 5 rooms per guest
    
    private JTabbedPane tabbedPane;
    
    public HotelManagementSystemGUI() {
        rooms = new ArrayList<>();
        employees = new ArrayList<>();
        totalIncome = 0;
        
        loadData();
        if (rooms.isEmpty()) {
            initializeRooms();
        }
        
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Data");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        saveItem.addActionListener(event -> saveData());
        logoutItem.addActionListener(event -> logout());
        exitItem.addActionListener(event -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Room Management", createRoomPanel());
        tabbedPane.addTab("Employee Management", createEmployeePanel());
        tabbedPane.addTab("Daily Report", createReportPanel());
        tabbedPane.addTab("Multiple Room Booking", createMultiBookingPanel());
        
        add(tabbedPane);
        
        // Save on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }
    
    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top buttons panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewAllBtn = new JButton("View All Rooms");
        JButton viewAvailableBtn = new JButton("View Available Rooms");
        JButton bookRoomBtn = new JButton("Book Single Room");
        JButton orderFoodBtn = new JButton("Order Food");
        JButton checkoutBtn = new JButton("Check Out");
        JButton addRoomBtn = new JButton("Add New Room");
        
        // Room list display
        JTextArea roomTextArea = new JTextArea(20, 70);
        roomTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(roomTextArea);
        
        // Button actions
        viewAllBtn.addActionListener(event -> {
            StringBuilder sb = new StringBuilder();
            if (rooms.isEmpty()) {
                sb.append("No rooms available.\n");
            } else {
                for (Room r : rooms) {
                    sb.append(String.format("Room %d (%s) - $%.2f/night - %s\n",
                        r.roomNumber, r.roomType, r.pricePerNight,
                        r.isBooked ? "Booked by " + r.guestName + " (Phone: " + r.guestPhone + ") for " + r.daysBooked + " days" : "Available"));
                    if (r.isBooked) {
                        sb.append(String.format("  Extra Charges: $%.2f, Estimated Bill: $%.2f\n", 
                            r.extraCharges, r.calculateBill()));
                    }
                }
            }
            roomTextArea.setText(sb.toString());
        });
        
        viewAvailableBtn.addActionListener(event -> {
            StringBuilder sb = new StringBuilder("Available Rooms:\n");
            boolean found = false;
            for (Room r : rooms) {
                if (!r.isBooked) {
                    sb.append(String.format("Room %d - %s - $%.2f/night\n", 
                        r.roomNumber, r.roomType, r.pricePerNight));
                    found = true;
                }
            }
            if (!found) sb.append("No available rooms.\n");
            roomTextArea.setText(sb.toString());
        });
        
        bookRoomBtn.addActionListener(event -> bookRoomDialog(roomTextArea));
        orderFoodBtn.addActionListener(event -> orderFoodDialog(roomTextArea));
        checkoutBtn.addActionListener(event -> checkoutDialog(roomTextArea));
        addRoomBtn.addActionListener(event -> addRoomDialog(roomTextArea));
        
        topPanel.add(viewAllBtn);
        topPanel.add(viewAvailableBtn);
        topPanel.add(bookRoomBtn);
        topPanel.add(orderFoodBtn);
        topPanel.add(checkoutBtn);
        topPanel.add(addRoomBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMultiBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bookMultiBtn = new JButton("Book Multiple Rooms");
        JButton checkLimitBtn = new JButton("Check Guest Booking Limit");
        
        JTextArea multiTextArea = new JTextArea(20, 70);
        multiTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(multiTextArea);
        
        bookMultiBtn.addActionListener(event -> bookMultipleRoomsDialog(multiTextArea));
        checkLimitBtn.addActionListener(event -> checkGuestLimitDialog(multiTextArea));
        
        topPanel.add(bookMultiBtn);
        topPanel.add(checkLimitBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Add instruction panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setBorder(BorderFactory.createTitledBorder("Booking Rules"));
        
        JLabel rule1 = new JLabel("• Maximum 5 rooms per guest");
        JLabel rule2 = new JLabel("• Guest identification by phone number");
        JLabel rule3 = new JLabel("• Different guests can have same name");
        JLabel rule4 = new JLabel("• Phone number must be unique per guest");
        
        instructionPanel.add(rule1);
        instructionPanel.add(rule2);
        instructionPanel.add(rule3);
        instructionPanel.add(rule4);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(instructionPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void bookMultipleRoomsDialog(JTextArea textArea) {
        JTextField guestNameField = new JTextField(20);
        JTextField guestPhoneField = new JTextField(15);
        JTextField daysField = new JTextField(5);
        JTextArea roomsField = new JTextArea(5, 20);
        roomsField.setLineWrap(true);
        JScrollPane roomsScroll = new JScrollPane(roomsField);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Guest Name:"));
        panel.add(guestNameField);
        panel.add(new JLabel("Guest Phone:"));
        panel.add(guestPhoneField);
        panel.add(new JLabel("Days to Book:"));
        panel.add(daysField);
        panel.add(new JLabel("Room Numbers (comma separated):"));
        panel.add(roomsScroll);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Book Multiple Rooms", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String guestName = guestNameField.getText().trim();
                String guestPhone = guestPhoneField.getText().trim();
                int days = Integer.parseInt(daysField.getText());
                String roomsInput = roomsField.getText().trim();
                
                if (guestName.isEmpty() || guestPhone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter guest name and phone!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if guest already has bookings
                int currentBookings = countRoomsByGuest(guestPhone);
                
                // Parse room numbers
                String[] roomStrs = roomsInput.split(",");
                List<Integer> roomNumbers = new ArrayList<>();
                for (String roomStr : roomStrs) {
                    roomStr = roomStr.trim();
                    if (!roomStr.isEmpty()) {
                        roomNumbers.add(Integer.parseInt(roomStr));
                    }
                }
                
                // Check if guest is trying to book more than limit
                if (currentBookings + roomNumbers.size() > MAX_ROOMS_PER_GUEST) {
                    textArea.setText(String.format(
                        "Booking Failed!\nGuest %s (Phone: %s) already has %d rooms booked.\n"
                        + "Trying to book %d more rooms (Total: %d)\n"
                        + "Maximum allowed: %d rooms per guest.\n"
                        + "Please reduce number of rooms.",
                        guestName, guestPhone, currentBookings, roomNumbers.size(), 
                        currentBookings + roomNumbers.size(), MAX_ROOMS_PER_GUEST));
                    return;
                }
                
                // Book each room
                StringBuilder sb = new StringBuilder();
                sb.append("Multiple Room Booking Results:\n\n");
                int successful = 0;
                int failed = 0;
                
                for (int roomNo : roomNumbers) {
                    Room room = findRoom(roomNo);
                    
                    if (room == null) {
                        sb.append(String.format("Room %d: Not found\n", roomNo));
                        failed++;
                        continue;
                    }
                    
                    if (room.isBooked) {
                        sb.append(String.format("Room %d: Already booked by %s\n", 
                            roomNo, room.guestName));
                        failed++;
                        continue;
                    }
                    
                    // Check if room is already booked by this guest (shouldn't happen but check)
                    if (room.guestPhone.equals(guestPhone)) {
                        sb.append(String.format("Room %d: Already booked by this guest\n", roomNo));
                        failed++;
                        continue;
                    }
                    
                    // Book the room
                    room.isBooked = true;
                    room.guestName = guestName;
                    room.guestPhone = guestPhone;
                    room.daysBooked = days;
                    room.extraCharges = 0;
                    successful++;
                    
                    sb.append(String.format("Room %d: Booked successfully\n", roomNo));
                }
                
                sb.append("\n=== Summary ===\n");
                sb.append(String.format("Guest: %s (Phone: %s)\n", guestName, guestPhone));
                sb.append(String.format("Successfully booked: %d rooms\n", successful));
                sb.append(String.format("Failed: %d rooms\n", failed));
                sb.append(String.format("Total rooms booked by this guest: %d/%d\n", 
                    currentBookings + successful, MAX_ROOMS_PER_GUEST));
                
                textArea.setText(sb.toString());
                saveData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please check days and room numbers.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void checkGuestLimitDialog(JTextArea textArea) {
        JTextField guestPhoneField = new JTextField(15);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Enter Guest Phone:"));
        panel.add(guestPhoneField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Check Guest Booking Status", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String guestPhone = guestPhoneField.getText().trim();
            
            if (guestPhone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter guest phone!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<Room> guestRooms = getRoomsByGuest(guestPhone);
            int bookedCount = guestRooms.size();
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== Guest Booking Status ===\n\n");
            sb.append(String.format("Phone: %s\n", guestPhone));
            sb.append(String.format("Rooms Booked: %d/%d\n\n", bookedCount, MAX_ROOMS_PER_GUEST));
            
            if (bookedCount > 0) {
                sb.append("Booked Rooms:\n");
                for (Room room : guestRooms) {
                    sb.append(String.format("  Room %d (%s) - %s - %d days\n", 
                        room.roomNumber, room.roomType, room.guestName, room.daysBooked));
                }
                
                if (bookedCount >= MAX_ROOMS_PER_GUEST) {
                    sb.append("\n⚠️ WARNING: This guest has reached the maximum booking limit!\n");
                    sb.append("Cannot book more rooms.\n");
                } else {
                    sb.append(String.format("\nCan book %d more rooms.\n", 
                        MAX_ROOMS_PER_GUEST - bookedCount));
                }
            } else {
                sb.append("No rooms booked by this guest.\n");
                sb.append(String.format("Can book up to %d rooms.\n", MAX_ROOMS_PER_GUEST));
            }
            
            textArea.setText(sb.toString());
        }
    }
    
    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addEmpBtn = new JButton("Add Employee");
        JButton viewEmpBtn = new JButton("View Employees");
        JButton removeEmpBtn = new JButton("Remove Employee");
        
        JTextArea empTextArea = new JTextArea(20, 70);
        empTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(empTextArea);
        
        addEmpBtn.addActionListener(event -> addEmployeeDialog(empTextArea));
        viewEmpBtn.addActionListener(event -> {
            StringBuilder sb = new StringBuilder();
            if (employees.isEmpty()) {
                sb.append("No employees yet.\n");
            } else {
                for (Employee emp : employees) {
                    sb.append(String.format("%s | Designation: %s | Salary: $%.2f\n",
                        emp.name, emp.designation, emp.salary));
                }
            }
            empTextArea.setText(sb.toString());
        });
        
        removeEmpBtn.addActionListener(event -> removeEmployeeDialog(empTextArea));
        
        topPanel.add(addEmpBtn);
        topPanel.add(viewEmpBtn);
        topPanel.add(removeEmpBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton generateBtn = new JButton("Generate Daily Report");
        JTextArea reportArea = new JTextArea(15, 60);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        
        generateBtn.addActionListener(event -> {
            long booked = rooms.stream().filter(r -> r.isBooked).count();
            double dailyIncome = rooms.stream()
                .filter(r -> r.isBooked)
                .mapToDouble(r -> r.calculateBill() / r.daysBooked)
                .sum();
            
            // Count unique guests
            List<String> guestPhones = new ArrayList<>();
            for (Room r : rooms) {
                if (r.isBooked && !guestPhones.contains(r.guestPhone)) {
                    guestPhones.add(r.guestPhone);
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("==== DAILY REPORT ====\n\n");
            sb.append(String.format("Total Rooms: %d\n", rooms.size()));
            sb.append(String.format("Booked Rooms: %d\n", booked));
            sb.append(String.format("Available Rooms: %d\n", rooms.size() - booked));
            sb.append(String.format("Unique Guests: %d\n", guestPhones.size()));
            sb.append(String.format("Estimated Daily Income: $%.2f\n", dailyIncome));
            sb.append(String.format("Total Income (Cumulative): $%.2f\n", totalIncome));
            sb.append(String.format("Total Employees: %d\n\n", employees.size()));
            
            // Guest booking statistics
            sb.append("Guest Booking Statistics:\n");
            for (String phone : guestPhones) {
                List<Room> guestRooms = getRoomsByGuest(phone);
                if (!guestRooms.isEmpty()) {
                    String guestName = guestRooms.get(0).guestName;
                    sb.append(String.format("  %s (%s): %d rooms\n", 
                        guestName, phone, guestRooms.size()));
                }
            }
            
            sb.append("\nBooked Rooms Details:\n");
            rooms.stream()
                .filter(r -> r.isBooked)
                .forEach(r -> sb.append(String.format(
                    "  Room %d: %s (Booked by %s, Phone: %s for %d days)\n",
                    r.roomNumber, r.roomType, r.guestName, r.guestPhone, r.daysBooked)));
            
            reportArea.setText(sb.toString());
        });
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(generateBtn, gbc);
        
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);
        
        return panel;
    }
    
    // Dialog methods
    private void bookRoomDialog(JTextArea textArea) {
        JTextField roomField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JTextField daysField = new JTextField(5);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Room Number:"));
        panel.add(roomField);
        panel.add(new JLabel("Guest Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Guest Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Days to Book:"));
        panel.add(daysField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Book Room", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int roomNo = Integer.parseInt(roomField.getText());
                String guestName = nameField.getText().trim();
                String guestPhone = phoneField.getText().trim();
                int days = Integer.parseInt(daysField.getText());
                
                if (guestName.isEmpty() || guestPhone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter guest name and phone!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Room room = findRoom(roomNo);
                if (room == null) {
                    JOptionPane.showMessageDialog(this, "Room not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (room.isBooked) {
                    JOptionPane.showMessageDialog(this, 
                        "Room already booked by " + room.guestName, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if guest has reached booking limit
                int currentBookings = countRoomsByGuest(guestPhone);
                if (currentBookings >= MAX_ROOMS_PER_GUEST) {
                    textArea.setText(String.format(
                        "Booking Failed!\nGuest %s (Phone: %s) has already booked %d rooms.\n"
                        + "Maximum allowed: %d rooms per guest.\n"
                        + "Please check out some rooms first.",
                        guestName, guestPhone, currentBookings, MAX_ROOMS_PER_GUEST));
                    return;
                }
                
                room.isBooked = true;
                room.guestName = guestName;
                room.guestPhone = guestPhone;
                room.daysBooked = days;
                room.extraCharges = 0;
                
                double bill = room.calculateBill();
                textArea.setText(String.format(
                    "Room %d booked successfully!\nGuest: %s\nPhone: %s\nDays: %d\n"
                    + "Estimated Bill: $%.2f\n"
                    + "Guest now has %d/%d rooms booked.",
                    roomNo, guestName, guestPhone, days, bill, 
                    currentBookings + 1, MAX_ROOMS_PER_GUEST));
                
                saveData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void orderFoodDialog(JTextArea textArea) {
        JTextField roomField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Room Number:"));
        panel.add(roomField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Order Food", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int roomNo = Integer.parseInt(roomField.getText());
                Room room = findRoom(roomNo);
                
                if (room == null || !room.isBooked) {
                    JOptionPane.showMessageDialog(this, "Invalid or unbooked room!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Food menu selection
                String[] options = {"Burger - $10", "Pizza - $15", "Coffee - $5", "Water - $2"};
                double[] prices = {10, 15, 5, 2};
                
                double total = 0;
                boolean ordering = true;
                
                while (ordering) {
                    int choice = JOptionPane.showOptionDialog(this,
                        "Select food item (Current total: $" + total + ")",
                        "Food Menu",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                    
                    if (choice >= 0 && choice < prices.length) {
                        total += prices[choice];
                        room.extraCharges += prices[choice];
                        
                        int cont = JOptionPane.showConfirmDialog(this,
                            "Add more items? Current total: $" + total,
                            "Continue Ordering?",
                            JOptionPane.YES_NO_OPTION);
                        
                        if (cont != JOptionPane.YES_OPTION) {
                            ordering = false;
                        }
                    } else {
                        ordering = false;
                    }
                }
                
                textArea.setText(String.format(
                    "Food order for Room %d completed!\nGuest: %s\nPhone: %s\nTotal charges: $%.2f\nNew extra charges: $%.2f\nUpdated bill: $%.2f",
                    roomNo, room.guestName, room.guestPhone, total, room.extraCharges, room.calculateBill()));
                
                saveData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid room number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void checkoutDialog(JTextArea textArea) {
        JTextField roomField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Room Number:"));
        panel.add(roomField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Check Out", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int roomNo = Integer.parseInt(roomField.getText());
                Room room = findRoom(roomNo);
                
                if (room == null || !room.isBooked) {
                    JOptionPane.showMessageDialog(this, "Invalid or not booked room!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double bill = room.calculateBill();
                totalIncome += bill;
                
                String guestName = room.guestName;
                String guestPhone = room.guestPhone;
                
                textArea.setText(String.format(
                    "Checkout completed for Room %d!\nGuest: %s\nPhone: %s\nTotal Bill: $%.2f\nTotal Income: $%.2f",
                    roomNo, guestName, guestPhone, bill, totalIncome));
                
                // Reset room
                room.isBooked = false;
                room.guestName = "";
                room.guestPhone = "";
                room.daysBooked = 0;
                room.extraCharges = 0;
                
                saveData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid room number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addRoomDialog(JTextArea textArea) {
        JTextField numField = new JTextField(10);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        JTextField priceField = new JTextField(10);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Room Number:"));
        panel.add(numField);
        panel.add(new JLabel("Room Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Price per Night:"));
        panel.add(priceField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add New Room", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int roomNo = Integer.parseInt(numField.getText());
                
                if (findRoom(roomNo) != null) {
                    JOptionPane.showMessageDialog(this, "Room already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String type = (String) typeCombo.getSelectedItem();
                double price = Double.parseDouble(priceField.getText());
                
                rooms.add(new Room(roomNo, type, price));
                textArea.setText(String.format(
                    "Room added successfully!\nRoom %d - %s - $%.2f/night",
                    roomNo, type, price));
                
                saveData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addEmployeeDialog(JTextArea textArea) {
        JTextField nameField = new JTextField(20);
        JTextField designationField = new JTextField(20);
        JTextField salaryField = new JTextField(10);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Employee Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Designation:"));
        panel.add(designationField);
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String designation = designationField.getText();
                double salary = Double.parseDouble(salaryField.getText());
                
                employees.add(new Employee(name, designation, salary));
                textArea.setText(String.format(
                    "Employee added successfully!\n%s - %s - $%.2f",
                    name, designation, salary));
                
                saveData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid salary!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void removeEmployeeDialog(JTextArea textArea) {
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employees to remove!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] empNames = employees.stream()
            .map(emp -> emp.name)
            .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select employee to remove:",
            "Remove Employee",
            JOptionPane.QUESTION_MESSAGE,
            null,
            empNames,
            empNames[0]);
        
        if (selected != null) {
            boolean removed = employees.removeIf(emp -> emp.name.equals(selected));
            if (removed) {
                textArea.setText("Employee '" + selected + "' removed successfully!");
                saveData();
            }
        }
    }
    
    private void logout() {
        saveData();
        int result = JOptionPane.showConfirmDialog(this,
            "Do you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                HotelManagementSystemGUI newGUI = new HotelManagementSystemGUI();
                LoginWindow loginWindow = new LoginWindow(newGUI);
                loginWindow.setVisible(true);
            });
        }
    }
    
    // Helper methods for guest booking limit
    private int countRoomsByGuest(String guestPhone) {
        int count = 0;
        for (Room room : rooms) {
            if (room.isBooked && room.guestPhone.equals(guestPhone)) {
                count++;
            }
        }
        return count;
    }
    
    private List<Room> getRoomsByGuest(String guestPhone) {
        List<Room> guestRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isBooked && room.guestPhone.equals(guestPhone)) {
                guestRooms.add(room);
            }
        }
        return guestRooms;
    }
    
    // Data methods
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            HotelData data = (HotelData) ois.readObject();
            rooms = data.rooms;
            employees = data.employees;
            totalIncome = data.totalIncome;
        } catch (FileNotFoundException e) {
            // No previous data, start fresh
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            HotelData data = new HotelData(rooms, employees, totalIncome);
            oos.writeObject(data);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initializeRooms() {
        rooms.add(new Room(1001, "Single", 80));
        rooms.add(new Room(1002, "Double", 120));
        rooms.add(new Room(1003, "Suite", 200));
        rooms.add(new Room(1004, "Single", 80));
        rooms.add(new Room(1005, "Double", 120));
        rooms.add(new Room(1006, "Suite", 200));
        rooms.add(new Room(1007, "Single", 80));
        rooms.add(new Room(1008, "Double", 120));
        rooms.add(new Room(1009, "Suite", 200));
        rooms.add(new Room(1010, "Single", 80));
    }
    
    private Room findRoom(int roomNo) {
        for (Room r : rooms) {
            if (r.roomNumber == roomNo) return r;
        }
        return null;
    }
}

// ===== MAIN CLASS =====
public class HotelManagementSystem {
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            HotelManagementSystemGUI mainGUI = new HotelManagementSystemGUI();
            LoginWindow loginWindow = new LoginWindow(mainGUI);
            loginWindow.setVisible(true);
        });
    }
}