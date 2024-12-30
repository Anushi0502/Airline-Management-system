import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AirlineManagementSystem {
    private static final String FLIGHTS_FILE_PATH = "flights.json";
    private static final String BOOKINGS_FILE_PATH = "bookings.json";
    private static final String REGISTRATION_FILE = "registrations.json";
    private static String Name;
    private static List<User> users;
    private static JFrame frame,loginFrame,RegistrationFrame,bookingFrame,availableFlightsFrame,manageBookingsFrame;
    private static List<Flight> availableFlights;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initializeApp();
            loadFlights();
            loadUsers();
        });
    }
    private static void initializeApp() {
        // Create login frame
        loginFrame = createFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel loginPanel = new JPanel(new GridLayout(0, 2, 30, 40));
        customizePanel(loginPanel, Color.WHITE);

        // Username field
        addLabel(loginPanel, "Username:", Color.BLACK,true);
        JTextField usernameField = createTextField(loginPanel, 20,"");

        // Password field
        addLabel(loginPanel, "Password:", Color.BLACK,true);
        JPasswordField passwordField = createPasswordField(loginPanel, 20);
        
        // Login button
        addButton(loginPanel, "Login", Color.BLUE, e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (isValidUser(username, password)) {
                loginFrame.dispose(); // Close login frame
                showMainPage(); // Open main page
            } else {
                showMessage(loginFrame, "Invalid username or password. Please try again.");
            }
        });
        addButton(loginPanel, "Register", Color.RED, e -> {
            loginFrame.setVisible(false);
            initializeRegistrationApp();
        });
        loginFrame.add(loginPanel, BorderLayout.CENTER);
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private static void initializeRegistrationApp() {
        // Create login frame
        RegistrationFrame = createFrame("Registration Portal");
        JPanel RegistrationPanel = new JPanel(new GridLayout(0, 2, 30, 40));
        customizePanel(RegistrationPanel, Color.WHITE);

        // Username field
        addLabel(RegistrationPanel, "Name:", Color.BLACK,true);
        JTextField usernameField = createTextField(RegistrationPanel, 20,"");
        addLabel(RegistrationPanel, "Username:", Color.BLACK,true);
        JTextField emailField = createTextField(RegistrationPanel, 20,"");
        
        // Password field
        addLabel(RegistrationPanel, "Password:", Color.BLACK,true);
        JPasswordField passwordField = createPasswordField(RegistrationPanel, 20);
        
        JLabel label = createLabel("", Color.decode("#95a5a6"));
        
        addButton(RegistrationPanel, "Register", Color.RED, e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            String email = emailField.getText();
            if (users!= null) {
            for (User user : users) {
                if (user.getEmail().equals(username)) {
                    showConfirmation(RegistrationFrame,label,"User already exists Please login.");
                   }
                else{
                    users.add(new User(username, email, password));
                    saveUsers(); // Save the updated list of users
                    showConfirmation(RegistrationFrame,label,"Registration Sucessful.");
                    try {
                        Thread.sleep(1000); // Sleep for 1 second
                    } catch (InterruptedException error) {
                        // Handle interruption if needed
                    }
                    
                    RegistrationFrame.dispose();
                    loginFrame.setVisible(true);   
                    break;
                }
            }}else{
                users = new ArrayList<>();
                users.add(new User(username, email, password));
                saveUsers(); // Save the updated list of users
                showConfirmation(RegistrationFrame,label,"Registration Sucessful.");
                RegistrationFrame.dispose();
                loginFrame.setVisible(true);
            }
        
        });
        
        RegistrationPanel.add(label); // Add label to panel
        RegistrationFrame.add(RegistrationPanel, BorderLayout.CENTER);
        RegistrationFrame.pack();
        RegistrationFrame.setLocationRelativeTo(null);
        RegistrationFrame.setVisible(true);
    }
    private static void showConfirmation(JFrame RegistrationFrame, JLabel label,String text) {
        label.setText(text);
        
    }
    public static boolean isValidUser(String username,String password) {
        for (User user : users) {
            if (user.getEmail().equals(username)) {
                if (user.getPassword().equals(password)) {  
                    Name=user.getname(); 
                return true;//login success
                }
            }
        }
        return false; // login failed
    }
    private static void showMainPage() {
        frame = createFrame("Airline Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        availableFlights = loadFlights(); // Load flights from JSON file

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create main page panel with image buttons on the left side
        JPanel mainPagePanel = createMainPagePanel();
        mainPanel.add(mainPagePanel, BorderLayout.WEST);

        // Create image panel on the right side
        ImageIcon imageIcon = new ImageIcon("right_side.png"); // Replace "right_side.png" with your image path
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBackground(Color.CYAN);
        mainPanel.add(imageLabel, BorderLayout.EAST);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setBackground(Color.CYAN);
        frame.pack(); // Adjust frame size to fit components
        frame.setLocationRelativeTo(null); // Center frame on screen
        frame.setVisible(true); // Show frame
    }

    private static JPanel createMainPagePanel() {
        JPanel mainPagePanel = new JPanel(new GridLayout(0, 1, 60, 20)); // Vertical layout with spacing
        //mainPagePanel.setBackground(Color.WHITE); // Set background color

        // Add image buttons for main page options
        addButton(mainPagePanel, "Book a Flight", "book_ticket.png", e -> openBookingWindow(Name,"", ""));
        addButton(mainPagePanel, "View Available Flights", "view_flight.jpeg", e -> openAvailableFlightsPage());
        addButton(mainPagePanel, "Manage Bookings", "manage_booking.png", e -> openManageBookings());
        addButton(mainPagePanel, "Exit", "exit.jpeg", e -> System.exit(0));

        return mainPagePanel;
    }

    private static JPasswordField createPasswordField(JPanel panel, int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        customizePasswordField(passwordField);
        panel.add(passwordField);
        return passwordField;
    }

    private static void customizePasswordField(JPasswordField passwordField) {
        passwordField.setForeground(Color.black);
        passwordField.setBackground(Color.white);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 36));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.decode("#bdc3c7"))); // Light gray border
        passwordField.setMargin(new Insets(10, 20, 10, 20)); // Add padding
    }


    private static void loadUsers() {
        try (FileReader reader = new FileReader(REGISTRATION_FILE)) {
            Gson gson = new Gson();
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
        } catch (IOException e) {
            e.printStackTrace();
            users = new ArrayList<>();
        }
        }



    private static void saveUsers() {
        try (FileWriter writer = new FileWriter(REGISTRATION_FILE)) {
            Gson gson = new Gson();
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void addButton(JPanel container, String label, String imagePath, ActionListener actionListener) {
        ImageIcon imageIcon = new ImageIcon(imagePath);
        JButton button = new JButton(label, imageIcon);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        customizeButton(button, Color.decode("#3498db"), actionListener);
        container.add(button);
    }
    private static void addButton(JPanel container, String label, Color background , ActionListener actionListener) {
        JButton button = new JButton(label);
        customizeButton(button, background, actionListener); // Blue background
        container.add(button);
    }
    private static void showMessage(JFrame booking, String message) {
        JOptionPane.showMessageDialog(booking, message);
    }

    private static void customizeButton(JButton button, Color background, ActionListener actionListener) {
        button.setFont(new Font("Arial", Font.BOLD, 32));
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        button.addActionListener(actionListener);
    }
    private static void openBookingWindow(String Name,String location, String destination) {
        bookingFrame = createFrame("Flight Booking");
        JPanel bookingPanel = new JPanel(new GridLayout(7, 2, 10, 10)); // Grid layout with spacing
        customizePanel(bookingPanel, Color.cyan); // Light gray background
    
        addLabel(bookingPanel, "Name", Color.decode("#3498db"),false); // Blue label
        JTextField nameField=createTextField(bookingPanel, 20, Name);
    
        addLabel(bookingPanel, "Departure Location", Color.decode("#2ecc71"),false); // Green label
        JTextField deptartureField=createTextField(bookingPanel, 20, location);
    
        addLabel(bookingPanel, "Destination", Color.YELLOW,false); // Red label
        JTextField destinationField=createTextField(bookingPanel, 20, destination);
    
        addLabel(bookingPanel, "Date", Color.blue,false); // Red label
        JTextField dateField = createTextField(bookingPanel, 20, "");
    
        addLabel(bookingPanel, "Class", Color.MAGENTA,false);
        JComboBox<String> classComboBox=createComboBox(bookingPanel);
    
        JLabel label = createLabel("", Color.decode("#95a5a6")); // Gray label
        addButton(bookingPanel, "Book Flight", Color.decode("#2ecc71"), e -> {
            if (!dateField.getText().isEmpty()&& !nameField.getText().isEmpty() && !deptartureField.getText().isEmpty() && !destinationField.getText().isEmpty() ) {
                showBookingConfirmation(bookingFrame, label);
                saveBooking(new Booking(nameField.getText(),deptartureField.getText(),destinationField.getText(),dateField.getText(),(String) classComboBox.getSelectedItem()));
            } else if (dateField.getText().isEmpty()&& !nameField.getText().isEmpty() && !deptartureField.getText().isEmpty() && !destinationField.getText().isEmpty()) {
                showMessage(bookingFrame, "Please enter Date.");
            }else if (nameField.getText().isEmpty()&& !dateField.getText().isEmpty() && !deptartureField.getText().isEmpty() && !destinationField.getText().isEmpty()) {
                showMessage(bookingFrame, "Please enter Name." );
            }else{
                showMessage(bookingFrame, "All fields should be filled out!");
            }}); // Green button
        bookingPanel.add(label); // Add label to panel
        bookingFrame.add(bookingPanel, BorderLayout.CENTER);
        bookingFrame.pack(); // Adjust frame size to fit components
        bookingFrame.setLocationRelativeTo(null); // Center frame on screen
        bookingFrame.setVisible(true);
    }
    private static void BookingdetailsWindow(Booking details) {
        bookingFrame = createFrame("Flight Booking Details");
        JPanel bookingPanel = new JPanel(new GridLayout(7, 2, 10, 10)); // Grid layout with spacing
        customizePanel(bookingPanel, Color.cyan); // Light gray background
    
        addLabel(bookingPanel, "Name", Color.decode("#3498db"),false); // Blue label
        addLabel(bookingPanel, details.getPassengerName(), Color.MAGENTA,true);
    
        addLabel(bookingPanel, "Departure Location", Color.decode("#2ecc71"),false); // Green label
        
        addLabel(bookingPanel, details.getDepartureLocation(), Color.MAGENTA,true);
    
        addLabel(bookingPanel, "Destination", Color.YELLOW,false); // Red label
        addLabel(bookingPanel, details.getDestination(), Color.MAGENTA,true);
    
        addLabel(bookingPanel, "Date", Color.blue,false); // Red label
        addLabel(bookingPanel, details.getDate(), Color.MAGENTA,true);
    
        addLabel(bookingPanel, "Class", Color.MAGENTA,false);
        addLabel(bookingPanel, details.getFlightClass(), Color.MAGENTA,true);
    
        JLabel label = createLabel("Showing Flight Details", Color.decode("#95a5a6")); // Gray label
        addButton(bookingPanel, "Hide Details", Color.decode("#2ecc71"), e-> bookingFrame.setVisible(false)); // Green button
        bookingPanel.add(label); // Add label to panel
        bookingFrame.add(bookingPanel, BorderLayout.CENTER);
        bookingFrame.pack(); // Adjust frame size to fit components
        bookingFrame.setLocationRelativeTo(null); // Center frame on screen
        bookingFrame.setVisible(true);
    }

    private static JTextField createTextField(JPanel panel, int columns, String text) {
        JTextField textField = new JTextField(columns);
        customizeTextField(textField, text);
        panel.add(textField);
        return textField;
    }

    private static void customizeTextField(JTextField textField, String text) {
        textField.setForeground(Color.BLACK);
        textField.setBackground(Color.WHITE);
        textField.setFont(new Font("Arial", Font.PLAIN, 36));
        textField.setText(text);
        textField.setBorder(BorderFactory.createLineBorder(Color.decode("#bdc3c7")));
        textField.setMargin(new Insets(10, 20, 10, 20));
    }

    private static JComboBox<String> createComboBox(JPanel panel) {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Economy", "Business"});
        customizeComboField(comboBox);
        panel.add(comboBox);
        return comboBox;
    }

    private static void customizeComboField(JComboBox<String> comboBox) {
        comboBox.setForeground(Color.BLACK);
        comboBox.setBackground(Color.WHITE);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 36));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.decode("#bdc3c7")));
    }

    private static void showBookingConfirmation(JFrame booking, JLabel label) {
        label.setText("Flight booking successful.");
        
    }

    private static void customizePanel(JPanel panel, Color background) {
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private static JLabel createLabel(String text, Color background) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(background);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    private static void openAvailableFlightsPage() {
        availableFlightsFrame = createFrame("Available Flights");
        JPanel flightsPanel = new JPanel(new GridLayout(5, 2, 40, 40));
        flightsPanel.setBackground(Color.WHITE);

        for (Flight flight : availableFlights) {
            addButton(flightsPanel, flight.getName(), flight.getImagePath(), e -> {availableFlightsFrame.dispose();
                openBookingWindow(Name,flight.getLocation(), flight.getDestination());
            });
        }

        availableFlightsFrame.add(new JScrollPane(flightsPanel), BorderLayout.CENTER);
        availableFlightsFrame.pack();
        availableFlightsFrame.setLocationRelativeTo(null);
        availableFlightsFrame.setVisible(true);
    }

    private static void addLabel(JPanel container, String text, Color foreground,boolean condition) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 38));
        label.setForeground(foreground);
        label.setOpaque(true);
        if (!condition){
        label.setBackground(Color.RED);}
        else{label.setBackground(Color.decode("#3498db"));}
        label.setHorizontalAlignment(SwingConstants.CENTER);
        container.add(label);
    }

    private static JFrame createFrame(String title) {
        JFrame fullScreenFrame = new JFrame(title);
        fullScreenFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return fullScreenFrame;
    }

    private static void openManageBookings() {
        manageBookingsFrame = createFrame("Manage Bookings");

        // Load bookings
        List<Booking> bookings = loadBookings();

        JPanel bookingsPanel = new JPanel(new GridLayout(bookings.size(), 1, 10, 10));
        bookingsPanel.setBackground(Color.WHITE);

        for (Booking booking : bookings) {
            JPanel bookingPanel = new JPanel(new GridLayout(1, 2));
            addButton(bookingPanel, booking.getFlightName(), Color.decode("#2ecc71"), e-> BookingdetailsWindow(booking));            
            bookingPanel.setBackground(Color.LIGHT_GRAY);
            addButton(bookingPanel, "Cancel Booking", Color.decode("#3498db"),new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Remove booking
                    bookings.remove(booking);
                    saveBookings(bookings);
                    // Update UI
                    bookingPanel.setVisible(false);
                    bookingsPanel.remove(bookingPanel);
                    manageBookingsFrame.revalidate();
                    manageBookingsFrame.repaint();
                    JOptionPane.showMessageDialog(manageBookingsFrame, "Booking canceled successfully.");
                }
            });
            bookingsPanel.add(bookingPanel);
        }

        JScrollPane scrollPane = new JScrollPane(bookingsPanel);
        manageBookingsFrame.add(scrollPane);
        manageBookingsFrame.pack();
        manageBookingsFrame.setLocationRelativeTo(null);
        manageBookingsFrame.setVisible(true);
    }

    private static List<Flight> loadFlights() {
        try (FileReader reader = new FileReader(FLIGHTS_FILE_PATH)) {
            Gson gson = new Gson();
            Type flightListType = new TypeToken<List<Flight>>() {}.getType();
            availableFlights = gson.fromJson(reader, flightListType);
            if (availableFlights == null) {
                availableFlights = new ArrayList<>();
                addFlights();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableFlights;
    }
    private static void addFlights(){
        saveFlight(new Flight("New Delhi to Chennai", "New Delhi", "Chennai", "flight1.jpeg"));
        saveFlight(new Flight("Chennai to Mumbai", "Chennai", "Mumbai", "flight2.jpeg")); // Replace "flight2.jpg" with image path
        saveFlight(new Flight("Ramapuram to Goa", "Ramapuram", "Goa", "flight3.jpeg"));
        saveFlight(new Flight("Shimla to New Delhi", "Shimla", "New Delhi", "flight4.jpeg"));
        saveFlight(new Flight("Pune to New Delhi", "Pune", "New Delhi", "flight4.jpeg"));
        saveFlight(new Flight("Shimla to Jammu", "Shimla", "Jammu", "flight5.jpeg"));
        saveFlight(new Flight("Goa to New Delhi", "Goa", "New Delhi", "flight4.jpeg"));
        saveFlight(new Flight("Shimla to Chennai", "Shimla", "Chennai", "flight1.jpeg"));
        saveFlight(new Flight("Pune to Pathankot", "Pune", "Pathankot", "flight6.jpeg"));
    }
    private static void saveFlight(Flight flight) {
        availableFlights.add(flight);
        saveFlights(availableFlights);
    }

    private static void saveFlights(List<Flight> flights) {
        try (Writer writer = new FileWriter(FLIGHTS_FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(flights, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Booking> loadBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (FileReader reader = new FileReader(BOOKINGS_FILE_PATH)) {
            Gson gson = new Gson();
            Type bookingListType = new TypeToken<List<Booking>>() {}.getType();
            bookings = gson.fromJson(reader, bookingListType);
            if (bookings == null) {
                bookings = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    private static void saveBooking(Booking booking) {
        List<Booking> bookings = loadBookings();
        bookings.add(booking);
        saveBookings(bookings);
    }

    private static void saveBookings(List<Booking> bookings) {
        try (Writer writer = new FileWriter(BOOKINGS_FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(bookings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class User {
    private String username;
    private String name;
    private String password;

    public User(String username, String email, String password) {
        this.name = username;
        this.username = email;
        this.password = password;
    }

    // Getters and setters
    public String getname() {
        return name;
    }

    public String getEmail() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class Flight {
    private String name;
    private String location;
    private String destination;
    private String imagePath;

    public Flight(String name, String location, String destination, String imagePath) {
        this.name = name;
        this.location = location;
        this.destination = destination;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDestination() {
        return destination;
    }

    public String getImagePath() {
        return imagePath;
    }
}

class Booking {
    private String passengerName;
    private String departureLocation;
    private String destination;
    private String date;
    private String flightClass;

    public Booking(String passengerName, String departureLocation, String destination, String date, String flightClass) {
        this.passengerName = passengerName;
        this.departureLocation = departureLocation;
        this.destination = destination;
        this.date = date;
        this.flightClass = flightClass;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public String getFlightClass() {
        return flightClass;
    }

    public String getFlightName() {
        return departureLocation + " to " + destination;
    }
}
