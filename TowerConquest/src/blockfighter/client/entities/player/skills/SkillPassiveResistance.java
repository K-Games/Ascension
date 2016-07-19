package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveResistance extends Skill {

    public SkillPassiveResistance() {
        this.skillName = "Resistance";
        this.skillCode = PASSIVE_RESIST;
        this.maxCooldown = 35000;
        this.icon = Globals.SKILL_ICON[PASSIVE_RESIST];
    }

    @Override
    public double getMaxCooldown() {
        return this.maxCooldown - (1000 * this.level);
    }

    @Override
    public void setCooldown() {
        super.setCooldown();
        reduceCooldown(1000 * this.level);
    }

    @Override
    public void drawInfo(final Graphics2D g, final int x, final int y) {
        final int boxHeight = (this.level < 30) ? 230 : 185, boxWidth = 370;
        int drawX = x, drawY = y;
        if (drawY + boxHeight > 700) {
            drawY = 700 - boxHeight;
        }

        if (drawX + 30 + boxWidth > 1240) {
            drawX = 1240 - boxWidth;
        }
        g.setColor(new Color(30, 30, 30, 185));
        g.fillRect(drawX, drawY, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, boxWidth, boxHeight);
        g.drawRect(drawX + 1, drawY + 1, boxWidth - 2, boxHeight - 2);
        g.drawImage(this.icon, drawX + 10, drawY + 10, null);
        g.setColor(Color.WHITE);
        g.setFont(Globals.ARIAL_18PT);
        g.drawString(getSkillName(), drawX + 80, drawY + 30);
        g.setFont(Globals.ARIAL_15PT);
        g.drawString("Level: " + this.level, drawX + 80, drawY + 50);
        g.drawString("Cooldown: " + (this.maxCooldown / 1000 - this.level) + " Seconds", drawX + 80, drawY + 70);

        g.drawString("When taking damage over 25% of your HP in 2", drawX + 10, drawY + 90);
        g.drawString("seconds, block all damage for 2 seconds.", drawX + 10, drawY + 110);

        g.setColor(new Color(255, 190, 0));
        g.drawString("Assign this passive to a hotkey to gain its effects.", drawX + 10, drawY + 130);

        g.setColor(Color.WHITE);
        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 155);
        g.drawString("Reduce cooldown by " + this.level + ((this.level > 1) ? " seconds." : " second."), drawX + 10, drawY + 175);
        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 200);
            g.drawString("Reduce cooldown by " + (this.level + 1) + (((this.level + 1) > 1) ? " seconds." : " second."), drawX + 10,
                    drawY + 220);
        }
    }
}
