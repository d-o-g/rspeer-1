import net.miginfocom.swing.MigLayout;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.ui.component.BotTitlePane;
import org.rspeer.ui.skin.BotLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Spencer on 13/06/2018.
 */
public class InjectorTestSample {

    static int x = 0;
    static Object[] test;

    private int field;
    static long timeOfClick = 0;
    static long timeOfPreviousClick = 0;

    void storelvartest() {
        long var21 = (timeOfClick * -6248133867474392459L - -5290188514648997903L * timeOfPreviousClick) / 50L;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    static int xplus1() {
        return x + 1;
    }

    static int xpreincrement() {
        return x++;
    }

    static int xpostincrement() {
        return ++x;
    }

    static  int[] arr = new int[69];

    void skills() {
        int var0 = 0;
        int var1 = 0;

        arr[var0] = var1 + 2;
    }

    static void browse(String lvar) {
        try {
            Desktop.getDesktop().browse(new URI(lvar));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static RSClient client;

    public static void test(int var0, int var1, int var2, int var3) {
        if (client.isForcingInteraction()) {
            var0 = client.getForcedAction().getSecondaryArg();
            var1 = client.getForcedAction().getTertiaryArg();
            var2 = client.getForcedAction().getOpcode();
            var3 = client.getForcedAction().getPrimaryArg();
        }
    }

    InjectorTestSample(int var1, int var2, int var3, int var4, int var5, int var6, int var7, Object var8) {
        if (var7 != -1) {
           // var7 = Game.getClient().getEventMediator().replaceDynamicObjectAnimation(var1, var7);
        }
    }

    Indexed[][] test2d;

    void testIndex() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                test2d[i][j].set(j);
            }
        }
    }

    class Indexed {
        int index;

        void set(int index) {
            this.index = index;
        }

        int get() {
            return index;
        }
    }
}
