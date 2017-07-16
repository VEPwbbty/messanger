package test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import source.DBManager;
import source.User;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DBManagerUser {
    private DBManager dbManager;

    @Before
    public void init() {
        try {
            dbManager = new DBManager("C:\\sqlite-dll-win64-x64-3190300\\messenger.db");
        } catch (SQLException e) {
            System.out.println("Подключение не удалось");
        }
    }

    @Test
    public void addUsers() {
        assertEquals(true, dbManager.addUser("veppev1", "veppev1997", "Виталий1"));
        assertEquals(true, dbManager.addUser("ohundr", "123456", "Миша"));
        assertEquals(false, dbManager.addUser("veppev", "852963741", "Катя"));
    }

    @Test
    public void getUser() {
        assertEquals(new User(1, "Виталий"), dbManager.getUser("veppev", "veppev1997"));
        assertEquals(null, dbManager.getUser("veppev2", "veppev1997"));
        assertEquals(null, dbManager.getUser(null, null));
    }

    @Test
    public void getAllUsers() {
        Set<User> users = dbManager.getUsers();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void changeName() {
        assertEquals(true, dbManager.changeUserName(1, "Миша1"));
        assertEquals(true, dbManager.changeUserName(-1, "Миша1"));
        assertEquals(true, dbManager.changeUserName(4, "Виталий"));
        assertEquals(false, dbManager.changeUserName(5, "Виталий"));
    }

    @Test
    public void changePassword() {
        assertEquals(true, dbManager.changeUserPassword(1, "Миша1"));
        assertEquals(true, dbManager.changeUserPassword(-1, "Миша1"));
        assertEquals(true, dbManager.changeUserPassword(4, "Виталий"));
        assertEquals(true, dbManager.changeUserPassword(1, "Виталий"));
    }
}
