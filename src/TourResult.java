package src;
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author npquy
 */
public class TourResult extends javax.swing.JPanel {

    /** Creates new form TourResult */
    public TourResult() {
        initComponents();
    }
    public javax.swing.JLabel getTourLocation() {
        return location;
    }

    public javax.swing.JLabel getDay() {
        return date;
    }

    public javax.swing.JLabel getPrice() {
        return price;
    }

    public javax.swing.JLabel getTourName() {
        return tourName;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tourName = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        location = new javax.swing.JLabel();
        price = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        tourName.setFont(new java.awt.Font("Segoe UI Light", 1, 20)); // NOI18N
        tourName.setText("Tour Name");

        date.setFont(new java.awt.Font("Segoe UI Light", 0, 16)); // NOI18N
        date.setText("Date");

        location.setFont(new java.awt.Font("Segoe UI Light", 0, 16)); // NOI18N
        location.setText("Location");

        price.setFont(new java.awt.Font("Segoe UI Light", 1, 16)); // NOI18N
        price.setForeground(new java.awt.Color(204, 0, 0));
        price.setText("2000 Kr");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(date))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(price)
                            .addComponent(location))))
                .addContainerGap(103, Short.MAX_VALUE))
            .addComponent(tourName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tourName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(location)
                .addGap(7, 7, 7)
                .addComponent(date)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(price)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        tourName.setHorizontalAlignment(JLabel.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel date;
    private javax.swing.JLabel location;
    private javax.swing.JLabel price;
    private javax.swing.JLabel tourName;
    // End of variables declaration//GEN-END:variables

}
