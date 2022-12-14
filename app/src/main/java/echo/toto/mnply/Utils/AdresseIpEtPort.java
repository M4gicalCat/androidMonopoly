package echo.toto.mnply.Utils;

import java.util.Locale;

public class AdresseIpEtPort {
    public static String translateToCode(String ip, int port) {
        StringBuilder b = new StringBuilder();

        String[] ipSplit = ip.split("\\.");
        for (String oct : ipSplit) {
            int octet = Integer.parseInt(oct);
            int gauche = octet / 16;
            int droite = octet % 16;
            b.append(getChar(gauche)).append(getChar(droite));
        }
        b.append(port);

        return b.toString();
    }

    private static String getChar(int val) {
        return Character.toString((char) (((int)'A') + val));
    }

    public static String getIp(String code) {
        code = code.toUpperCase(Locale.ROOT);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String octetCode = code.substring(2 * i, 2 * i + 2);
            int base16num1 = octetCode.charAt(0) - (int) 'A';
            int base16num2 = octetCode.charAt(1) - (int) 'A';
            b.append(base16num1 * 16 + base16num2);
            if (i < 3) b.append(".");
        }
        return b.toString();
    }

    public static int getPort(String code) {
        return Integer.parseInt(code.substring(8));
    }
}
