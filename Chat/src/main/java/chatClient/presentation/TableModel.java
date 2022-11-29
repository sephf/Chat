package chatClient.presentation;

import chatClient.Application;
import chatProtocol.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TableModel extends AbstractTableModel implements javax.swing.table.TableModel{
    List<User> rows;
    int[] cols;

    public TableModel(int[] cols, List<User> rows){
        initColNames();
        this.cols=cols;
        this.rows=rows;
    }

    public int getColumnCount() {
        return cols.length;
    }

    public String getColumnName(int col){
        return colNames[cols[col]];
    }

    public Class<?> getColumnClass(int col){
        switch (cols[col]){
            default: return super.getColumnClass(col);
        }
    }

    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int row, int col) {
        User user = rows.get(row);
        switch (cols[col]){
            case NOMBRE: return user.getNombre();
            case STATE: return isOnline(user.isOnline());
            default: return "";
        }
    }
    public String isOnline(boolean flag){
        if(flag){
            return "online" ;
        }else{
            return "offline";
        }
    }
    public static final int NOMBRE=0;
    public static final int STATE=1;

    String[] colNames = new String[2];
    private void initColNames(){
        colNames[NOMBRE]= "CONTACTOS";
        colNames[STATE]="";
    }

}