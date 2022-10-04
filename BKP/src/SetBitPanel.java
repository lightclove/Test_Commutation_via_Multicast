import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * Created by alexsandr on 10.02.17.
 */
public class SetBitPanel extends JPanel {
    private ArrayList<JCheckBox> listJCheckBox;
    //названия устройств
    private ArrayList<String> listString;
    private int setDevices;

    public SetBitPanel(ArrayList<String> listString, boolean setVisible, int setDevices) {
        this.listString = listString;
        this.setDevices = setDevices;

        createPanel();

        setVisible(setVisible);
    }

    public byte getByte() {
        byte outByte = 0;
        for (int i = 0; i < listJCheckBox.size(); i++) {

            outByte |= getBit(listJCheckBox.get(i).isSelected(), i);

        }
        return outByte;
    }

    private byte getBit(boolean bool, int number) {
        byte b1 = 0;
        if (bool) {
            b1 = 1;
            b1 = (byte) ((byte) b1 << number);
        }
        return b1;
    }

    private boolean setJJCheckBox(int numberBit) {
        int i = setDevices >> numberBit;
        return !(i % 2 == 0);
    }

    private void createPanel() {

        listJCheckBox = new ArrayList<>();
        for (int i = 0; i < listString.size(); i++)
            listJCheckBox.add(new JCheckBox(listString.get(i), setJJCheckBox(i)));

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;//GridBagConstraints.REMAINDER;
        gridBagConstraints.gridx = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.gridy = 0;//GridBagConstraints.RELATIVE;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;

        JLabel label = new JLabel("Формирование БИ для РЭС:");
        gbl.setConstraints(label, gridBagConstraints);
        add(label);
        gridBagConstraints.gridx = 0;
        for (int i = 0; i <= 7; i++) {
            gridBagConstraints.gridy = i + 1;
            gbl.setConstraints(listJCheckBox.get(i), gridBagConstraints);
            add(listJCheckBox.get(i));
        }


    }

//для проверки возвращаемого значения
/*
    public static void main(String[] args) {
        ArrayList<String >stringList =new ArrayList<String>();
        for(int i=1;i<9;i++)
            stringList.add("device"+i);

        SetBitPanel setBitPanel=new SetBitPanel(stringList,true,64);

        System.out.println( Integer.toString(setBitPanel.getBit(false,0),2));

        System.out.println( Integer.toString(setBitPanel.getBit(true,0),2));
        System.out.println( Integer.toString(setBitPanel.getBit(true,1),2));
        System.out.println( Integer.toString(setBitPanel.getBit(true,2),2));
        System.out.println( Integer.toString(setBitPanel.getBit(true,3),2));
        System.out.println( Integer.toString(setBitPanel.getBit(true,4),2));
        System.out.println( Integer.toString(setBitPanel.getBit(true,5),2));
        System.out.println( Integer.toString(setBitPanel.getBit(true,6),2));
        System.out.println( Integer.toString(setBitPanel.getBit(true,7),10));
        //   System.out.println( 1%2);
        //    System.out.println( 2%2);
    int parseInt=Integer.parseInt("11111111",2);
        System.out.println(" parseInt = " +parseInt);

        JButton button=new JButton("clik");
        JPanel jPanel=new JPanel(new FlowLayout());
        jPanel.add(setBitPanel);
        jPanel.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println( Integer.toString(setBitPanel.getByte(),10));
            }
        });
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame Frame = new JFrame();
                //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                //mainFrame.setLocation(dim.width/2-mainFrame.getSize().width/2, dim.height/2-mainFrame.getSize().height/2);
                Frame.setSize(600,800);
                //mainFrame.setResizable(false);
                Frame.add(jPanel);
                Frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                String message = (e.getMessage());
                if (message != null) {
                    JOptionPane.showMessageDialog(null, e.getClass().getName() + " :\n" + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }
    */

}
