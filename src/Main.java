
public class Main {

    public static void main(String[] args) throws Exception {

        Machine mek7222 = MachineFactory.getFactory().createMek7222();

        Node root = ((Rule) mek7222.getRule()).getRoot();
        Node outline = root.find("outline");

        for (Node node : outline.values()) {
            System.out.println(node.getName());
        }
    }
}