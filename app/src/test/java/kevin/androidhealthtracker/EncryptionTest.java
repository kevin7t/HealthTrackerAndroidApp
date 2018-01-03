package kevin.androidhealthtracker;

import org.junit.Test;

import kevin.androidhealthtracker.Util.PasswordEncrypter;

import static org.junit.Assert.*;

public class EncryptionTest {
    @Test
    public void encryptionIsCorret() throws Exception {
        PasswordEncrypter passwordEncrypter = new PasswordEncrypter();
        byte[] salt = passwordEncrypter.generateSalt();
        String inputPassword = "testpassword";
        byte[] encryptedPassword = passwordEncrypter.getEncryptedPassword(inputPassword, salt);
        passwordEncrypter.authenticate(inputPassword,encryptedPassword,salt);
    }
}