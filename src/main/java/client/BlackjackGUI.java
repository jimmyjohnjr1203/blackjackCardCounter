package client;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class BlackjackGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private JButton hitButton;
    private JButton standButton;
    

    private String BASE_URL = "http://localhost:8080/api/blackjack";
    private String USERNAME = "jklubbs";
    private String PASSWORD = "fdde45f";
    private ClientConnecter clientConnecter;

    private CardPanel cardPanel;
    private Map<Card, ImageIcon> cardImages;
    private Random random = new Random();
    private UUID sessionId;

    public BlackjackGUI() {
        setTitle("Blackjack Game");
        setSize(1000, 800);
        loadCards();
        // create and pass the buttons to the card panel
        // it will resize them and add them to the panel
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        cardPanel = new CardPanel(hitButton, standButton, cardImages);
        setContentPane(cardPanel);

        // now set the action listeners for the hit/stand buttons
        hitButton.addActionListener(e -> {
            System.out.println("Hit button clicked");
            List<Card> cards = List.of(Card.values());
            cardPanel.addPlayerCard(cards.get(random.nextInt(cards.size())));
            repaint(); 
        });
        standButton.addActionListener(e -> {
            System.out.println("Stand button clicked");
            List<Card> cards = List.of(Card.values());
            cardPanel.addDealerCard(cards.get(random.nextInt(cards.size())));
            repaint(); 
        });

        

        // client connecter to make API calls on the server
        clientConnecter = new ClientConnecter(BASE_URL, USERNAME, PASSWORD);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        addMenuBar();
        //TODO: keyboard shortcuts
        //TODO: mouse events
    }

    private void addMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        
        menuBar.add(fileMenu);
        addMenuItem(fileMenu, "Reconnect", () -> {
            System.out.println("Load clicked");
            try {
                List<SessionSummary> sessionSummaryList = clientConnecter.listSessions();
                for (SessionSummary session : sessionSummaryList) {
                    System.out.println("Session ID: " + session.sessionId + ", Balance: " + session.balance);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        addMenuItem(fileMenu, "New Game", () -> {
            System.out.println("New Game clicked");
            try {
                GameState state = clientConnecter.startGame();
                System.out.println(state);
                sessionId = state.sessionId;
                // TODO: use the game state to update the UI
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error starting new game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    // convert "THREE OF HEARTS" from server to Card.THREE_OF_HEARTS
    private Card getCard(String cardName) {
        return Card.valueOf(cardName.toUpperCase().replace(' ', '_'));
    }

    private void addMenuItem(JMenu menu, String name, Runnable action) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> action.run());
        menu.add(menuItem);
    }

    private void loadCards() {
        // Load card images and add them to the main panel
        // This is where you would implement the logic to load and display cards
        cardImages = new HashMap<>();
        for (Card card : Card.values()) {
            ImageIcon cardImage = new ImageIcon(getClass().getResource("/assets/" + card.getFilename()));
            cardImages.put(card, cardImage);
        }
    }

    public void showListPopup(String title, java.util.List<String> items) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JList<String> list = new JList<>(new DefaultListModel<>());
        DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
        for (String item : items) {
            model.addElement(item);
        }

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // double click to select
                    String selected = list.getSelectedValue();
                    System.out.println("Selected: " + selected);
                    dialog.dispose();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        BlackjackGUI gui = new BlackjackGUI();
        gui.setVisible(true);
    }
    
}
