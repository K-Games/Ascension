package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.player.skills.bow.SkillBowFrost;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjBowFrost extends Projectile {

    private double speedX = 0;
    private long lastMoveTime = 0;
    private final boolean isSecondary;

    public ProjBowFrost(final LogicModule l, final Player o, final double x, final double y, final boolean isSec) {
        super(l, o, x, y, 500);
        this.isSecondary = isSec;
        this.hitbox = new Rectangle2D.Double[1];
        lastMoveTime = logic.getTime();
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x, this.y - 150, 180, 150);
            speedX = 180;
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 180, this.y - 150, 180, 150);
            speedX = -180;
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.BOW_FROST).getBaseValue();
        double multValue = owner.getSkill(Globals.BOW_FROST).getMultValue();
        double damage = (!this.isSecondary)
                ? (owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.BOW_FROST)))
                : owner.rollDamage() * owner.getSkill(Globals.BOW_FROST).getCustomValue(SkillBowFrost.CUSTOM_DATA_HEADERS[3]);
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, true));
        target.queueBuff(new BuffKnockback(this.logic, 200, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -4, owner, target));
        if (!this.isSecondary) {
            Skill skill = owner.getSkill(Globals.BOW_FROST);
            double stunDuration = (skill.isMaxed()) ? skill.getCustomValue(SkillBowFrost.CUSTOM_DATA_HEADERS[1]) : skill.getCustomValue(SkillBowFrost.CUSTOM_DATA_HEADERS[0]);
            target.queueBuff(new BuffStun(this.logic, (int) stunDuration));
        }
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, true));
        if (!this.isSecondary) {
            Skill skill = owner.getSkill(Globals.BOW_FROST);
            double stunDuration = (skill.isMaxed()) ? skill.getCustomValue(SkillBowFrost.CUSTOM_DATA_HEADERS[1]) : skill.getCustomValue(SkillBowFrost.CUSTOM_DATA_HEADERS[0]);
            target.queueBuff(new BuffStun(this.logic, (int) stunDuration));
        }
    }

    @Override
    public void update() {
        if (logic.getTime() - lastMoveTime >= Globals.msToNs(100)) {
            this.x += this.speedX;
            this.hitbox[0].x += this.speedX;
            lastMoveTime = logic.getTime();
        }
        super.update();
    }
}
