package blockfighter.client.entities.ingamenumber;

import blockfighter.client.Core;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.ConcurrentLinkedDeque;

public class IngameNumberStack extends IngameNumber {

    private static class Number {

        final int number;
        final byte type;

        Number(final int num, final byte type) {
            this.number = num;
            this.type = type;
        }
    }
    private final ConcurrentLinkedDeque<Number> numbers = new ConcurrentLinkedDeque<>();
    private long lastUpdateTime;
    private int numDisplayed = 0;

    public IngameNumberStack(final int num, final byte t, final Point loc, final Player myPlayer) {
        super(num, t, loc, myPlayer);
        this.speedY = -1.25;
        this.lastUpdateTime = Core.getLogicModule().getTime();
        this.x += -35 + Globals.rng(51);
    }

    @Override
    public IngameNumberStack call() {
        this.y += this.speedY;
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastUpdateTime) >= 10) {
            numDisplayed++;
            this.lastUpdateTime = Core.getLogicModule().getTime();
        }
        return this;
    }

    public void addNumber(final int num, final byte type) {
        this.numbers.add(new Number(num, type));
    }

    public boolean canAddNumber() {
        return this.numbers.size() < 15 && Globals.nsToMs(Core.getLogicModule().getTime() - this.startTime) <= 400;
    }

    @Override
    public void draw(final Graphics2D g) {
        float yDelta = 0;
        int xDelta = 0;
        int count = 0;
        for (Number num : numbers) {
            if (count >= numDisplayed) {
                break;
            }
            drawNumber(g, (float) (this.x + xDelta), (float) (this.y + yDelta), num.type, num.number);

            yDelta -= g.getFontMetrics().getHeight() * 0.6;
            xDelta = (xDelta == 10) ? 0 : 10;
            count++;
        }
    }
}
