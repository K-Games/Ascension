package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;

public class BuffStun extends Buff {

    public BuffStun(final LogicModule l, final int d) {
        super(l, d);
        super.setDebuff(true);
    }

}
