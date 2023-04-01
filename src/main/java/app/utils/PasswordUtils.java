package app.utils;


import app.common.CustomValidator;
import app.services.accounts.models.User;
import io.smallrye.mutiny.Uni;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import static app.utils.Utils.isBlank;
import static app.utils.Utils.isNull;

public class PasswordUtils {

    public static Uni<Void> validatePassword(String password) {
        HashSet<String> violations = new HashSet<>();

        if (password.length() > 20 || password.length() < 8) {
            violations.add("Password must be less than 20 and more than 8 characters in length.");
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            violations.add("Password must have at least one uppercase character");
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            violations.add("Password must have at least one lowercase character");
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            violations.add("Password must have at least one number");
        }
        Pattern specialChars = Pattern.compile("[$&+,:;=?@#|'<>.-^*()%!]");
        if (!specialChars.matcher(password).find()) {
            violations.add("Password must have at least one special character");
        }

        if (violations.size() > 0) {
            String message = String.join("; ", violations);
            return Uni.createFrom()
                    .failure(
                            new CustomValidator.CustomValidationException(Map.of("password", message), "Invalid password"));
        } else {
            return Uni.createFrom().voidItem();
        }
    }

    public static boolean verifyPassword(String plainPassword, User user) {
        if (isNull(user)) return false;
        if (isBlank(user.getHashedPassword())) return false;
        return verifyPassword(plainPassword, user.getHashedPassword(), user.getSalt());
    }


        private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

        public static String hashPassword(String password, String salt) {
            String concat = (salt == null ? "" : salt) + password;
            return hashSha512(concat);
        }

        private static String hashSha512(String payload){
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                byte[] bHash = md.digest(payload.getBytes(StandardCharsets.UTF_8));
                return bytesToHex(bHash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public static String getSalt(){
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return bytesToHex(salt);
        }

        private static String bytesToHex(byte[] bytes) {
            char[] chars = new char[bytes.length * 2];
            for (int i = 0; i < bytes.length; i++) {
                int x = 0xFF & bytes[i];
                chars[i * 2] = HEX_CHARS[x >>> 4];
                chars[1 + i * 2] = HEX_CHARS[0x0F & x];
            }
            return new String(chars);
        }

        public static boolean verifyPassword(String plainPassword, String hashedPassword, String salt) {
            if(isNull(plainPassword) || isBlank(plainPassword)) return false;
            return MessageDigest.isEqual(
                    hashedPassword.getBytes(StandardCharsets.UTF_8),
                    hashPassword(plainPassword, salt).getBytes(StandardCharsets.UTF_8));
        }
}
