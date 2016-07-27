package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

public class SkillPassiveShieldMastery extends Skill {

    public SkillPassiveShieldMastery() {
        this.skillName = "Defender Mastery";
        this.skillCode = PASSIVE_SHIELDMASTERY;
        this.icon = Globals.SKILL_ICON[PASSIVE_SHIELDMASTERY];
    }

    @Override
    public void drawInfo(final Graphics2D g, final int x, final int y) {
        final int boxHeight = (this.level < 30) ? 270 : 205, boxWidth = 365;
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

        g.drawString("When equipped with a Sword and Shield you deal", drawX + 10, drawY + 90);
        g.drawString("additional damage and take reduced damage.", drawX + 10, drawY + 110);

        g.setColor(new Color(255, 190, 0));
        g.drawString("Assign this passive to a hotkey to gain its effects.", drawX + 10, drawY + 130);

        g.setColor(Color.WHITE);
        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 155);
        g.drawString("Deal additional " + this.df.format(9 + this.level * 0.2) + "% damage.", drawX + 10, drawY + 175);
        g.drawString("Take " + this.df.format(5 + this.level * 0.5) + "% reduced damage.", drawX + 10, drawY + 195);

        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 220);
            g.drawString("Deal additional " + this.df.format(9 + (this.level + 1) * 0.2) + "% damage.", drawX + 10, drawY + 240);
            g.drawString("Take " + this.df.format(5 + (this.level + 1) * 0.5) + "% reduced damage.", drawX + 10, drawY + 260);
        }
    }

}
