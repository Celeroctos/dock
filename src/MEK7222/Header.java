package MEK7222;

import Core.Machine;
import Core.Node;
import Core.Receiver;

import java.nio.ByteBuffer;

public class Header extends Node<Integer, Node> {

    public Header(Machine machine, Receiver receiver) throws Exception {

        super(machine, receiver, machine.getName(), Length.TOTAL.getLength());
        
        add(10, new Order(machine, receiver));
//        add(11, new Order(machine, receiver));
//        add(12, new Order(machine, receiver));
//        add(20, new Order(machine, receiver));
//        add(21, new Order(machine, receiver));
//        add(22, new Order(machine, receiver));
    }

    enum Length {

        LENGTH (6),
        FORMAT (2),
        TOTAL  (
            LENGTH.length +
            FORMAT.length
        );

        private Length(int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }

        private int length;
    }

    @Override
    public boolean parse(ByteBuffer buffer) throws Exception {

        byte[] length = new byte[6];
        byte[] format = new byte[2];

        buffer.get(length);
        buffer.get(format);

        this.length = Integer.parseInt(new String(length));
        this.format = Integer.parseInt(new String(format));

        return true;
    }

    @Override
    public int getLength() {
        return length;
    }

    public int getFormat() {
        return format;
    }

    int format = -1;
    int length = -1;
}
