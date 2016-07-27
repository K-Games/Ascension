package blockfighter.server.entities.mob.boss.ShadowFiend;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import static blockfighter.server.entities.mob.Mob.STATE_STAND;
import blockfighter.server.entities.mob.MobSkill;
import blockfighter.server.entities.proj.Projectile;

public class SkillRaze extends MobSkill {

    public SkillRaze(final LogicModule l) {
        super(l);
        this.skillCode = BossShadowFiend.SKILL_RAZE;
        this.maxCooldown = 1000;
    }

    @Override
    public void updateSkillUse(Mob mob) {
        long duration = Globals.nsToMs(this.room.getTime() - mob.getSkillCastTime());
        if (mob.getSkillCounter() == 0) {
            mob.incrementSkillCounter();
            Projectile proj = new ProjRaze(this.room, mob, mob.getX(), mob.getY(), true);
            Projectile proj2 = new ProjRaze(this.room, mob, mob.getX(), mob.getY(), false);
            this.room.queueAddProj(proj2);
            this.room.queueAddProj(proj);
        }
        if (Globals.hasPastDuration(duration, 1000)) {
            mob.queueMobState(STATE_STAND);
        }
    }
}
