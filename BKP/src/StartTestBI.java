import javax.swing.*;
import java.util.Locale;


/**
 * Программа предназначнена для проведения испытаний в рамках изделия
 * БКП Технические условия КВСБ.465275.010
 */
public class StartTestBI {
    public static void main(String[] args) {
        Locale bLocale = new Locale("en");
        Locale.setDefault(bLocale);
        LoaderCheckProperties loaderCheckProperties=new LoaderCheckProperties("bkp.properties");
        try {
            //UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        if(loaderCheckProperties.isCorecTProperties()) {
            SwingUtilities.invokeLater(() -> {
                try {

                    MainFrame mainFrame = new MainFrame(loaderCheckProperties);
                    //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                    //mainFrame.setLocation(dim.width/2-mainFrame.getSize().width/2, dim.height/2-mainFrame.getSize().height/2);
                    mainFrame.pack();
                    //mainFrame.setResizable(false);
                    mainFrame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = (e.getMessage());
                    if (message != null) {
                        JOptionPane.showMessageDialog(null, e.getClass().getName() + " :\n" + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }


}
