package blockfighter.client.entities.ingamenumber;

import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Point;

public class IngameNumberAscend extends IngameNumber {

    public IngameNumberAscend(final int num, final byte t, final Point loc, final Player myPlayer) {
        super(num, t, loc, myPlayer);
        this.speedY = -1.25;
        this.x += -35 + Globals.rng(51);
    }

    @Override
    public IngameNumberAscend call() {
        this.y += this.speedY;
        return this;
    }

}
