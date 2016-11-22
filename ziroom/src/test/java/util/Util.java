package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import database.Database;

final public class Util {

    private Util() {
        // empty
    }

    public static String readFile(String path) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        return content.toString();
    }

    public static void clearDatabase(Database database) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        Method method = Database.class.getDeclaredMethod("createOneConnection", new Class<?>[] {});
        method.setAccessible(true);
        Connection connection = (Connection) method.invoke(database);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        statement.execute("delete from history_location where id>0;");
        statement.execute("delete from history_price where id>0;");
        statement.execute("delete from history_room where id>0;");
        statement.execute("delete from history_house where id>0;");
        statement.execute("delete from location where id>0;");
        statement.execute("delete from price where id>0;");
        statement.execute("delete from room where id>0;");
        statement.execute("delete from house where id>0;");
        connection.commit();
        connection.setAutoCommit(true);
        connection.close();
    }

    public static void insertRoomIntoDatabase(Database database) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        Method method = Database.class.getDeclaredMethod("createOneConnection", new Class<?>[] {});
        method.setAccessible(true);
        Connection connection = (Connection) method.invoke(database);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        statement.execute(
                "INSERT INTO `house` VALUES ('1', '1', 'detail name', 'not detail name', 'layout', '3', '1', '5', '10');");
        statement.execute("INSERT INTO `location` VALUES ('10', '1', '6', 'station name', '100');");
        statement.execute("INSERT INTO `location` VALUES ('20', '1', '6', 'station name 2', '200');");
        statement.execute("INSERT INTO `location` VALUES ('30', '1', '6', 'station name 3', '300');");
        statement.execute("INSERT INTO `price` VALUES ('100', '1000', '2000', '2000', '2100', '月付');");
        statement.execute("INSERT INTO `price` VALUES ('200', '1000', '1900', '1900', '2000', '季付');");
        statement.execute("INSERT INTO `price` VALUES ('300', '1000', '1900', '1900', '1900', '半年付');");
        statement.execute("INSERT INTO `price` VALUES ('400', '1000', '1900', '1900', '1800', '年付');");
        statement.execute("INSERT INTO `price` VALUES ('500', '2000', '3000', '3000', '3100', '月付');");
        statement.execute("INSERT INTO `price` VALUES ('600', '2000', '2900', '2900', '3000', '季付');");
        statement.execute("INSERT INTO `price` VALUES ('700', '2000', '2900', '2900', '2900', '半年付');");
        statement.execute("INSERT INTO `price` VALUES ('800', '2000', '2900', '2900', '2800', '年付');");
        statement.execute(
                "INSERT INTO `room` VALUES ('10000', '1', '1000', 'number', '10', '南', '木棉', '4', '1', '0', 'Available', '2016-11-19 14:08:09', '2016-11-21 15:28:08');");
        statement.execute(
                "INSERT INTO `room` VALUES ('20000', '1', '2000', 'number', '15', '南', '拿铁', '4', '0', '1', 'Unavailable', '2016-11-16 14:08:09', '2016-11-21 14:08:13');");
        connection.commit();
        connection.setAutoCommit(true);
        connection.close();
    }

    public static void initDatabase(Database database) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        clearDatabase(database);
        insertRoomIntoDatabase(database);
    }
}
