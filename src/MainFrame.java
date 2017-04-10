package src;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    
    private SlidePanel sp = new SlidePanel();
    private JDatePickerImpl returnPicker;
    private JDatePickerImpl departPicker;
    private int xMouse;
    private int yMouse;
    private int travelersValue;
    
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
    
    private FlightMockGenerator flightMock;
    
    public MainFrame() {
        initComponents();
        //adjustFrame();
        
        //airportCodes.txt should be in the same directory as the 'src' folder.
        flightMock = new FlightMockGenerator("airportCodes.txt");  
        generateComboboxModel();  
        calendar();
        comboBox();
        flight_label.setFocusable(true);
    }
    
    private void searchFlights() {
        Date departure_date = null;
        Date arrival_date = null;
        String origin = "";
        String destination = "";
        try {
            departure_date = getDepartDate();
            arrival_date = getReturnDate();
            origin = (String) jDepartureComboBox.getSelectedItem();
            destination = (String) jLocationComboBox.getSelectedItem();
            List<Flight> outbound = flightMock.search(departure_date, origin, destination, travelersValue);
            List<Flight> inbound = flightMock.search(arrival_date, destination, origin, travelersValue);
            displayFlightResult(inbound, outbound);
            scrollToDate(inbound_list, arrival_date);
            scrollToDate(outbound_list, departure_date);
            slideLeft();
        } catch (NullPointerException e) {
            System.out.println("Fill in all inputs");
        }
    }
    
    private void generateComboboxModel() {
        DefaultComboBoxModel dbm = new DefaultComboBoxModel();
        List<Airport> airports = flightMock.getAirports();
        Collections.sort(airports);
        for (Airport ap : airports) {
            String name = ap.getName() + " (" + ap.getAirportCode() + "), " + ap.getCountry();
            dbm.addElement(name);
        }
        jDepartureComboBox.setModel(dbm);
    }
    
    private void displayFlightResult(List<Flight> inbound_flights, List<Flight> outbound_flights) {
        DefaultListModel inbound = new DefaultListModel();
        DefaultListModel outbound = new DefaultListModel();
        addToListModel(inbound_flights, inbound);
        addToListModel(outbound_flights, outbound);
        setFlightLabel();
        inbound_list.setModel(inbound);
        outbound_list.setModel(outbound);
    }
    
    private void setFlightLabel() {
        String  t = "<font color='rgb(153,51,0'>  to  </font>";
        String from = (String) jDepartureComboBox.getSelectedItem();
        String to = (String) jLocationComboBox.getSelectedItem();
        
        outbound_flight.setText("<html>" + from + t + to + "</html>");
        inbound_flight.setText("<html>" + to + t + from + "</html>");
    }
    
    public void scrollToDate(JList list, Date date) {
        int index = getFlightIndexAt(date, (DefaultListModel) list.getModel());
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }
    
    public int getFlightIndexAt(Date d, DefaultListModel dl) {
        for (int i = 0; i < dl.size(); i++) {
            if (fmt.format(((Flight) dl.get(i)).getDepartureTime()).equals(fmt.format(d))) {
                return i;
            }
        }
        return -1;
    }
    
    private void addToListModel(List<?> item, DefaultListModel  dm) {
        for (int i = 0; i < item.size(); i++) {
            dm.addElement(item.get(i));
        }
    }
    
    private void adjustFrame(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }
    
    private void calendar(){
        UtilDateModel model = new UtilDateModel();
        UtilDateModel model2 = new UtilDateModel();

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        departPicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, p);
        returnPicker = new JDatePickerImpl(datePanel2, new DateLabelFormatter());
               
    
        departPicker.setBounds(0,0,150,40);
        returnPicker.setBounds(0,0,150,40);
        jDepartingCalendar.add(departPicker);
        jReturningCalendar.add(returnPicker);
    }
    
    private void comboBox(){
        JTextField departing = (JTextField)jDepartureComboBox.getEditor().getEditorComponent();
        JTextField returning = (JTextField)jLocationComboBox.getEditor().getEditorComponent();
        departing.addKeyListener(new ComboKeyHandler(jDepartureComboBox));
        returning.addKeyListener(new ComboKeyHandler(jLocationComboBox));
        changeScrollBarDimension(jDepartureComboBox, 7);
        changeScrollBarDimension(jLocationComboBox, 7);
    }

    private void changeScrollBarDimension(JComboBox<String> cb, int width) {
        Object popup = cb.getUI().getAccessibleChild(cb, 0);
        Component c = ((Container) popup).getComponent(0);
        if (c instanceof JScrollPane) {
            JScrollPane scrollpane = (JScrollPane) c;
            JScrollBar scrollBar = scrollpane.getVerticalScrollBar();
            Dimension scrollBarDim = new Dimension(width, scrollBar.getPreferredSize().height);
            scrollBar.setPreferredSize(scrollBarDim);
        }
    }
    
    private static void changeLF(String lf) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (lf.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error :" + e.getMessage());
        }
    }
    
    private void change_preference_tab(JPanel a){
        //Removing old panel
        preference_container.removeAll();
        //Adding new one
        preference_container.add(a);
        preference_container.repaint();
    }
    
    private void slideScrollBar(JScrollPane spane, int increment) {
        int current = spane.getHorizontalScrollBar().getValue();
        spane.getHorizontalScrollBar().setValue(current + increment);
    }
    
    private void slideLeft() {
        sp.slideLeft(result_panel.getX() - result_panel.getWidth(), 10, 20, result_panel);
        sp.slideLeft(preference_panel.getX() - preference_panel.getWidth(), 10, 20, preference_panel);
    }
    
    private void slideRight() {
        sp.slideRight(result_panel.getX() + result_panel.getWidth(), 10, 20, result_panel);
        sp.slideRight(preference_panel.getX() + preference_panel.getWidth(), 10, 20, preference_panel);
    }
    
    private Date getDepartDate() {
        return (Date) departPicker.getModel().getValue();
    }
    
    private Date getReturnDate() {
        return (Date) returnPicker.getModel().getValue();
    }
    
    private void setTravelersValue(int n) {
        this.travelersValue = n;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        container = new javax.swing.JPanel();
        result_panel = new javax.swing.JPanel();
        menuBar1 = new javax.swing.JPanel();
        jExit1 = new javax.swing.JLabel();
        flight_label1 = new javax.swing.JLabel();
        hotel_label1 = new javax.swing.JLabel();
        day_tour_label1 = new javax.swing.JLabel();
        back_label = new javax.swing.JLabel();
        result_container = new javax.swing.JPanel();
        flight_result = new javax.swing.JPanel();
        outbound_flight = new javax.swing.JLabel();
        inbound_title = new javax.swing.JLabel();
        outbound_scrollpane = new javax.swing.JScrollPane();
        outbound_list = new javax.swing.JList<>();
        outbound_back = new javax.swing.JLabel();
        outbound_forward = new javax.swing.JLabel();
        outbound_title = new javax.swing.JLabel();
        inbound_scrollpane = new javax.swing.JScrollPane();
        inbound_list = new javax.swing.JList<>();
        inbound_back = new javax.swing.JLabel();
        inbound_flight = new javax.swing.JLabel();
        inbound_forward = new javax.swing.JLabel();
        hotel_result = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        preference_panel = new javax.swing.JPanel();
        menuBar = new javax.swing.JPanel();
        jExit = new javax.swing.JLabel();
        jInstruction = new javax.swing.JLabel();
        jAbout = new javax.swing.JLabel();
        jContact = new javax.swing.JLabel();
        preference_tab = new javax.swing.JPanel();
        flight_label = new javax.swing.JLabel();
        hotel_label = new javax.swing.JLabel();
        day_tour_label = new javax.swing.JLabel();
        search_label = new javax.swing.JLabel();
        preference_container = new javax.swing.JPanel();
        flight_preference = new javax.swing.JPanel();
        jTitle = new javax.swing.JLabel();
        jDepartureLabel = new javax.swing.JLabel();
        jLocationLabel = new javax.swing.JLabel();
        jTravelerLabel = new javax.swing.JLabel();
        changeLF("Windows");
        jTravelerSpinner = new javax.swing.JSpinner();
        jDepartingLabel = new javax.swing.JLabel();
        jReturningLabel = new javax.swing.JLabel();
        jDepartingCalendar = new javax.swing.JPanel();
        jReturningCalendar = new javax.swing.JPanel();
        changeLF("Windows");
        jLocationComboBox = new javax.swing.JComboBox<>();
        changeLF("Windows");
        jDepartureComboBox = new javax.swing.JComboBox<>();
        switch_label = new javax.swing.JLabel();
        jHotelSelect = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jToursSelect = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        container.setBackground(new java.awt.Color(255, 255, 255));
        container.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(51, 51, 51)));
        container.setPreferredSize(new java.awt.Dimension(1215, 735));
        container.setLayout(null);

        result_panel.setBackground(new java.awt.Color(255, 255, 255));
        result_panel.setBorder(new javax.swing.border.MatteBorder(null));
        result_panel.setLayout(null);

        menuBar1.setBackground(new java.awt.Color(51, 51, 51));
        menuBar1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                menuBar1MouseDragged(evt);
            }
        });
        menuBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                menuBar1MousePressed(evt);
            }
        });

        jExit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Multiply_64px_1.png"))); // NOI18N
        jExit1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jExit1MouseClicked(evt);
            }
        });

        flight_label1.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        flight_label1.setForeground(new java.awt.Color(250, 250, 250));
        flight_label1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/flight-light-icon.png"))); // NOI18N
        flight_label1.setText("Flight");
        flight_label1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                flight_label1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                flight_label1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                flight_label1MousePressed(evt);
            }
        });

        hotel_label1.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        hotel_label1.setForeground(new java.awt.Color(250, 250, 250));
        hotel_label1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/hotel-light-icon.png"))); // NOI18N
        hotel_label1.setText("Hotel");
        hotel_label1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hotel_label1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hotel_label1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                hotel_label1MousePressed(evt);
            }
        });

        day_tour_label1.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        day_tour_label1.setForeground(new java.awt.Color(250, 250, 250));
        day_tour_label1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/day-tour-light-icon.png"))); // NOI18N
        day_tour_label1.setText("Day Tours");
        day_tour_label1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                day_tour_label1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                day_tour_label1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                day_tour_label1MousePressed(evt);
            }
        });

        back_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/back-button-icon.png"))); // NOI18N
        back_label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        back_label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                back_labelMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout menuBar1Layout = new javax.swing.GroupLayout(menuBar1);
        menuBar1.setLayout(menuBar1Layout);
        menuBar1Layout.setHorizontalGroup(
            menuBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuBar1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(back_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
                .addComponent(flight_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
                .addComponent(hotel_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101)
                .addComponent(day_tour_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(121, 121, 121)
                .addComponent(jExit1)
                .addGap(27, 27, 27))
        );
        menuBar1Layout.setVerticalGroup(
            menuBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuBar1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuBar1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(back_label))
                    .addGroup(menuBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(flight_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hotel_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(day_tour_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jExit1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        result_panel.add(menuBar1);
        menuBar1.setBounds(0, 0, 1220, 90);

        result_container.setBackground(new java.awt.Color(255, 255, 255));
        result_container.setLayout(new java.awt.CardLayout());

        flight_result.setBackground(new java.awt.Color(240, 241, 241));

        outbound_flight.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        outbound_flight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/departure-13x13-icon.png"))); // NOI18N
        outbound_flight.setText("Keflavík (KEF)  to London (LHR)");

        inbound_title.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        inbound_title.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrival-icon.png"))); // NOI18N
        inbound_title.setText("  Select inbound flight");

        outbound_list.setFixedCellWidth(160);
        outbound_list.setVisibleRowCount(1);
        outbound_scrollpane.setViewportView(outbound_list);
        outbound_list.setBorder(BorderFactory.createEmptyBorder());
        outbound_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        outbound_list.setCellRenderer(new FlightResultRenderer());
        outbound_list.setVisibleRowCount(1);

        outbound_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow-left-black.png"))); // NOI18N
        outbound_back.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        outbound_back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                outbound_backMousePressed(evt);
            }
        });

        outbound_forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow-right-black.png"))); // NOI18N
        outbound_forward.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        outbound_forward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                outbound_forwardMousePressed(evt);
            }
        });

        outbound_title.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        outbound_title.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/departure-icon.png"))); // NOI18N
        outbound_title.setText("  Select outbound flight");

        inbound_list.setFixedCellWidth(160);
        inbound_list.setVisibleRowCount(1);
        inbound_scrollpane.setViewportView(inbound_list);
        inbound_list.setBorder(BorderFactory.createEmptyBorder());
        inbound_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        inbound_list.setCellRenderer(new FlightResultRenderer());
        inbound_list.setVisibleRowCount(1);

        inbound_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow-left-black.png"))); // NOI18N
        inbound_back.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        inbound_back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                inbound_backMousePressed(evt);
            }
        });

        inbound_flight.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        inbound_flight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/departure-13x13-icon.png"))); // NOI18N
        inbound_flight.setText("Keflavík (KEF)  to London (LHR)");

        inbound_forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow-right-black.png"))); // NOI18N
        inbound_forward.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        inbound_forward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                inbound_forwardMousePressed(evt);
            }
        });

        javax.swing.GroupLayout flight_resultLayout = new javax.swing.GroupLayout(flight_result);
        flight_result.setLayout(flight_resultLayout);
        flight_resultLayout.setHorizontalGroup(
            flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(flight_resultLayout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(inbound_title, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, flight_resultLayout.createSequentialGroup()
                .addGap(0, 61, Short.MAX_VALUE)
                .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(flight_resultLayout.createSequentialGroup()
                        .addComponent(inbound_back)
                        .addGap(42, 42, 42)
                        .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(inbound_flight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inbound_scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE))
                        .addGap(33, 33, 33)
                        .addComponent(inbound_forward))
                    .addGroup(flight_resultLayout.createSequentialGroup()
                        .addComponent(outbound_back)
                        .addGap(42, 42, 42)
                        .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(flight_resultLayout.createSequentialGroup()
                                .addComponent(outbound_scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 971, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(outbound_forward))
                            .addComponent(outbound_flight, javax.swing.GroupLayout.PREFERRED_SIZE, 971, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(78, 78, 78))
            .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(flight_resultLayout.createSequentialGroup()
                    .addGap(84, 84, 84)
                    .addComponent(outbound_title, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(824, Short.MAX_VALUE)))
        );
        flight_resultLayout.setVerticalGroup(
            flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, flight_resultLayout.createSequentialGroup()
                .addContainerGap(114, Short.MAX_VALUE)
                .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, flight_resultLayout.createSequentialGroup()
                        .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(outbound_back, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(outbound_forward, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(124, 124, 124))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, flight_resultLayout.createSequentialGroup()
                        .addComponent(outbound_flight, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outbound_scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73)))
                .addComponent(inbound_title, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(flight_resultLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(inbound_flight, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(inbound_scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(flight_resultLayout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(inbound_back))
                    .addGroup(flight_resultLayout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(inbound_forward)))
                .addGap(53, 53, 53))
            .addGroup(flight_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(flight_resultLayout.createSequentialGroup()
                    .addGap(48, 48, 48)
                    .addComponent(outbound_title, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(552, Short.MAX_VALUE)))
        );

        outbound_flight.setHorizontalAlignment(JLabel.CENTER);
        outbound_scrollpane.getViewport().setOpaque(false);
        outbound_scrollpane.setViewportBorder(null);
        outbound_scrollpane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
        inbound_scrollpane.getViewport().setOpaque(false);
        inbound_scrollpane.setViewportBorder(null);
        inbound_scrollpane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
        inbound_flight.setHorizontalAlignment(JLabel.CENTER);

        result_container.add(flight_result, "card2");

        jLabel5.setText("jLabel5");

        javax.swing.GroupLayout hotel_resultLayout = new javax.swing.GroupLayout(hotel_result);
        hotel_result.setLayout(hotel_resultLayout);
        hotel_resultLayout.setHorizontalGroup(
            hotel_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hotel_resultLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(979, Short.MAX_VALUE))
        );
        hotel_resultLayout.setVerticalGroup(
            hotel_resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hotel_resultLayout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(125, Short.MAX_VALUE))
        );

        result_container.add(hotel_result, "card3");

        result_panel.add(result_container);
        result_container.setBounds(0, 90, 1215, 645);

        container.add(result_panel);
        result_panel.setBounds(1215, 0, 1215, 735);

        preference_panel.setBackground(new java.awt.Color(255, 255, 255));
        preference_panel.setBorder(new javax.swing.border.MatteBorder(null));
        preference_panel.setLayout(null);

        menuBar.setBackground(new java.awt.Color(51, 51, 51));
        menuBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                menuBarMouseDragged(evt);
            }
        });
        menuBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                menuBarMousePressed(evt);
            }
        });

        jExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Multiply_64px_1.png"))); // NOI18N
        jExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jExitMouseClicked(evt);
            }
        });

        jInstruction.setBackground(new java.awt.Color(255, 255, 255));
        jInstruction.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        jInstruction.setForeground(new java.awt.Color(255, 255, 255));
        jInstruction.setText("Instruction");

        jAbout.setFont(new java.awt.Font("Segoe UI Light", 0, 24)); // NOI18N
        jAbout.setForeground(new java.awt.Color(255, 255, 255));
        jAbout.setText("About");

        jContact.setBackground(new java.awt.Color(255, 255, 255));
        jContact.setFont(new java.awt.Font("Segoe UI Light", 0, 24)); // NOI18N
        jContact.setForeground(new java.awt.Color(255, 255, 255));
        jContact.setText("Contact");

        javax.swing.GroupLayout menuBarLayout = new javax.swing.GroupLayout(menuBar);
        menuBar.setLayout(menuBarLayout);
        menuBarLayout.setHorizontalGroup(
            menuBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuBarLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jInstruction, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(jAbout, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(jContact, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jExit)
                .addGap(27, 27, 27))
        );
        menuBarLayout.setVerticalGroup(
            menuBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuBarLayout.createSequentialGroup()
                .addGroup(menuBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuBarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jExit))
                    .addGroup(menuBarLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(menuBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jContact)
                            .addComponent(jAbout)
                            .addComponent(jInstruction))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        preference_panel.add(menuBar);
        menuBar.setBounds(0, 0, 1220, 90);

        flight_label.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        flight_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/flight-dark-icon.png"))); // NOI18N
        flight_label.setText("Flight");
        flight_label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                flight_labelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                flight_labelMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                flight_labelMousePressed(evt);
            }
        });

        hotel_label.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        hotel_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/hotel-dark-icon.png"))); // NOI18N
        hotel_label.setText("Hotel");
        hotel_label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hotel_labelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hotel_labelMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                hotel_labelMousePressed(evt);
            }
        });

        day_tour_label.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        day_tour_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/day-tour-dark-icon.png"))); // NOI18N
        day_tour_label.setText("Day Tours");
        day_tour_label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                day_tour_labelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                day_tour_labelMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                day_tour_labelMousePressed(evt);
            }
        });

        search_label.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        search_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search-icon.png"))); // NOI18N
        search_label.setText("Search  ");
        search_label.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        search_label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        search_label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                search_labelMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout preference_tabLayout = new javax.swing.GroupLayout(preference_tab);
        preference_tab.setLayout(preference_tabLayout);
        preference_tabLayout.setHorizontalGroup(
            preference_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preference_tabLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(preference_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(search_label, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(day_tour_label, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hotel_label, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flight_label, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        preference_tabLayout.setVerticalGroup(
            preference_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preference_tabLayout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(flight_label, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(hotel_label, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(day_tour_label, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                .addComponent(search_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );

        search_label.setHorizontalTextPosition(SwingConstants.LEFT);

        preference_panel.add(preference_tab);
        preference_tab.setBounds(0, 90, 260, 643);

        preference_container.setBackground(new java.awt.Color(255, 255, 255));
        preference_container.setLayout(new java.awt.CardLayout());

        flight_preference.setBackground(new java.awt.Color(255, 255, 255));

        jTitle.setFont(new java.awt.Font("Segoe UI Semibold", 1, 30)); // NOI18N
        jTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/flight-dark-20x20-icon.png"))); // NOI18N
        jTitle.setText("Book your flight   ");

        jDepartureLabel.setFont(new java.awt.Font("Segoe UI Light", 1, 22)); // NOI18N
        jDepartureLabel.setText("From");

        jLocationLabel.setFont(new java.awt.Font("Segoe UI Light", 1, 22)); // NOI18N
        jLocationLabel.setText("To");

        jTravelerLabel.setFont(new java.awt.Font("Segoe UI Light", 1, 22)); // NOI18N
        jTravelerLabel.setText("Traveler");

        jTravelerSpinner.setFont(new java.awt.Font("Segoe UI Light", 0, 16)); // NOI18N
        jTravelerSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        changeLF("Nimbus");
        jTravelerSpinner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTravelerSpinnerKeyPressed(evt);
            }
        });

        jDepartingLabel.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jDepartingLabel.setText("Departing");

        jReturningLabel.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jReturningLabel.setText("Returning");

        jDepartingCalendar.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jDepartingCalendarLayout = new javax.swing.GroupLayout(jDepartingCalendar);
        jDepartingCalendar.setLayout(jDepartingCalendarLayout);
        jDepartingCalendarLayout.setHorizontalGroup(
            jDepartingCalendarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 142, Short.MAX_VALUE)
        );
        jDepartingCalendarLayout.setVerticalGroup(
            jDepartingCalendarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 34, Short.MAX_VALUE)
        );

        jReturningCalendar.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jReturningCalendarLayout = new javax.swing.GroupLayout(jReturningCalendar);
        jReturningCalendar.setLayout(jReturningCalendarLayout);
        jReturningCalendarLayout.setHorizontalGroup(
            jReturningCalendarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jReturningCalendarLayout.setVerticalGroup(
            jReturningCalendarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 34, Short.MAX_VALUE)
        );

        jLocationComboBox.setEditable(true);
        jLocationComboBox.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLocationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Keflavik International Airport (KEF), Iceland" }));
        jLocationComboBox.setBorder(null);

        jDepartureComboBox.setEditable(true);
        jDepartureComboBox.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jDepartureComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bahamas, The", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burma", "Burundi", "Cambodia", "Cameroon", "Canada", "Cabo Verde", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo, Democratic Republic of the", "Congo, Republic of the", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Curacao", "Cyprus", "Czechia" }));
        jDepartureComboBox.setSelectedIndex(-1);
        jDepartureComboBox.setBorder(null);

        switch_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/switch-icon.png"))); // NOI18N
        switch_label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout flight_preferenceLayout = new javax.swing.GroupLayout(flight_preference);
        flight_preference.setLayout(flight_preferenceLayout);
        flight_preferenceLayout.setHorizontalGroup(
            flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(flight_preferenceLayout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(flight_preferenceLayout.createSequentialGroup()
                        .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDepartureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTitle))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(flight_preferenceLayout.createSequentialGroup()
                        .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(flight_preferenceLayout.createSequentialGroup()
                                .addComponent(jDepartingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDepartingCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jDepartureComboBox, 0, 0, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addComponent(switch_label, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLocationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(flight_preferenceLayout.createSequentialGroup()
                                .addComponent(jReturningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jReturningCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                        .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(flight_preferenceLayout.createSequentialGroup()
                                .addComponent(jTravelerSpinner)
                                .addGap(28, 28, 28))
                            .addComponent(jTravelerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(74, 74, 74))))
        );
        flight_preferenceLayout.setVerticalGroup(
            flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(flight_preferenceLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLocationLabel)
                    .addComponent(jDepartureLabel)
                    .addComponent(jTravelerLabel))
                .addGap(18, 18, 18)
                .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDepartureComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(jTravelerSpinner)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, flight_preferenceLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(switch_label, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLocationComboBox, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(110, 110, 110)
                .addGroup(flight_preferenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDepartingCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jReturningCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jReturningLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jDepartingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(128, 128, 128))
        );

        jTitle.setHorizontalTextPosition(SwingConstants.LEFT);
        JComponent comp = jTravelerSpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        jTravelerSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setTravelersValue((int)jTravelerSpinner.getValue());
            }
        });
        changeLF("Nimbus");
        changeLF("Nimbus");

        preference_container.add(flight_preference, "card2");

        jHotelSelect.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel2.setText("Hotel design something here....");

        javax.swing.GroupLayout jHotelSelectLayout = new javax.swing.GroupLayout(jHotelSelect);
        jHotelSelect.setLayout(jHotelSelectLayout);
        jHotelSelectLayout.setHorizontalGroup(
            jHotelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jHotelSelectLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(478, Short.MAX_VALUE))
        );
        jHotelSelectLayout.setVerticalGroup(
            jHotelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jHotelSelectLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel2)
                .addContainerGap(478, Short.MAX_VALUE))
        );

        preference_container.add(jHotelSelect, "card3");

        jToursSelect.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel3.setText("Tours design something here....");

        javax.swing.GroupLayout jToursSelectLayout = new javax.swing.GroupLayout(jToursSelect);
        jToursSelect.setLayout(jToursSelectLayout);
        jToursSelectLayout.setHorizontalGroup(
            jToursSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jToursSelectLayout.createSequentialGroup()
                .addGap(151, 151, 151)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(551, Short.MAX_VALUE))
        );
        jToursSelectLayout.setVerticalGroup(
            jToursSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jToursSelectLayout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(369, Short.MAX_VALUE))
        );

        preference_container.add(jToursSelect, "card4");

        preference_panel.add(preference_container);
        preference_container.setBounds(260, 91, 950, 550);

        container.add(preference_panel);
        preference_panel.setBounds(1, 1, 1213, 733);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(container, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1215, 735));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jExitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_jExitMouseClicked

    private void menuBarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuBarMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x-xMouse, y-yMouse);
    }//GEN-LAST:event_menuBarMouseDragged

    private void menuBarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuBarMousePressed
        xMouse= evt.getX();
        yMouse= evt.getY();
    }//GEN-LAST:event_menuBarMousePressed

    private void flight_labelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flight_labelMouseEntered
        flight_label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
    }//GEN-LAST:event_flight_labelMouseEntered

    private void flight_labelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flight_labelMouseExited
        flight_label.setBorder(BorderFactory.createEmptyBorder());
    }//GEN-LAST:event_flight_labelMouseExited

    private void hotel_labelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hotel_labelMouseEntered
        hotel_label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
    }//GEN-LAST:event_hotel_labelMouseEntered

    private void hotel_labelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hotel_labelMouseExited
        hotel_label.setBorder(BorderFactory.createEmptyBorder());
    }//GEN-LAST:event_hotel_labelMouseExited

    private void day_tour_labelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_day_tour_labelMouseEntered
        day_tour_label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
    }//GEN-LAST:event_day_tour_labelMouseEntered

    private void day_tour_labelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_day_tour_labelMouseExited
        day_tour_label.setBorder(BorderFactory.createEmptyBorder());
    }//GEN-LAST:event_day_tour_labelMouseExited

    private void flight_labelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flight_labelMousePressed
        change_preference_tab(flight_preference);
    }//GEN-LAST:event_flight_labelMousePressed

    private void hotel_labelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hotel_labelMousePressed
        change_preference_tab(jHotelSelect);
    }//GEN-LAST:event_hotel_labelMousePressed

    private void day_tour_labelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_day_tour_labelMousePressed
        change_preference_tab(jToursSelect);
    }//GEN-LAST:event_day_tour_labelMousePressed

    private void jExit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jExit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jExit1MouseClicked

    private void menuBar1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuBar1MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x-xMouse, y-yMouse);
    }//GEN-LAST:event_menuBar1MouseDragged

    private void menuBar1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuBar1MousePressed
        xMouse= evt.getX();
        yMouse= evt.getY();
    }//GEN-LAST:event_menuBar1MousePressed

    private void flight_label1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flight_label1MouseEntered
        flight_label1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(250,250,250)));
    }//GEN-LAST:event_flight_label1MouseEntered

    private void flight_label1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flight_label1MouseExited
        hotel_label1.setBorder(BorderFactory.createEmptyBorder());
    }//GEN-LAST:event_flight_label1MouseExited

    private void flight_label1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flight_label1MousePressed
        
    }//GEN-LAST:event_flight_label1MousePressed

    private void hotel_label1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hotel_label1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_hotel_label1MouseEntered

    private void hotel_label1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hotel_label1MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_hotel_label1MouseExited

    private void hotel_label1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hotel_label1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_hotel_label1MousePressed

    private void day_tour_label1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_day_tour_label1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_day_tour_label1MouseEntered

    private void day_tour_label1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_day_tour_label1MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_day_tour_label1MouseExited

    private void day_tour_label1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_day_tour_label1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_day_tour_label1MousePressed

    private void search_labelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_labelMouseReleased
        searchFlights();
    }//GEN-LAST:event_search_labelMouseReleased

    private void back_labelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back_labelMouseReleased
        slideRight();
    }//GEN-LAST:event_back_labelMouseReleased

    private void inbound_forwardMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inbound_forwardMousePressed
        slideScrollBar(inbound_scrollpane, 120);
    }//GEN-LAST:event_inbound_forwardMousePressed

    private void inbound_backMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inbound_backMousePressed
        slideScrollBar(inbound_scrollpane, -120);
    }//GEN-LAST:event_inbound_backMousePressed

    private void outbound_forwardMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outbound_forwardMousePressed
        slideScrollBar(outbound_scrollpane, 120);
    }//GEN-LAST:event_outbound_forwardMousePressed

    private void outbound_backMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outbound_backMousePressed
        slideScrollBar(outbound_scrollpane, -120);
    }//GEN-LAST:event_outbound_backMousePressed

    private void jTravelerSpinnerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTravelerSpinnerKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTravelerSpinnerKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back_label;
    private javax.swing.JPanel container;
    private javax.swing.JLabel day_tour_label;
    private javax.swing.JLabel day_tour_label1;
    private javax.swing.JLabel flight_label;
    private javax.swing.JLabel flight_label1;
    private javax.swing.JPanel flight_preference;
    private javax.swing.JPanel flight_result;
    private javax.swing.JLabel hotel_label;
    private javax.swing.JLabel hotel_label1;
    private javax.swing.JPanel hotel_result;
    private javax.swing.JLabel inbound_back;
    private javax.swing.JLabel inbound_flight;
    private javax.swing.JLabel inbound_forward;
    private javax.swing.JList<String> inbound_list;
    private javax.swing.JScrollPane inbound_scrollpane;
    private javax.swing.JLabel inbound_title;
    private javax.swing.JLabel jAbout;
    private javax.swing.JLabel jContact;
    private javax.swing.JPanel jDepartingCalendar;
    private javax.swing.JLabel jDepartingLabel;
    private javax.swing.JComboBox<String> jDepartureComboBox;
    private javax.swing.JLabel jDepartureLabel;
    private javax.swing.JLabel jExit;
    private javax.swing.JLabel jExit1;
    private javax.swing.JPanel jHotelSelect;
    private javax.swing.JLabel jInstruction;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox<String> jLocationComboBox;
    private javax.swing.JLabel jLocationLabel;
    private javax.swing.JPanel jReturningCalendar;
    private javax.swing.JLabel jReturningLabel;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel jToursSelect;
    private javax.swing.JLabel jTravelerLabel;
    private javax.swing.JSpinner jTravelerSpinner;
    private javax.swing.JPanel menuBar;
    private javax.swing.JPanel menuBar1;
    private javax.swing.JLabel outbound_back;
    private javax.swing.JLabel outbound_flight;
    private javax.swing.JLabel outbound_forward;
    private javax.swing.JList<String> outbound_list;
    private javax.swing.JScrollPane outbound_scrollpane;
    private javax.swing.JLabel outbound_title;
    private javax.swing.JPanel preference_container;
    private javax.swing.JPanel preference_panel;
    private javax.swing.JPanel preference_tab;
    private javax.swing.JPanel result_container;
    private javax.swing.JPanel result_panel;
    private javax.swing.JLabel search_label;
    private javax.swing.JLabel switch_label;
    // End of variables declaration//GEN-END:variables
}
