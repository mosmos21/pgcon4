import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class Main {
    // メインメモリ64バイト（下位6ビットを使用）
    static byte[] memory = new byte[64];
    static byte[] register = new byte[6];
    static boolean zf;
    static boolean cf;
 
    // 入力の1行目だけを読み込みメインメモリを初期化して、メインメモリの状態を出力するプログラムです。
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
 
        // 1行目メインメモリ
        String line = in.readLine();
        for (int i = 0; i < line.length(); i++) {
            byte val = base64Decode(line.charAt(i));
            memory[i] = val;
        }
 
        // TODO:2行目以降を読み込み、スクリプトを実行する
        solve(in);
 
        // メインメモリの状態を出力する
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memory.length; i++) {
            sb.append(base64Encode(memory[i]));
        }
        System.out.println(sb.toString());
    }
 
    // ******************************************************************************
 
    // BASE64テーブル
    static final String base64_TBL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
 
    // BASE64エンコード：0-63の範囲外のチェックをしていません。
    static char base64Encode(int v) {
        return base64_TBL.charAt(v);
    }
 
    // BASE64デコード：不正な文字のチェックをしていませんが、入力データはエラーがないことが保証されます。
    static byte base64Decode(char c) {
        return (byte) base64_TBL.indexOf(c);
    }
 
    static void solve(BufferedReader in) throws IOException {
        String[] orders;
        ;
        int line = Integer.parseInt(in.readLine());
        orders = new String[line];
        for (int i = 0; i < line; i++) {
            orders[i] = in.readLine();
        }
        int next = 1;
        for (int i = 0; i < line; i += next) {
            next = 1;
            if (orders[i].startsWith(";")) {
                continue;
            }
            String[] order = orders[i].split(" ");
            switch (order[0]) {
            case "NOP":
                continue;
            case "HLT":
                return;
            case "MOV":
                mov(order[1]);
                break;
            case "INC":
                inc(order[1]);
                break;
            case "DEC":
                dec(order[1]);
                break;
            case "ADD":
                add(order[1]);
                break;
            case "SUB":
                sub(order[1]);
                break;
            case "CMP":
                cmp(order[1]);
                break;
            case "JMP":
                next = jmp(order[1]);
                break;
            case "JPZ":
                next = jpz(order[1]);
                break;
            case "JNZ":
                next = jnz(order[1]);
                break;
            case "JPC":
                next = jpc(order[1]);
                break;
            case "JNC":
                next = jnc(order[1]);
                break;
            }
        }
    }
 
    static void mov(String args) {
        if (args.matches("[ABCDEF],[0-9]+")) {
            char reg = args.charAt(0);
            int num = Integer.parseInt(args.split(",")[1]);
            register[reg - 'A'] = (byte) num;
        } else if (args.matches("[ABCDEF],[ABCDEF]")) {
            char reg1 = args.charAt(0);
            char reg2 = args.charAt(2);
            register[reg1 - 'A'] = register[reg2 - 'A'];
        } else if (args.matches("\\([ABCDEF]\\),[ABCDEF]")) {
            char reg1 = args.charAt(1);
            char reg2 = args.charAt(4);
            memory[register[reg1 - 'A']] = register[reg2 - 'A'];
        } else if (args.matches("[ABCDEF],\\([ABCDEF]\\)")) {
            char reg1 = args.charAt(0);
            char reg2 = args.charAt(3);
            register[reg1 - 'A'] = memory[register[reg2 - 'A']];
        }
    }
 
    static void inc(String args) {
        char reg = args.charAt(0);
        cf = (register[reg - 'A'] + 1) > 63;
        register[reg - 'A'] = (byte) ((register[reg - 'A'] + 1) & 0x3f);
        zf = register[reg - 'A'] == 0;
        
    }
 
    static void dec(String args) {
        char reg = args.charAt(0);
        cf = (register[reg - 'A'] - 1) < 0;
        register[reg - 'A'] = (byte) ((register[reg - 'A'] - 1) & 0x3f);
        zf = register[reg - 'A'] == 0;
        
    }
 
    static void add(String args) {
        char reg1 = args.charAt(0);
        char reg2 = args.charAt(2);
        cf = (register[reg1 - 'A'] + register[reg2 - 'A']) > 63;
        register[reg1 - 'A'] = (byte) ((register[reg1 - 'A'] + register[reg2 - 'A']) & 0x3f);
        zf = register[reg1 - 'A'] == 0;
    }
 
    static void sub(String args) {
        char reg1 = args.charAt(0);
        char reg2 = args.charAt(2);
        cf = (register[reg1 - 'A'] - register[reg2 - 'A']) < 0;
        register[reg1 - 'A'] = (byte) ((register[reg1 - 'A'] - register[reg2 - 'A']) & 0x3f);
        zf = register[reg1 - 'A'] == 0;
    }
 
    static void cmp(String args) {
        char reg1 = args.charAt(0);
        char reg2 = args.charAt(2);
        zf = register[reg1 - 'A'] == register[reg2 - 'A'];
        cf = register[reg1 - 'A'] < register[reg2 - 'A'];
        
    }
 
    static int jmp(String args) {
        int jump = 0;
        boolean plus = args.charAt(1) == '+' ? true : false;
        jump = Integer.parseInt(args.substring(2));
        return plus ? jump : - jump;
    }
 
    static int jpz(String args) {
        if(zf){
            int jump = 0;
            boolean plus = args.charAt(1) == '+' ? true : false;
            jump = Integer.parseInt(args.substring(2));
            return plus ? jump : - jump;
        }
        return 1;
    }
 
    static int jnz(String args) {
        if(!zf){
            int jump = 0;
            boolean plus = args.charAt(1) == '+' ? true : false;
            jump = Integer.parseInt(args.substring(2));
            return plus ? jump : - jump;
        }
        return 1;
    }
    
    static int jpc(String args) {
        if(cf){
            int jump = 0;
            boolean plus = args.charAt(1) == '+' ? true : false;
            jump = Integer.parseInt(args.substring(2));
            return plus ? jump : - jump;
        }
        return 1;
    }
 
    static int jnc(String args) {
        if(!cf){
            int jump = 0;
            boolean plus = args.charAt(1) == '+' ? true : false;
            jump = Integer.parseInt(args.substring(2));
            return plus ? jump : - jump;
        }
        return 1;
    }
 
}