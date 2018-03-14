package kevin.androidhealthtracker;

import org.junit.Test;

import kevin.androidhealthtracker.Util.PasswordTool;

import static junit.framework.Assert.assertEquals;


public class HashSaltPasswordTest {

    @Test
    public void encryptionIsCorrect() throws Exception {
        String userName = "testusername";
        String userPassword = "testpassword";
        String expectedHash = "�S�>��Gƣf~epHXAz-a�";

        byte[] hashedPassword = PasswordTool.saltedHash(userPassword, userName.getBytes("UTF-8"));
        assertEquals(new String(hashedPassword, "UTF-8"), expectedHash);
    }

}