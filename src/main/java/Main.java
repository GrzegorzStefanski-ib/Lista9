import java.sql.*;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) throws SQLException {
        try {
            // ladowanie klasy sterownika, wymaga wyjatku ClassNotFoundException
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder urlSB = new StringBuilder("jdbc:mysql://"); // polaczenie z MySQL
        urlSB.append("localhost:3306/"); // numer portu
        urlSB.append("chocolatesDB?"); // nazwa bazy (tennis_rackets)
        urlSB.append("useUnicode=true&characterEncoding=utf-8"); // kodowanie
        urlSB.append("&user=root"); // nazwa uzytkownika (root)
        urlSB.append("&password=509363509363");   // haslo uzytkownika
        urlSB.append("&serverTimezone=CET"); // strefa czasowa (CET)

        String connectionUrl = urlSB.toString();


        Connection conn = DriverManager.getConnection(connectionUrl);

        System.out.println("|*** a ***|");
        PreparedStatement selectST = conn.prepareStatement("SELECT country_name, avg(sugars)" +
                " from chocolates ch" +
                " join countries c on c.country_id=ch.country_id" +
                " where chocolate_type='Milk'" +
                " group by country_name");
        ResultSet rsSelect = selectST.executeQuery();
        printResultSet(rsSelect);


        System.out.println("|*** b ***|");
        selectST = conn.prepareStatement("SELECT producer_name" +
                " from producers p" +
                " join chocolates c on c.producer_id=p.producer_id" +
                " group by producer_name" +
                " having count(c.chocolate_name)>3");
        rsSelect = selectST.executeQuery();
        printResultSet(rsSelect);

        System.out.println("|*** c ***|");
        selectST = conn.prepareStatement("SELECT avg(fiber)" +
                " from chocolates" +
                " where chocolate_type='Dark'");
        rsSelect = selectST.executeQuery();
        double dark_fiber=0;
        if (rsSelect.next())
        dark_fiber=rsSelect.getDouble(1);

        selectST = conn.prepareStatement("SELECT avg(fiber)" +
                " from chocolates" +
                " where chocolate_type='Milk'");
        rsSelect = selectST.executeQuery();
        double milk_fiber=0;
        if (rsSelect.next())
            milk_fiber=rsSelect.getDouble(1);

        System.out.println("Mleczna czekolada zawiera średnio "+milk_fiber+" g błonnika na 100g, a gorzka "
        + dark_fiber+" g błonnika na 100g");

        System.out.println("|*** d ***|");
        selectST = conn.prepareStatement("SELECT chocolate_name, fat_acids/fat*100 as fatPercent" +
                " from chocolates");
        rsSelect = selectST.executeQuery();
        printResultSet(rsSelect);

        System.out.println("|*** e ***|");
        selectST = conn.prepareStatement("SELECT chocolate_name, carbs" +
                " from chocolates c" +
                " order by carbs asc");
        rsSelect = selectST.executeQuery();
        printResultSet(rsSelect);


        System.out.println("|*** f ***|");
        selectST = conn.prepareStatement("SELECT p.producer_name, avg(sugars)" +
                " from chocolates c" +
                " join producers p on c.producer_id=p.producer_id" +
                " where chocolate_type='Dark'" +
                " group by producer_name");
        rsSelect = selectST.executeQuery();
        printResultSet(rsSelect);


        System.out.println("|*** g ***|");
        selectST = conn.prepareStatement("SELECT 2500*100/cal as chocolatePerDay" +
                " from chocolates c" +
                " where chocolate_name='Ultime Noir 86% Cocoa'");
        rsSelect = selectST.executeQuery();
        double daily_choco=0;
        DecimalFormat format=new DecimalFormat("#");
        if (rsSelect.next())
            daily_choco=rsSelect.getDouble(1);
        System.out.println("Dzienna ilość czekolady: "+format.format(Math.ceil(daily_choco))+" g\n");

        System.out.println("|*** h ***|");
        CallableStatement callableStatement = conn.prepareCall("{CALL chocForCoff(?, ?, ?, ?)}");
        callableStatement.registerOutParameter(4, Types.DECIMAL);
        callableStatement.setString(1, "'35% Cacao Premium Milk'");
        callableStatement.setString(2, "'Brewed Black Tea'");
        callableStatement.setInt(3, 47);
        callableStatement.execute();


        double choco_grams_coff = callableStatement.getDouble(4);
        System.out.println(choco_grams_coff+"\n");

        selectST = conn.prepareStatement("SELECT teobromin('35% Cacao Premium Milk')");
        rsSelect = selectST.executeQuery();
        printResultSet(rsSelect);

        selectST = conn.prepareStatement("select coffein('35% Cacao Premium Milk')");
        rsSelect = selectST.executeQuery();
        printResultSet(rsSelect);


    }

    public static void printResultSet(ResultSet resultSet) throws SQLException {

        ResultSetMetaData rsmd = resultSet.getMetaData(); // metadane o zapytaniu
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn

        while (resultSet.next()) {  // wyswietlenie nazw kolumn i wartosci w rzedach

            for (int i = 1; i <= columnsNumber; i++) {

                if (i > 1)
                    System.out.print(",  ");

                String columnValue = resultSet.getString(i);
                System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
            }

            System.out.println("");
        }

        System.out.println("");

    }
}
