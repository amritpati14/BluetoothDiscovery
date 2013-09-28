package com.benvonhandorf.bluetoothdiscovery.SensorTagDevice;

import java.util.UUID;

/**
 * Created by benvh on 7/28/13.
 */
public class Sensors {
    public static class Descriptors {
        public static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        public static final UUID USER_DESCRIPTION = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    }

    public static class IR_SENSOR {
        public static final UUID SERVICE = UUID.fromString("f000aa00-0451-4000-b000-000000000000");
        public static final UUID DATA = UUID.fromString("f000aa01-0451-4000-b000-000000000000");
        public static final UUID CONFIG = UUID.fromString("f000aa02-0451-4000-b000-000000000000");
    }

    public static class HUMIDITY {
        public static final UUID SERVICE = UUID.fromString("f000aa20-0451-4000-b000-000000000000");
        public static final UUID DATA = UUID.fromString("f000aa21-0451-4000-b000-000000000000");
        public static final UUID CONFIG = UUID.fromString("f000aa22-0451-4000-b000-000000000000");
    }
}
