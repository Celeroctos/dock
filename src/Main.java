
public class Main {

    public static void main(String[] args) throws Exception {

        Machine mek7222 = new Machine("MEK7222");

        mek7222.getLoader().load();
        mek7222.getBuilder().build();
    }
}