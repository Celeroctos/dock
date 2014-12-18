package MEK7222;

import Core.Machine;
import Core.Node;
import Core.Receiver;

import java.nio.ByteBuffer;

public class OrderInfo extends Node<Integer, Node> {

    public OrderInfo(Machine machine, Receiver receiver) {
        super(machine, receiver, "order-info", 0);
    }

    enum Length {

        ORDER_NUMBER              (10),
        ORDER_DATE                (12),
        PATIENT_NAME              (26),
        PATIENT_SEX               (1),
        PATIENT_BIRTHDAY          (8),
        PATIENT_AGE               (3),
        DOCTOR_NAME               (26),
        WARD_NAME                 (13),
        DEPARTMENT_NAME           (13),
        RECEIVE_DATE              (12),
        ORDER_INPUT_DATE          (12),
        PATIENT_ID                (13),
        ANALYZER_GROUP_NUMBER     (1),
        NORMAL_RANGE_TABLE_NUMBER (1),
        RESPONSIBLE_NAME          (26),
        LABORATORY_NUMBER         (4),
        COMMENT                   (128),
        RESERVED                  (51),
        TOTAL (
            ORDER_NUMBER.length +
            ORDER_DATE.length +
            PATIENT_NAME.length +
            PATIENT_SEX.length +
            PATIENT_BIRTHDAY.length +
            PATIENT_AGE.length +
            DOCTOR_NAME.length +
            WARD_NAME.length +
            DEPARTMENT_NAME.length +
            RECEIVE_DATE.length +
            ORDER_INPUT_DATE.length +
            PATIENT_ID.length +
            ANALYZER_GROUP_NUMBER.length +
            NORMAL_RANGE_TABLE_NUMBER.length +
            RESPONSIBLE_NAME.length +
            LABORATORY_NUMBER.length +
            COMMENT.length +
            RESERVED.length
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
