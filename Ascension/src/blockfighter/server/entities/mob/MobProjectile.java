package blockfighter.server.entities.mob;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;

public abstract class MobProjectile extends Projectile {

    public MobProjectile(final LogicModule l) {
        super(l);
    }

    public MobProjectile(final LogicModule l, final Mob o, final double x, final double y, final int duration) {
        super(l, o, x, y, duration);
    }

    public MobProjectile(final LogicModule l, final Mob o) {
        super(l, o, 0, 0, 0);
    }

    @Override
    public void update() {
        if (this.hitbox[0] == null) {
            return;
        }

        this.room.getPlayers().entrySet().forEach((pEntry) -> {
            final Player p = pEntry.getValue();
            if (p != getOwner() && !this.pHit.containsKey(p.getKey()) && !p.isInvulnerable() && p.intersectHitbox(this.hitbox[0])) {
                this.playerQueue.add(p);
                this.pHit.put(p.getKey(), p);
                queueEffect(this);
            }
        });
    }
}
