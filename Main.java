import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("TwoFiveSixPCs.txt"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<Integer> plaintext = new ArrayList<>();
        List<Integer> ciphertext = new ArrayList<>();

        for (String line : lines) {
            String[] a = line.split(" ");
            plaintext.add(Integer.parseInt(a[0]));
            ciphertext.add(Integer.parseInt(a[3].split("\n")[0]));
        }

        int[] S_Box = {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7};

        int[] S_BoxInv = new int[16];
        for (int i = 0; i < S_Box.length; i++) {
            S_BoxInv[S_Box[i]] = i;
        }

        int[] count = new int[256];

        String del_p = "00001011";
        String del_u3 = "00010100";

        for (int i = 0; i < plaintext.size(); i++) {
            int p = plaintext.get(i);
            int c = ciphertext.get(i);

            int p_dash = Integer.parseInt(del_p, 2) ^ Integer.parseInt(String.format("%08d", Integer.parseInt(Integer.toBinaryString(p))), 2);

            int c_dash = ciphertext.get(plaintext.indexOf(p_dash));

            String bin_c = String.format("%08d", Integer.parseInt(Integer.toBinaryString(c)));
            String bin_c_dash = String.format("%08d", Integer.parseInt(Integer.toBinaryString(c_dash)));

            for (int key = 0; key < 256; key++) {
                String bin_key = String.format("%08d", Integer.parseInt(Integer.toBinaryString(key)));
                String V3 = Integer.toBinaryString(Integer.parseInt(bin_key, 2) ^ Integer.parseInt(bin_c, 2));
                V3 = String.format("%0" + bin_key.length() + "d", Integer.parseInt(V3));

                String V3_1 = V3.substring(0, 4);
                String V3_2 = V3.substring(4, 8);

                String U3_1 = String.format("%04d", Integer.parseInt(Integer.toBinaryString(S_BoxInv[binToHexa(V3_1)])));
                String U3_2 = String.format("%04d", Integer.parseInt(Integer.toBinaryString(S_BoxInv[binToHexa(V3_2)])));

                String V3_dash = Integer.toBinaryString(Integer.parseInt(bin_key, 2) ^ Integer.parseInt(bin_c_dash, 2));
                V3_dash = String.format("%0" + bin_key.length() + "d", Integer.parseInt(V3_dash));

                String V3_1_dash = V3_dash.substring(0, 4);
                String V3_2_dash = V3_dash.substring(4, 8);

                String U3_1_dash = String.format("%04d", Integer.parseInt(Integer.toBinaryString(S_BoxInv[binToHexa(V3_1_dash)])));
                String U3_2_dash = String.format("%04d", Integer.parseInt(Integer.toBinaryString(S_BoxInv[binToHexa(V3_2_dash)])));

                String check1 = String.format("%04d", Integer.parseInt(Integer.toBinaryString(Integer.parseInt(U3_1, 2) ^ Integer.parseInt(U3_1_dash, 2))));
                String check2 = String.format("%04d", Integer.parseInt(Integer.toBinaryString(Integer.parseInt(U3_2, 2) ^ Integer.parseInt(U3_2_dash, 2))));

                if (check1.equals(del_u3.substring(0, 4)) && check2.equals(del_u3.substring(4, 8))) {
                    count[key]++;
                }
            }
        }

        int max_count = -1;
        int target_key = 0;
        for (int key = 0; key < 256; key++) {
            if (count[key] > max_count) {
                max_count = count[key];
                target_key = key;
                System.out.println("Key " + String.format("%08d", Integer.parseInt(Integer.toBinaryString(target_key))) +
                        " Probability " + (float) max_count / 256);
            }
        }

       String binaryString = String.format("%8s", Integer.toBinaryString(target_key)).replace(' ', '0');
       String K14 = binaryString.substring(0, 4);
       String K58 = binaryString.substring(4);

       System.out.println("\nK14 is " + K14);
       System.out.println("K58 is " + K58);



    }

    static int binToHexa(String n) {
        int num = Integer.parseInt(n, 2);
        String hexNum = Integer.toHexString(num);
        if (hexNum.equals("a")) {
            hexNum = "10";
        } else if (hexNum.equals("b")) {
            hexNum = "11";
        } else if (hexNum.equals("c")) {
            hexNum = "12";
        } else if (hexNum.equals("d")) {
            hexNum = "13";
        } else if (hexNum.equals("e")) {
            hexNum = "14";
        } else if (hexNum.equals("f")) {
            hexNum = "15";
        }
        return Integer.parseInt(hexNum);
    }
}
