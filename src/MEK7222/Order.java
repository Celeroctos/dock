package MEK7222;

import Core.Machine;
import Core.Node;
import Core.Receiver;

import java.nio.ByteBuffer;

public class Order extends Node<Integer, Node> {

    public Order(Machine machine, Receiver receiver) throws Exception {

        super(machine, receiver, "", Length.TOTAL.length);
    }

    enum Length {

        ID    (2),
        ORDER (10),
        CMD   (1),
        INFO  (360),
        TOTAL (
            ID.length +
            ORDER.length +
            CMD.length +
            INFO.length
        );

        private Length(int length) {
            this.length = length;
        }

        private int length;
    }

    @Override
    public boolean parse(ByteBuffer buffer) throws Exception {



        return false;
    }
}
