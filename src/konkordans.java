public class konkordans {

    public native void tokenizer();

    static {
        System.loadLibrary("tokenizer");
    }

    public static void setup() {
        new konkordans().tokenizer();

    }
}
