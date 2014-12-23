import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

public class MachineFactory {

    private MachineFactory() {
        /* Locked */
    }

    /**
     * Get singleton factory instance
     * @return - Factory instance
     */
    public static MachineFactory getFactory() {
        return factory;
    }

    /**
     * Build machine for MEK7222
     * @return - Machine's instance
     * @throws Exception
     */
    public Machine createMek7222() throws Exception {

        final Machine machine = new Machine("MEK7222");

        final Map<Integer, String> formatMap = new LinkedHashMap<Integer, String>() {
            {
                put(10, "order-information");
                put(11, "order-information-ok");
                put(12, "order-information-ng");
                put(20, "measurement-data");
                put(21, "measurement-data-ok");
                put(22, "measurement-data-ng");
            }
        };

        machine.setParser(new Parser() {
            @Override
            public void parse(byte[] bytes) throws Exception {

                Rule rule = ((Rule) machine.getRule());
                Node root = rule.getRoot();

                ByteBuffer buffer = ByteBuffer.wrap(bytes);

                byte[] lengthBuffer = new byte[
                        root.find("outline/length").getLength()
                    ];

                byte[] formatBuffer = new byte[
                        root.find("outline/format").getLength()
                    ];

                buffer.get(lengthBuffer);
                buffer.get(formatBuffer);

                int length = Integer.parseInt(new String(lengthBuffer));
                int format = Integer.parseInt(new String(formatBuffer));

                parse(buffer, root.get(formatMap.get(format)));


            }
        });

        machine.getRule().load();
        machine.getRule().build();

        return machine;
    }

    private static MachineFactory factory
            = new MachineFactory();
}
